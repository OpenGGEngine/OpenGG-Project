

package com.opengg.objparser.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.opengg.exceptions.WFException;

/**
 * Default implementation of the {@link IMTLScanner}
 * interface.
 * 
 * @author Momchil Atanasov
 *
 */
public class MTLScanner implements IMTLScanner {
	
	/**
	 * Creates a new instance of {@link MTLScanner}.
	 */
	public MTLScanner() {
		super();
	}

	@Override
	public void scan(InputStream in, IMTLScannerHandler handler) throws WFException, IOException {
		final Reader reader = new InputStreamReader(in);
		scan(new BufferedReader(reader), handler);
	}

	@Override
	public void scan(BufferedReader reader, IMTLScannerHandler handler) throws WFException, IOException {
		final MTLScanRunner runner = new MTLScanRunner(handler);
		runner.run(reader);
	}

}
