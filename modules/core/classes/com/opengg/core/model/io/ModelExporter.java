package com.opengg.core.model.io;

import com.opengg.core.console.GGConsole;
import com.opengg.core.model.Material;
import com.opengg.core.model.Mesh;
import com.opengg.core.model.Model;
import com.opengg.core.render.texture.TextureData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModelExporter {

    public static void exportModel(Model model, Path directory, String name, boolean writeMaterial){
        var meshBuilder = new StringBuilder();
        meshBuilder.append("# Autogenerated by the BrickBench mesh exporter\n");
        meshBuilder.append("mtllib ").append(name).append(".mtl").append("\n");

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }


        var materialBuilder = new StringBuilder();
        materialBuilder.append("# Autogenerated by the BrickBench mesh exporter\n");

        int lastWrittenVertex = 1;

        var writtenMaterials = new ArrayList<Material>();
        for(var mesh : model.getMeshes()){
            if(writeMaterial){
                if(!writtenMaterials.contains(mesh.getMaterial())){
                    writeMaterial(mesh.getMaterial(), materialBuilder, directory);
                }

                meshBuilder.append("usemtl ").append(mesh.getMaterial().name).append("\n\n");
            }

            lastWrittenVertex += writeMesh(mesh, meshBuilder, lastWrittenVertex);
        }

        try {
            Files.writeString(directory.resolve(name + ".obj"), meshBuilder.toString());

            if(writeMaterial){
                Files.writeString(directory.resolve(name + ".mtl"), materialBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int writeMesh(Mesh mesh, StringBuilder strBuilder, int lastVert) {
        record Face(int v1, int v2, int v3) {}

        List<Face> faces = new ArrayList<>();

        boolean invert = true;
        mesh.getIndexBuffer().rewind();

        while(mesh.getIndexBuffer().remaining() > 0) {
            if(mesh.isTriStrip() && faces.size() > 0){
                int vec1 = faces.get(faces.size()-1).v2;
                int vec2 = faces.get(faces.size()-1).v3;
                int vec3 = mesh.getIndexBuffer().get();

                if(vec1 != vec2 && vec2 != vec3 && vec3 != vec1){
                    faces.add(new Face(vec1, vec2, vec3));
                }
            }else{
                int vec1 = mesh.getIndexBuffer().get();
                int vec2 = mesh.getIndexBuffer().get();
                int vec3 = mesh.getIndexBuffer().get();

                faces.add(new Face(vec1, vec2, vec3));
            }

            invert = !invert;
        }

        mesh.getIndexBuffer().rewind();

        for (var v : mesh.getVertices()) {
            strBuilder.append("v ").append(" ")
                    .append(String.format("%f", v.position.x)).append(" ")
                    .append(String.format("%f", v.position.y)).append(" ")
                    .append(String.format("%f", v.position.z)).append("\n");

            strBuilder.append("vn ").append(" ")
                    .append(String.format("%f", v.normal.x)).append(" ")
                    .append(String.format("%f", v.normal.y)).append(" ")
                    .append(String.format("%f", v.normal.z)).append("\n");

            strBuilder.append("vt ").append(" ")
                    .append(String.format("%f", v.uvs.x)).append(" ")
                    .append(String.format("%f", v.uvs.y)).append("\n");
        }

        strBuilder.append("\n");

        for (var f : faces) {
            strBuilder.append("f ").append(" ")
                    .append(f.v1 + lastVert).append("/").append(f.v1 + lastVert).append(" ").append(f.v1 + lastVert).append(" ")
                    .append(f.v2 + lastVert).append("/").append(f.v2 + lastVert).append(" ").append(f.v2 + lastVert).append(" ")
                    .append(f.v3 + lastVert).append("/").append(f.v3 + lastVert).append(" ").append(f.v3 + lastVert).append("\n");
        }

        return mesh.getVertices().size();
    }

    public static void exportTexture(TextureData texture, Path path) {
        try {
            Files.createDirectories(path.getParent());
            FileOutputStream fs = new FileOutputStream(path.toFile());
            fs.getChannel().write((ByteBuffer) texture.originalBuffer);
            fs.close();
        } catch (IOException e) {
            GGConsole.warn("Failed to export texture " + path);
            GGConsole.exception(e);
        }

        texture.originalBuffer.rewind();
    }

    private static void writeMaterial(Material material, StringBuilder strBuilder, Path directory){
        strBuilder.append("\nnewmtl ").append(material.name).append("\n\n");

        strBuilder.append("Ka ")
                .append(material.kd.x).append(" ")
                .append(material.kd.y).append(" ")
                .append(material.kd.z).append("\n");

        strBuilder.append("Kd ")
                .append(material.kd.x).append(" ")
                .append(material.kd.y).append(" ")
                .append(material.kd.z).append("\n\n");

        strBuilder.append("d ").append(material.dFactor > 0 ? material.dFactor : 1f).append("\n\n");

        if(!material.mapKdFilename.isEmpty()) {
            strBuilder.append("map_Kd ").append(material.mapKdFilename).append("\n");

            System.out.println(material.mapKdFilename);

            var fullTextureFile = directory.resolve(material.mapKdFilename);
            exportTexture(material.mapKd.getData().get(0), fullTextureFile);
        }

        if(!material.mapNsFilename.isEmpty()) {
            strBuilder.append("bump ").append(material.mapKdFilename).append("\n");

            var fullTextureFile = directory.resolve(material.mapKdFilename);
            exportTexture(material.bumpMap.getData().get(0), fullTextureFile);
        }

        strBuilder.append("\n");

        strBuilder.append("illum 0\n");
    }
}