

package com.opengg.core.io.objloader.common;

import com.opengg.core.exceptions.WFSizeException;

/**
 * The {@link OBJLimits} class can be used to
 * specify parsing limits for OBJ resources.
 * <p>
 * Properly configuring an instance of this class and 
 * passing it to parsers allows one to prevent 
 * {@link OutOfMemoryError} errors. 
 * 
 *
 */
public class OBJLimits {
	
	public static final int DEFAULT_MAX_COUNT = 65536;

	/**
	 * Specifies the maximum number of comments that can be
	 * parsed before an {@link WFSizeException} is thrown.
	 * <p>
	 * By default this value is equal to {@value #DEFAULT_MAX_COUNT}.
	 */
	public int maxCommentCount = DEFAULT_MAX_COUNT;
	
	/**
	 * Specifies the maximum number of vertices that can be 
	 * parsed before an {@link WFSizeException} is thrown.
	 * <p>
	 * By default this value is equal to {@value #DEFAULT_MAX_COUNT}.
	 */
	public int maxVertexCount = DEFAULT_MAX_COUNT;
	
	/**
	 * Specifies the maximum number of texture coordinates that
	 * can be parsed before an {@link WFSizeException} is thrown.
	 * <p>
	 * By default this value is equal to {@value #DEFAULT_MAX_COUNT}.
	 */
	public int maxTexCoordCount = DEFAULT_MAX_COUNT;
	
	/**
	 * Specifies the maximum number of normals that can be parsed
	 * before an {@link WFSizeException} is thrown.
	 * <p>
	 * By default this value is equal to {@value #DEFAULT_MAX_COUNT}.
	 */
	public int maxNormalCount = DEFAULT_MAX_COUNT;
	
	/**
	 * Specifies the maximum number of objects that can be
	 * parsed before an {@link WFSizeException} is thrown.
	 * <p>
	 * By default this value is equal to {@value #DEFAULT_MAX_COUNT}.
	 */
	public int maxObjectCount = DEFAULT_MAX_COUNT;
	
	/**
	 * Specifies the maximum number of faces that can be
	 * parsed before an {@link WFSizeException} is thrown.
	 * <p>
	 * By default this value is equal to {@value #DEFAULT_MAX_COUNT}.
	 */
	public int maxFaceCount = DEFAULT_MAX_COUNT;
	
	/**
	 * Specifies the maximum number of data references that
	 * can be parsed before an {@link WFSizeException} is thrown.
	 * <p>
	 * By default this value is equal to {@value #DEFAULT_MAX_COUNT}.
	 */
	public int maxDataReferenceCount = DEFAULT_MAX_COUNT;
	
	/**
	 * Specifies the maximum number of material libraries that
	 * can be parsed before an {@link WFSizeException} is thrown.
	 * <p>
	 * By default this value is equal to {@value #DEFAULT_MAX_COUNT}.
	 */
	public int maxMaterialLibraryCount = DEFAULT_MAX_COUNT;
	
	/**
	 * Specifies the maximum number of material references that
	 * can be parsed before an {@link WFSizeException} is thrown.
	 * <p>
	 * By default this value is equal to {@value #DEFAULT_MAX_COUNT}.
	 */
	public int maxMaterialReferenceCount = DEFAULT_MAX_COUNT;
	
	/**
	 * Creates a new instance of {@link OBJLimits}.
	 */
	public OBJLimits() {
		super();
	}
	
}
