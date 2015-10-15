

package com.opengg.core.io.objloader.scanner;

import com.opengg.core.io.objloader.common.FastInt;
import com.opengg.core.io.objloader.common.IFastInt;
import com.opengg.core.exceptions.WFCorruptException;

/**
 * Internal class that is used to parse face data references.
 *
 * 
 */
class OBJScanDataReference {

	private final FastInt vertexIndex = new FastInt();
	private final FastInt texCoordIndex = new FastInt();
	private final FastInt normalIndex = new FastInt();
	private boolean hasTexCoordIndex = false;
	private boolean hasNormalIndex = false;

    public OBJScanDataReference() {
    	super();
	}

	public void parse(String segment) throws WFCorruptException {
        final String[] references = segment.split("/");
        vertexIndex.set(parseInt(references[0]));
        hasTexCoordIndex = (references.length >= 2) && !references[1].isEmpty();
        if (hasTexCoordIndex) {
        	texCoordIndex.set(parseInt(references[1]));
        }
        hasNormalIndex = (references.length >= 3) && !references[2].isEmpty();
        if (hasNormalIndex) {
        	normalIndex.set(parseInt(references[2]));
        }
	}
	
	public IFastInt getVertexIndex() {
		return vertexIndex;
	}
	
	public IFastInt getTexCoordIndex() {
		return hasTexCoordIndex ? texCoordIndex : null;
	}
	
	public IFastInt getNormalIndex() {
		return hasNormalIndex ? normalIndex : null;
	}

    private static int parseInt(String text) throws WFCorruptException {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new WFCorruptException("Could not parse int value.", ex);
        }
    }

}
