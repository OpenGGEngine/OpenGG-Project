package com.opengg.core.model.io;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.*;
import com.opengg.core.model.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssimpModelLoader {

    private static final int NUM_WEIGHTS = 4;

    public static Model loadModel(String path) throws IOException {
        String name = path.substring(Math.max(path.lastIndexOf("\\"), path.lastIndexOf("/"))+1, path.lastIndexOf("."));

        File f = new File(path);
        AIScene scene = Assimp.aiImportFile(f.toString(),
                Assimp.aiProcess_GenSmoothNormals|Assimp.aiProcess_Triangulate|Assimp.aiProcess_CalcTangentSpace);
        GGConsole.log("Loading " + f.getName() + " with " +scene.mNumMeshes() + " meshes and " + scene.mNumAnimations() + " animations.");

        GGNode rootNode = recurNode(scene.mRootNode());

        //Load Materials
        ArrayList<Material> materials = new ArrayList<>(scene.mNumMaterials());
        if(scene.mNumMaterials() > 0){
            for(int i = 0;i<scene.mNumMaterials();i++){
                Material mat2 = processMaterial(AIMaterial.create(scene.mMaterials().get(i)));
                mat2.texpath = f.getParent() + "\\tex\\";
                materials.add(mat2);
            }
        }

        PointerBuffer pMeshes = scene.mMeshes();
        ArrayList<Mesh> meshes = new ArrayList<>(scene.mNumMeshes());

        boolean animationsEnabled = false;
        boolean generateHulls = false;
        boolean generatedTangent = false;

        for(int i = 0;i<pMeshes.capacity();i++){
            AIMesh mesh = AIMesh.create(pMeshes.get());

            Vector3f positions;
            Vector3f normals;
            Vector3f tangents;
            Vector2f uvs;

            int[] indices = new int[mesh.mNumFaces() * 3];
            GGBone[] bones = new GGBone[mesh.mNumBones()];

            boolean hasTangents = mesh.mTangents() == null;
            generatedTangent = !hasTangents;
            boolean hasNormal = mesh.mNormals() == null;
            boolean hasUVs = mesh.mTextureCoords(0) == null;

            ArrayList<GGVertex> vertices = new ArrayList<>(mesh.mNumVertices());
            //Load Mesh VBO Data
            for(int i2 = 0;i2<mesh.mNumVertices();i2++){

                positions = assimpToV3(mesh.mVertices().get(i2));
                normals =  hasNormal ? new Vector3f(1) : assimpToV3(mesh.mNormals().get(i2));
                tangents = hasTangents ? new Vector3f(1) : assimpToV3(mesh.mTangents().get(i2));
                uvs = hasUVs ? new Vector2f(1) : assimpToV2(mesh.mTextureCoords(0).get(i2));

                vertices.add(new GGVertex(positions,normals,tangents,uvs));

            }

            //Load animation mesh data
            if(!animationsEnabled) {
                Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();

                for (int i3 = 0; i3 < mesh.mNumBones(); i3++) {
                    AIBone bone = AIBone.create(mesh.mBones().get(i3));
                    bones[i3] = new GGBone(bone.mName().dataString(), assimpToMat4(bone.mOffsetMatrix()));

                    int numWeights = bone.mNumWeights();
                    numWeights = NUM_WEIGHTS;
                    AIVertexWeight.Buffer aiWeights = bone.mWeights();
                    for (int j = 0; j < numWeights; j++) {
                        AIVertexWeight aiWeight = aiWeights.get(j);
                        VertexWeight vw = new VertexWeight(i3, aiWeight.mVertexId(),
                                aiWeight.mWeight());
                        List<VertexWeight> vertexWeightList = weightSet.computeIfAbsent(vw.getVertexId(), k -> new ArrayList<>());
                        vertexWeightList.add(vw);
                    }
                }
                for (int i6 = 0; i6 < mesh.mNumVertices(); i6++) {

                    List<VertexWeight> vertexWeightList = weightSet.get(i6);

                    if(vertexWeightList == null || vertexWeightList.size() < NUM_WEIGHTS){
                        vertices.get(i6).jointIndices = new Vector4f(0,0,0,0);
                        vertices.get(i6).weights = new Vector4f(0,0,0,0);
                    }else {
                        VertexWeight vw = vertexWeightList.get(0);
                        VertexWeight vw2 = vertexWeightList.get(1);
                        VertexWeight vw3 = vertexWeightList.get(2);
                        VertexWeight vw4 = vertexWeightList.get(3);

                        vertices.get(i6).jointIndices = new Vector4f(vw.getBoneId(), vw2.getBoneId(), vw3.getBoneId(), vw4.getBoneId());
                        vertices.get(i6).weights = new Vector4f(vw.getWeight(), vw2.getWeight(), vw3.getWeight(), vw4.getWeight());
                    }
                }
            }

            //Load Mesh Index Data
            for(int i2 = 0;i2<mesh.mFaces().capacity();i2++){
                AIFace face = mesh.mFaces().get(i2);
                indices[i2*3] = face.mIndices().get(0);
                indices[i2*3+1] = face.mIndices().get(1);
                indices[i2*3+2] = face.mIndices().get(2);
            }

            Mesh gmesh  = new Mesh(vertices,indices,animationsEnabled);

            if(scene.mNumMaterials() > 0){
                gmesh.setMaterial(materials.get(mesh.mMaterialIndex()));
                gmesh.matIndex = mesh.mMaterialIndex();
            }
            gmesh.setBones(bones);

            meshes.add(gmesh);

        }
        GGConsole.log("Loaded model: " + f.getName());
        Model model = new Model(meshes, name);
        String formatConfig ="{";
        if(animationsEnabled){
            formatConfig+="anim_";
        }
        if(generatedTangent){
            formatConfig+="tangent_";
        }
        model.vaoFormat = formatConfig;
        System.out.println(model.vaoFormat);
        if(scene.mNumMaterials() > 0) model.exportConfig |= BMFFile.MATERIAL;

        //Load animations
        if(scene.mNumAnimations() > 0){
            for(int i =0;i<scene.mNumAnimations();i++){
                GGAnimation animation = processAnimation(AIAnimation.create(scene.mAnimations().get(i)));
                model.animations.put(animation.name,animation);
            }
            GGConsole.log("Loaded Animations");
        }

        model.fileLocation = f.getParent();
        model.isAnim = animationsEnabled;
        model.materials = materials;
        model.root = rootNode;
        return model;

    }

    public static Material processMaterial(AIMaterial material){
        AIString s = AIString.malloc();
        aiGetMaterialString(material,AI_MATKEY_NAME,0,0,s);
        Material m =new Material(s.dataString());

        AIColor4D color = AIColor4D.create();

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, path, (IntBuffer
                ) null, null, null, null, null, null);
        m.mapKdFilename = path.dataString();

        path = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, aiTextureType_SHININESS, 0, path, (IntBuffer
                ) null, null, null, null, null, null);
        if(Assimp.aiGetMaterialTextureCount(material,aiTextureType_SHININESS)>0) m.mapNsFilename = path.dataString();

        path = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, aiTextureType_EMISSIVE, 0, path, (IntBuffer
                ) null, null, null, null, null, null);
        if(Assimp.aiGetMaterialTextureCount(material,aiTextureType_EMISSIVE)>0) m.emmFilename = path.dataString();

        Vector3f ambient = Material.DEFAULT_COLOR;
        if (aiGetMaterialColor(material, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color) == 0) {
            ambient = new Vector3f(color.r(), color.g(), color.b());
        }
        Vector3f diffuse = Material.DEFAULT_COLOR;
        if (aiGetMaterialColor(material, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color) == 0) {
            diffuse = new Vector3f(color.r(), color.g(), color.b());
        }
        Vector3f specular = Material.DEFAULT_COLOR;
        if (aiGetMaterialColor(material, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color) == 0) {
            specular = new Vector3f(color.r(), color.g(), color.b());
        }
        float[] temp = new float[1];

        if(aiGetMaterialFloatArray(material,AI_MATKEY_SHININESS,aiTextureType_NONE,0,temp,new int[]{1}) == 0){
            m.nsExponent = temp[0];
        }
        return m;

    }

    public static GGAnimation processAnimation(AIAnimation animation){
        //For the awful people who have empty animation names.
        String name = animation.mName().dataString().equals("")? animation.toString():animation.mName().dataString();
        GGAnimation anim = new GGAnimation(name,animation.mDuration(),animation.mTicksPerSecond());
        //Unused Mesh Animations
        for(int i=0;i<animation.mNumMeshChannels();i++){}
        for(int i = 0;i<animation.mNumChannels();i++){
            AINodeAnim animnode = AINodeAnim.create(animation.mChannels().get(i));
            ArrayList<Tuple<Double,Vector3f>> positionKeys = new ArrayList<>();
            for(int i2 = 0;i2 < animnode.mNumPositionKeys();i2++){
                AIVectorKey v5 =animnode.mPositionKeys().get(i2);
                positionKeys.add(new Tuple<>(v5.mTime(),assimpToV3(v5.mValue())));
            }
            ArrayList<Tuple<Double,Quaternionf>> rotationKeys = new ArrayList<>();
            for(int i2 = 0;i2 < animnode.mNumRotationKeys();i2++){
                AIQuatKey v5 =animnode.mRotationKeys().get(i2);
                rotationKeys.add(new Tuple<>(v5.mTime(),
                        new Quaternionf(v5.mValue().w(),v5.mValue().x(),v5.mValue().y(),v5.mValue().z())));
            }
            ArrayList<Tuple<Double,Vector3f>> scalingKeys = new ArrayList<>();
            for(int i2 = 0;i2 < animnode.mNumScalingKeys();i2++){
                AIVectorKey v5 =animnode.mScalingKeys().get(i2);
                scalingKeys.add(new Tuple<>(v5.mTime(),assimpToV3(v5.mValue())));
            }
            GGAnimation.AnimNode node = new GGAnimation.AnimNode(positionKeys,rotationKeys,scalingKeys,animnode.mNodeName().dataString());
            anim.animdata.put(node.name,node);
        }
        return anim;
    }

    public static GGNode recurNode(AINode node){
        GGNode gnode = new GGNode(node.mName().dataString(),assimpToMat4(node.mTransformation()));
        //String length, String char data (ASCII 1 byte), Matrix data, Num Children
        gnode.byteSize =  4 + gnode.name.length() + (64) + 4;
        for(int i=0;i<node.mNumChildren();i++){
            GGNode cnode = recurNode(AINode.create(node.mChildren().get(i)));
            gnode.byteSize += cnode.byteSize;
            gnode.children.add(cnode);
        }
        return gnode;
    }

    public static Vector3f assimpToV3(AIVector3D a){
        return new Vector3f(a.x(),a.y(),a.z());
    }
    public static Vector2f assimpToV2(AIVector3D a){
        return new Vector2f(a.x(),a.y());
    }
    public static Matrix4f assimpToMat4(AIMatrix4x4 aiMatrix4x4) {
        Matrix4f result = new Matrix4f(aiMatrix4x4.a1(), aiMatrix4x4.b1(), aiMatrix4x4.c1(), aiMatrix4x4.d1()
                , aiMatrix4x4.a2(), aiMatrix4x4.b2(), aiMatrix4x4.c2(), aiMatrix4x4.d2()
                , aiMatrix4x4.a3(), aiMatrix4x4.b3(), aiMatrix4x4.c3(), aiMatrix4x4.d3()
                , aiMatrix4x4.a4(), aiMatrix4x4.b4(), aiMatrix4x4.c4(), aiMatrix4x4.d4());

        return result;
    }
}
