

package com.opengg.core.io.objloader.parser;

/**
 * The {@link OBJDataReference} structure holds index references 
 * to vertices, normals, etc. that a face uses.
 * <p>
 * A given face would hold at least three instances of such a reference.
 *
 * 
 */
public class OBJDataReference {

    /**
     * This value indicates that an index reference 
     * does not exist.
     * <p>
     * It is better to use the methods of the {@link OBJFace} 
     * class to check whether some type of index is supported 
     * for the given face.
     */
    public static final int UNDEFINED_INDEX = -1;
    
    /**
     * Specifies the vertex index.
     * <p>
     * Vertex indices start from <code>0</code>.
     * <p>
     * Even if the original file contained a negative index, 
     * it is converted to a positive one, starting from zero.
     */
    public int vertexIndex = UNDEFINED_INDEX;
    
    /**
     * Specifies the normal index.
     * <p>
     * Normal indices start from <code>0</code>.
     * <p>
     * Even if the original file contained a negative index, 
     * it is converted to a positive one, starting from zero.
     */
    public int normalIndex = UNDEFINED_INDEX;
    
    /**
     * Specifies the texture coordinate index.
     * <p>
     * Texture coordinate indices start from <code>0</code>.
     * <p>
     * Even if the original file contained a negative index, 
     * it is converted to a positive one, starting from zero.
     */
    public int texCoordIndex = UNDEFINED_INDEX;

    
    /**
     * Creates a new {@link OBJDataReference}.
     * <p>
     * By default all references are invalid (i.e. have a value of
     * {@link #UNDEFINED_INDEX}).
     */
    public OBJDataReference() {
        super();
    }
    
    /**
     * Determines whether this {@link OBJDataReference} contains
     * a vertex reference.
     * @return <code>true</code> if the vertex index is specified,
     * <code>false</code> otherwise.
     */
    public boolean hasVertexIndex() {
    	return (vertexIndex != UNDEFINED_INDEX);
    }
    
    /**
     * Determines whether this {@link OBJDataReference} contains
     * a normal reference.
     * @return <code>true</code> if the normal index is specified,
     * <code>false</code> otherwise.
     */
    public boolean hasNormalIndex() {
    	return (normalIndex != UNDEFINED_INDEX);
    }
    
    /**
     * Determines whether this {@link OBJDataReference} contains
     * a texture coordinate reference.
     * @return <code>true</code> if the texture coordinate index
     * is specified, <code>false</code> otherwise.
     */
    public boolean hasTexCoordIndex() {
    	return (texCoordIndex != UNDEFINED_INDEX);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OBJDataReference)) {
            return false;
        }
        final OBJDataReference other = (OBJDataReference)obj;
        return (vertexIndex == other.vertexIndex)
                && (texCoordIndex == other.texCoordIndex)
                && (normalIndex == other.normalIndex);
    }

    @Override
    public int hashCode() {
        int result = vertexIndex;
        result = result * 31 + texCoordIndex;
        result = result * 31 + normalIndex;
        return result;
    }
}
