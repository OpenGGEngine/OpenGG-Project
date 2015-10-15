

package com.opengg.core.io.objloader.common;

/**
 * Internal implementation of the {@link IFastInt}
 * interface.
 * 

 * 
 */
public class FastInt implements IFastInt {
	
	private int value;
	
	public FastInt() {
		super();
	}
	
	public void set(int value) {
		this.value = value;
	}

	@Override
	public int get() {
		return value;
	}

}
