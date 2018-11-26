package com.opengg.core.model.ggmodel.io;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;
import com.opengg.core.model.Material;
import com.opengg.core.model.ggmodel.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssimpModelLoader {

    private static final int NUM_WEIGHTS = 4;

    public static GGModel loadModel(String path) throws IOException {
        File f = new File(path);
        AIScene scene = Assimp.aiImportFile(f.toString(),Assimp.aiProcess_GenSmoothNormals|Assimp.aiProcess_Triangulate);
        GGConsole.log("Loading " + f.getName() + " with " +scene.mNumMeshes() + " meshes and " + scene.mNumAnimations() + " animations.");

        //Load animations
        if(scene.mNumAnimations() > 0){
            for(int i =0;i<scene.mNumAnimations();i++){
                AIAnimation anim = AIAnimation.create(scene.mAnimations().get(i));
                anim.mChannels().get();
                GGAnimation animation = processAnimation(anim);
                System.out.println(animation.name);
            }
            GGConsole.log("Loaded Animations");
        }

        //Load Materials
        ArrayList<Material> materials = new ArrayList<>();
        if(scene.mNumMaterials() > 0){
            for(int i = 0;i<scene.mNumMaterials();i++){
                AIMaterial mat = AIMaterial.create(scene.mMaterials().get(i));
                Material mat2 = processMaterial(mat);
                mat2.texpath = f.getParent() + "\\tex\\";
                materials.add(mat2);
            }
        }

        PointerBuffer pMeshes = scene.mMeshes();

        ArrayList<GGMesh> meshes = new ArrayList<>();

        boolean animationsEnabled = false;
        boolean generateHulls = true;

        for(int i = 0;i<pMeshes.capacity();i++){
            AIMesh mesh = AIMesh.create(pMeshes.get());

            Vector3f positions;
            Vector3f normals;
            Vector3f tangents;
            Vector2f uvs;

            ArrayList<GGVertex> vertices = new ArrayList<>();

            int[] indices = new int[mesh.mNumFaces() * 3];

            GGBone[] bones = new GGBone[mesh.mNumBones()];

            boolean hasTangents = mesh.mTangents() == null;
            boolean hasNormal = mesh.mNormals() == null;
            boolean hasUVs = mesh.mTextureCoords(0) == null;

            //Load Mesh VBO Data
            for(int i2 = 0;i2<mesh.mNumVertices();i2++){

                positions = assimpToV3(mesh.mVertices().get(i2));

                normals =  hasNormal ? new Vector3f(1,1,1) : assimpToV3(mesh.mNormals().get(i2));
                tangents = hasTangents ? new Vector3f(1,1,1) : assimpToV3(mesh.mTangents().get(i2));

                uvs = hasUVs ? new Vector2f(1,1) : assimpToV2(mesh.mTextureCoords(0).get(i2));

                vertices.add(new GGVertex(positions,normals,tangents,uvs));

            }
            //Load animation mesh data
            if(animationsEnabled) {
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
                        List<VertexWeight> vertexWeightList = weightSet.get(vw.getVertexId());
                        if (vertexWeightList == null) {
                            vertexWeightList = new ArrayList<>();
                            weightSet.put(vw.getVertexId(), vertexWeightList);
                        }
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

            GGMesh gmesh  = new GGMesh(vertices,indices,animationsEnabled);

            if(scene.mNumMaterials() > 0){
                gmesh.main = materials.get(mesh.mMaterialIndex());
                gmesh.matIndex = mesh.mMaterialIndex();
            }

            gmesh.bones = bones;

            meshes.add(gmesh);

        }
        GGConsole.log("Loaded model: " + f.getName());
        GGModel model = new GGModel(meshes);
        model.isAnim = animationsEnabled;
        model.materials = materials;
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
        int result = aiGetMaterialColor(material, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color);
        if (result == 0) {
            ambient = new Vector3f(color.r(), color.g(), color.b());
        }
        Vector3f diffuse = Material.DEFAULT_COLOR;
        result = aiGetMaterialColor(material, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
        if (result == 0) {
            diffuse = new Vector3f(color.r(), color.g(), color.b());
        }
        Vector3f specular = Material.DEFAULT_COLOR;
        result = aiGetMaterialColor(material, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color);
        if (result == 0) {
            specular = new Vector3f(color.r(), color.g(), color.b());
        }
        float[] temp = new float[1];

        result = aiGetMaterialFloatArray(material,AI_MATKEY_SHININESS,aiTextureType_NONE,0,temp,new int[]{1});

        if(result == 0){
            m.nsExponent = temp[0];
        }
        return m;

    }

    public static GGAnimation processAnimation(AIAnimation animation){
        for(int i = 0;i<animation.mNumChannels();i++){
            //animation.mChannels().get(i)
        }
        //animation.mChannels()
        return new GGAnimation(animation.mName().dataString(),animation.mDuration(),animation.mTicksPerSecond());
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
