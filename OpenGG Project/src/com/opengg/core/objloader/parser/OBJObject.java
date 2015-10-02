

package com.opengg.core.objloader.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single object in an OBJ model.
 * <p>
 * Basically, an {@link OBJObject} has a name and a 
 * collection of meshes.
 * <br>
 * Even though the OBJ specification does not include the
 * concept of meshes, the idea is to have a per-material 
 * face separation.
 * @see OBJMesh
 * 
 * 
 */
public class OBJObject {

    private final List<OBJMesh> meshes = new ArrayList<OBJMesh>();
    private String name;

    /**
     * Creates a new {@link OBJObject}.
     * <p>
     * By default this object does not have a name or
     * any meshes assigned.
     * <br>
     * It is generally wrong to have an object without a
     * name so the user should assure to always set one.
     */
    public OBJObject() {
        super();
    }

    /**
     * Creates a new {@link OBJObject} with the specified name.
     * @param name name of the object.
     */
    public OBJObject(String name) {
        this.name = name;
    }

    /**
     * Sets a new name of this object.
     * <p>
     * Though there is no restriction on the name,
     * it should generally follow the OBJ specification
     * and should not be <code>null</code>.
     * @param name object name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this object.
     * <p>
     * If <code>null</code> is returned, then this object
     * does not have a name. This is generally an invalid
     * object, and parsers should never return objects
     * without names. Users should try to follow the same
     * rule.
     * @return object name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a non-null writable list of meshes that make
     * up the geometry of this object.
     * @return non-null writable list of {@link OBJMesh} 
     * instances.
     */
    public List<OBJMesh> getMeshes() {
        return meshes;
    }

}
