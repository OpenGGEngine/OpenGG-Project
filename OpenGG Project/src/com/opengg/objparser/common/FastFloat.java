
package com.opengg.objparser.common;

/**
 * Internal implementation of the {@link IFastFloat}
 * interface.
 * 
 * 
 */
public class FastFloat implements IFastFloat {
	
	private float value;
	
	public FastFloat() {
		super();
	}
	
	public void set(float value) {
		this.value = value;
	}

	@Override
	public float get() {
		return value;
	}

}
