

package com.opengg.core.io.objloader.parser;

import com.opengg.core.Vector3f;
import java.io.BufferedReader;
import java.io.IOException;

import com.opengg.core.io.objloader.common.IFastFloat;
import com.opengg.core.io.objloader.common.IFastInt;
import com.opengg.core.io.objloader.common.OBJLimits;
import com.opengg.core.exceptions.WFCorruptException;
import com.opengg.core.exceptions.WFException;
import com.opengg.core.io.objloader.parser.OBJTexCoord.Type;
import com.opengg.core.io.objloader.scanner.IOBJScanner;
import com.opengg.core.io.objloader.scanner.IOBJScannerHandler;
import com.opengg.core.io.objloader.scanner.OBJLimitingScannerHandler;
import com.opengg.core.io.objloader.scanner.OBJScanner;

/**
 * Internal class that helps in the parsing of OBJ
 * resources.
 * 
 *
 */
class OBJParseRunner implements IOBJScannerHandler {
	
	private OBJModel model;
	private OBJObject currentObject;
	private OBJMesh currentMesh;
	private OBJFace currentFace;
	
	public OBJParseRunner() {
		super();
	}
	
	public OBJModel run(BufferedReader reader, OBJLimits limits) throws WFException, IOException {
		currentFace = null;
		currentMesh = null;
		currentObject = null;
		model = new OBJModel();
		final IOBJScanner scanner = new OBJScanner();
		if (limits == null) {
			scanner.scan(reader, this);
		} else {
			scanner.scan(reader, new OBJLimitingScannerHandler(this, limits));
		}
		return model;
	}

	@Override
	public void onComment(String comment) throws WFException {
	}

	@Override
	public void onVertex(IFastFloat x, IFastFloat y, IFastFloat z, IFastFloat w) throws WFException {
		final Vector3f vertex = new Vector3f(x.get(), y.get(), z.get());
		model.getVertices().add(vertex);
	}

	@Override
	public void onNormal(IFastFloat x, IFastFloat y, IFastFloat z) throws WFException {
		final OBJNormal normal = new OBJNormal(x.get(), y.get(), z.get());
		model.getNormals().add(normal);
	}

	@Override
	public void onTextureCoordinate(IFastFloat u, IFastFloat v, IFastFloat w) throws WFException {
		final OBJTexCoord texCoord = new OBJTexCoord(u.get());
		if (v != null) {
			texCoord.v = v.get();
			texCoord.type = Type.TYPE_2D;
		}
		if (w != null) {
			texCoord.w = w.get();
			texCoord.type = Type.TYPE_3D;
		}
		model.getTexCoords().add(texCoord);
	}

	@Override
	public void onObject(String objectName) throws WFException {
		currentMesh = null;
		currentObject = new OBJObject(objectName);
		model.getObjects().add(currentObject);
	}

	@Override
	public void onFaceBegin() throws WFException {
		assureCurrentMesh();
		currentFace = new OBJFace();
		currentMesh.getFaces().add(currentFace);
	}

	@Override
	public void onFaceEnd() throws WFException {
		if (currentFace.getReferences().size() < 3) {
			throw new WFCorruptException("Face does not have at least three vertices.");
		}
	}

	@Override
	public void onDataReference(IFastInt vertexIndex, IFastInt texCoordIndex, IFastInt normalIndex) throws WFException {
		final OBJDataReference reference = new OBJDataReference();
		reference.vertexIndex = evaluateVertexIndex(vertexIndex);
		reference.texCoordIndex = evaluateTexCoordIndex(texCoordIndex);
		reference.normalIndex = evaluateNormalIndex(normalIndex);
		currentFace.getReferences().add(reference);
	}

	@Override
	public void onMaterialLibrary(String libraryFilename) throws WFException {
		model.getMaterialLibraries().add(libraryFilename);
	}

	@Override
	public void onMaterialReference(String materialName) throws WFException {
		assureCurrentObject();
		currentMesh = new OBJMesh();
		currentMesh.setMaterialName(materialName);
		currentObject.getMeshes().add(currentMesh);
	}
	
	/*
	 * Makes sure there is a current object available
	 * even if one wasn't defined in the resource.
	 */
	private void assureCurrentObject() {
		if (currentObject != null) {
			return;
		}
		currentObject = new OBJObject("Default");
		model.getObjects().add(currentObject);
	}

	/*
	 * Makes sure there is a current mesh available
	 * even if one wasn't defined in the resource.
	 */
	private void assureCurrentMesh() {
		if (currentMesh != null) {
			return;
		}
		assureCurrentObject();
		currentMesh = new OBJMesh();
		currentObject.getMeshes().add(currentMesh);
	}
	
	private int evaluateVertexIndex(IFastInt vertexIndex) {
		if (vertexIndex == null) {
			return OBJDataReference.UNDEFINED_INDEX;
		}
		if (vertexIndex.get() > 0) {
			return (vertexIndex.get() - 1);
		} else {
			return (model.getVertices().size() + vertexIndex.get());
		}
	}

	private int evaluateTexCoordIndex(IFastInt texCoordIndex) {
		if (texCoordIndex == null) {
			return OBJDataReference.UNDEFINED_INDEX;
		}
		if (texCoordIndex.get() > 0) {
			return (texCoordIndex.get() - 1);
		} else {
			return (model.getTexCoords().size() + texCoordIndex.get());
		}
	}

	private int evaluateNormalIndex(IFastInt normalIndex) {
		if (normalIndex == null) {
			return OBJDataReference.UNDEFINED_INDEX;
		}
		if (normalIndex.get() > 0) {
			return (normalIndex.get() - 1);
		} else {
			return (model.getNormals().size() + normalIndex.get());
		}
	}
	
}
