
package com.opengg.core.objloader.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.opengg.core.exceptions.WFException;

/**
 * Default implementation of the {@link IOBJScanner}
 * interface.
 * 
 * 
 */
public class OBJScanner implements IOBJScanner {
	
	/**
	 * Creates a new instance of the {@link OBJScanner} class.
	 */
	public OBJScanner() {
		super();
	}

	@Override
	public void scan(InputStream in, IOBJScannerHandler handler) throws WFException, IOException {
        final Reader reader = new InputStreamReader(in);
        scan(new BufferedReader(reader), handler);
	}
	
	@Override
	public void scan(BufferedReader reader, IOBJScannerHandler handler) throws WFException, IOException {
		final OBJScanRunner runner = new OBJScanRunner(handler);
		runner.run(reader);
	}

}
