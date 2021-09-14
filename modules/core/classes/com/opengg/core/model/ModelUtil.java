/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.model;

import com.opengg.core.system.Allocator;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.meshoptimizer.MeshOptimizer;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.lwjgl.system.MemoryUtil.memAddress;

/**
 *
 * @author Javier
 */
public class ModelUtil {
    public static Model autoSimplifyToTargetIndexCount(Model model, int targetIndexCount) {
        try (var scope = ResourceScope.newConfinedScope()) {
            var meshes = new ArrayList<Mesh>();
            for (var mesh : model.getMeshes()) {
                if (mesh.getIndexBuffer().capacity() < targetIndexCount) {
                    meshes.add(mesh);
                    continue;
                }

                var indices = mesh.getIndexBuffer();
                indices.rewind();

                if (mesh.isTriStrip()) {
                    var size = MeshOptimizer.meshopt_unstripifyBound(indices.capacity());
                    var newIndices = MemorySegment.allocateNative(size * Integer.BYTES, scope).asByteBuffer().asIntBuffer();
                    var triListSize = MeshOptimizer.meshopt_unstripify(newIndices, indices, 0);

                    indices = newIndices.slice(0, (int) triListSize);
                }


                var vertices = MemorySegment.allocateNative((long) mesh.getVertices().size() * 3 * Float.BYTES, scope).asByteBuffer().asFloatBuffer();
                for(var vertex : mesh.getVertices()) {
                    vertices.put(vertex.position.x).put(vertex.position.y).put(vertex.position.z);
                }
                vertices.rewind();

                var newIndices = MemorySegment.allocateNative(targetIndexCount * Integer.BYTES, scope).asByteBuffer().asIntBuffer();
                var optimizedListSize = MeshOptimizer.nmeshopt_simplifySloppy(memAddress(newIndices), memAddress(indices), indices.remaining(), memAddress(vertices), mesh.getVertices().size(), 12, targetIndexCount);

                if (mesh.isTriStrip()) {
                    var resizedNewIndices = newIndices.slice(0, (int) optimizedListSize);

                    var size = MeshOptimizer.meshopt_stripifyBound(optimizedListSize);
                    var simpifiedStripIndices = MemorySegment.allocateNative(size * Integer.BYTES, scope).asByteBuffer().asIntBuffer();
                    var simplifiedStripSize = MeshOptimizer.meshopt_stripify(simpifiedStripIndices, resizedNewIndices, mesh.getVertices().size(), 0xffffffff);

                    var newIndicesArray = IntStream.range(0, (int) simplifiedStripSize).map(simpifiedStripIndices::get).toArray();
                    var newMesh = new Mesh(mesh.getVertices(), newIndicesArray);
                    newMesh.setMaterial(mesh.getMaterial());
                    newMesh.setTriStrip(true);
                    meshes.add(newMesh);
                }else{
                    var newIndicesArray = IntStream.range(0, (int) optimizedListSize).map(newIndices::get).toArray();
                    var newMesh = new Mesh(mesh.getVertices(), newIndicesArray);
                    newMesh.setMaterial(mesh.getMaterial());

                    meshes.add(newMesh);
                }
            }

            return new Model(meshes, model.getName() + "_simple");
        }
    }
}
