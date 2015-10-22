

package com.opengg.core.io.objloader.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single mesh within an OBJ scene.
 * <p>
 * The OBJ specification does not include the concept of 
 * meshes, but since different faces can have different materials 
 * applied to them, this seemed like a good way to group them.
 * <p>
 * Each mesh contains a list of faces that have the same material 
 * applied to them.
 *
 * 
 */
public class OBJMesh {

    private final List<OBJFace> faces = new ArrayList<OBJFace>();
    private String materialName = null;

    /**
     * Creates a new empty {@link OBJMesh}.
     */
    public OBJMesh() {
        super();
    }

    /**
     * Sets the name of the material that is used
     * by this mesh.
     * @param materialName name of a material or <code>null</code>,
     * if this mesh has no material at all.
     */
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    /**
     * Returns the name of the material used by this mesh.
     * <p>
     * If the returned value is <code>null</code>, then this 
     * mesh uses no material.
     * @return the name of a referenced material or <code>null</code>,
     * if this mesh has no material
     */
    public String getMaterialName() {
        return materialName;
    }

    /**
     * Returns a writable non-null list of faces that 
     * this mesh is comprised of.
     * @return non-null writable list of {@link OBJFace} instances.
     */
    public List<OBJFace> getFaces() {
        return faces;
    }
}
