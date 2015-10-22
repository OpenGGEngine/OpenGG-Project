

package com.opengg.core.io.objloader.common;

import com.opengg.core.exceptions.WFSizeException;

/**
 * The {@link MTLLimits} class can be used to
 * specify parsing limits for MTL resources.
 * <p>
 * Properly configuring an instance of this class
 * and passing it to parsers allows one to prevent
 * {@link OutOfMemoryError} errors.
 * 
 *
 */
public class MTLLimits {
	
	private static final int DEFAULT_MAX_COUNT = 65536;
	
	/**
	 * Specifies the maximum number of comments that can be 
	 * parsed before an {@link WFSizeException} is thrown.
	 */
	public int maxCommentCount = DEFAULT_MAX_COUNT;
	
	/**
	 * Specifies the maximum number of materials that can be
	 * parsed before an {@link WFSizeException} is thrown.
	 */
	public int maxMaterialCount = DEFAULT_MAX_COUNT;
	
	/**
	 * Creates a new {@link MTLLimits} instance.
	 */
	public MTLLimits() {
		super();
	}
	
}
