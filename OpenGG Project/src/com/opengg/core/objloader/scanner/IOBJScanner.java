
package com.opengg.core.objloader.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import com.opengg.core.exceptions.WFException;


public interface IOBJScanner {
	
	/**
	 * Scans the file specified through the {@link InputStream} and
	 * passes any events to the {@link IOBJScannerHandler} instance.
	 * @param in stream from which to read the file
	 * @param handler handler to be notified of events
	 * @throws WFException if the OBJ file is corrupt.
	 * @throws IOException if an I/O error occurs.
	 */
	public void scan(InputStream in, IOBJScannerHandler handler) throws WFException, IOException;
	
	/**
	 * Scans the file specified through the {@link BufferedReader} and
	 * passes any events to the {@link IOBJScannerHandler} instance.
	 * @param reader reader from which the file will be read
	 * @param handler handler to be notified of events
	 * @throws WFException if the OBJ file is corrupt
	 * @throws IOException if an I/O error occurs.
	 */
	public void scan(BufferedReader reader, IOBJScannerHandler handler) throws WFException, IOException;

}
