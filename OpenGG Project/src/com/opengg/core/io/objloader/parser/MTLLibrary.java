
package com.opengg.core.io.objloader.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a material library.
 * <p>
 * A material library can be though of as a
 * single MTL resource which can contain information
 * on multiple materials.
 * 
 *
 */
public class MTLLibrary {
	
	private final List<MTLMaterial> materials = new ArrayList<MTLMaterial>();
	
	/**
	 * Creates a new {@link MTLLibrary} instance.
	 */
	public MTLLibrary() {
		super();
	}
	
	/**
	 * Returns a list of materials in this library.
	 * @return non-null writable list of {@link MTLMaterial} 
	 * instances.
	 */
	public List<MTLMaterial> getMaterials() {
		return materials;
	}
	
	/**
	 * A helper method that returns the material in this library
	 * with the specified name.
	 * @param name name of the requested material
	 * @return an instance of {@link MTLMaterial}, or <code>null</code>
	 * if the material could not be found.
	 */
	public MTLMaterial getMaterial(String name) {
		for (MTLMaterial material : materials) {
			if (name.equals(material.getName())) {
				return material;
			}
		}
		return null;
	}

}
