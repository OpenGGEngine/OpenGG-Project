
package com.opengg.core.objloader.parser;

import java.io.BufferedReader;
import java.io.IOException;

import com.opengg.core.objloader.common.IFastFloat;
import com.opengg.core.objloader.common.MTLLimits;
import com.opengg.core.exceptions.WFCorruptException;
import com.opengg.core.exceptions.WFException;
import com.opengg.core.objloader.scanner.IMTLScanner;
import com.opengg.core.objloader.scanner.IMTLScannerHandler;
import com.opengg.core.objloader.scanner.MTLLimitingScannerHandler;
import com.opengg.core.objloader.scanner.MTLScanner;

/**
 * Internal class that helps in the MTL resource
 * parsing.
 * 
 *
 */
class MTLParseRunner implements IMTLScannerHandler {
	
	private MTLLibrary library;
	private MTLMaterial currentMaterial;
	
	public MTLParseRunner() {
		super();
	}
	
	public MTLLibrary run(BufferedReader reader, MTLLimits limits) throws WFException, IOException {
		currentMaterial = null;
		library = new MTLLibrary();
		final IMTLScanner scanner = new MTLScanner();
		if (limits == null) {
			scanner.scan(reader, this);
		} else {
			scanner.scan(reader, new MTLLimitingScannerHandler(this, limits));
		}
		return library;
	}

	@Override
	public void onComment(String comment) throws WFException {
	}

	@Override
	public void onMaterial(String name) throws WFException {
		currentMaterial = new MTLMaterial(name);
		library.getMaterials().add(currentMaterial);
	}

	@Override
	public void onAmbientColorRGB(IFastFloat r, IFastFloat g, IFastFloat b) throws WFException {
		assertCurrentMaterial();
		currentMaterial.getAmbientColor().r = r.get();
		currentMaterial.getAmbientColor().g = g.get();
		currentMaterial.getAmbientColor().b = b.get();
	}


	@Override
	public void onDiffuseColorRGB(IFastFloat r, IFastFloat g, IFastFloat b) throws WFException {
		assertCurrentMaterial();
		currentMaterial.getDiffuseColor().r = r.get();
		currentMaterial.getDiffuseColor().g = g.get();
		currentMaterial.getDiffuseColor().b = b.get();
	}

	@Override
	public void onSpecularColorRGB(IFastFloat r, IFastFloat g, IFastFloat b) throws WFException {
		assertCurrentMaterial();
		currentMaterial.getSpecularColor().r = r.get();
		currentMaterial.getSpecularColor().g = g.get();
		currentMaterial.getSpecularColor().b = b.get();
	}

	@Override
	public void onTransmissionColorRGB(IFastFloat r, IFastFloat g, IFastFloat b) throws WFException {
		assertCurrentMaterial();
		currentMaterial.getTransmissionColor().r = r.get();
		currentMaterial.getTransmissionColor().g = g.get();
		currentMaterial.getTransmissionColor().b = b.get();
	}

	@Override
	public void onDissolve(IFastFloat amount) throws WFException {
		assertCurrentMaterial();
		currentMaterial.setDissolve(amount.get());
	}

	@Override
	public void onSpecularExponent(IFastFloat amount) throws WFException {
		assertCurrentMaterial();
		currentMaterial.setSpecularExponent(amount.get());
	}

	@Override
	public void onAmbientTexture(String filename) throws WFException {
		assertCurrentMaterial();
		currentMaterial.setAmbientTexture(filename);
	}

	@Override
	public void onDiffuseTexture(String filename) throws WFException {
		assertCurrentMaterial();
		currentMaterial.setDiffuseTexture(filename);
	}

	@Override
	public void onSpecularTexture(String filename) throws WFException {
		assertCurrentMaterial();
		currentMaterial.setSpecularTexture(filename);
	}

	@Override
	public void onSpecularExponentTexture(String filename) throws WFException {
		assertCurrentMaterial();
		currentMaterial.setSpecularExponentTexture(filename);
	}

	@Override
	public void onDissolveTexture(String filename) throws WFException {
		assertCurrentMaterial();
		currentMaterial.setDissolveTexture(filename);
	}

	private void assertCurrentMaterial() throws WFCorruptException {
		if (currentMaterial == null) {
			throw new WFCorruptException("Material name has not been defined.");
		}
	}
	
}
