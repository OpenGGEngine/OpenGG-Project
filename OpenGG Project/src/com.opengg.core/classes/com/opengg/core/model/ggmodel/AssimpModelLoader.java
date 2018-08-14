package com.opengg.core.model.ggmodel;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Material;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class AssimpModelLoader {
    public static GGModel loadModel(String path) throws IOException {
        File f = new File(path);
        AIScene scene = Assimp.aiImportFile(f.toString(),Assimp.aiProcess_GenSmoothNormals);
        GGConsole.log("Loading " + f.getName() + " with " +scene.mNumMeshes() + " meshes and " + scene.mNumAnimations() + " animations.");

        //Load animations
        if(scene.mNumAnimations() > 0){
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

        for(int i = 0;i<pMeshes.capacity();i++){
            AIMesh mesh = AIMesh.create(pMeshes.get());

            Vector3f[] positions = new Vector3f[mesh.mNumVertices()];
            Vector3f[] normals = new Vector3f[mesh.mNumVertices()];
            Vector3f[] tangents = new Vector3f[mesh.mNumVertices()];
            Vector2f[] uvs = new Vector2f[mesh.mNumVertices()];

            int[] indices = new int[mesh.mNumFaces() * 3];

            GGBone[] bones = new GGBone[mesh.mNumBones()];

            boolean hasTangents = mesh.mTangents() == null;
            boolean hasNormal = mesh.mNormals() == null;
            boolean hasUVs = mesh.mTextureCoords(0) == null;

            //Load Mesh VBO Data
            for(int i2 = 0;i2<mesh.mNumVertices();i2++){

                positions[i2] = assimpToV3(mesh.mVertices().get(i2));

                normals[i2] =  hasNormal ? new Vector3f(1,1,1) : assimpToV3(mesh.mNormals().get(i2));
                tangents[i2] = hasTangents ? new Vector3f(1,1,1) : assimpToV3(mesh.mTangents().get(i2));

                uvs[i2] = hasUVs ? new Vector2f(1,1) : assimpToV2(mesh.mTextureCoords(0).get(i2));

                //Load animation mesh data
                if(mesh.mNumBones() > 0){
                }
                if(mesh.mNumAnimMeshes() > 0){
                }


            }
            //Load Mesh Index Data
            for(int i2 = 0;i2<mesh.mFaces().capacity();i2++){
                AIFace face = mesh.mFaces().get(i2);
                indices[i2*3] = face.mIndices().get(0);
                indices[i2*3+1] = face.mIndices().get(1);
                indices[i2*3+2] = face.mIndices().get(2);
            }

            GGMesh gmesh  = new GGMesh(positions,normals,tangents,uvs,indices);

            if(scene.mNumMaterials() > 0){
                gmesh.main = materials.get(mesh.mMaterialIndex());
                gmesh.matIndex = mesh.mMaterialIndex();
            }

            meshes.add(gmesh);

        }
        GGConsole.log("Loaded model: " + f.getName());
        return new GGModel(meshes);

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
        System.out.println(m.mapKdFilename);

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

        return m;

    }

    public static Vector3f assimpToV3(AIVector3D a){
        return new Vector3f(a.x(),a.y(),a.z());
    }
    public static Vector2f assimpToV2(AIVector3D a){
        return new Vector2f(a.x(),a.y());
    }
}
