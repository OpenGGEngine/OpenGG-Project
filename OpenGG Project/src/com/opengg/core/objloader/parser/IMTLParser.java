
package com.opengg.core.objloader.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import com.opengg.core.objloader.common.MTLLimits;
import com.opengg.core.exceptions.WFException;
import com.opengg.core.objloader.parser.MTLLibrary;

/**
 * The {@link IMTLParser} interface provides methods
 * through which users can parse MTL wavefront
 * resources.
 * 
 *
 */
public interface IMTLParser {
	
	/**
	 * Sets the limits that need to be followed
	 * during parsing.
	 * <p>
	 * If <code>null</code> is specified, then no constraints
	 * will be applied.
	 * @param limits the limits to follow during parsing.
	 */
	public void setLimits(MTLLimits limits);
	
	/**
	 * Returns the limits that are followed during parsing.
	 * <p>
	 * If <code>null</code> is returned, then no constraints
	 * have been specified.
	 * @return an instance of {@link MTLLimits} or <code>null</code>
	 * if no limits are applied.
	 */
	public MTLLimits getLimits();
	
	/**
	 * Parses a material library (MTL) resource from the
	 * specified {@link InputStream}.
	 * @param in stream from which to load the library
	 * @return an instance of {@link MTLLibrary}
	 * @throws WFException if an error occurs during resource parsing
	 * @throws IOException if an I/O error occurs
	 */
	public MTLLibrary parse(InputStream in) throws WFException, IOException;

	/**
	 * Parses a material library (MTL) resource from the
	 * specified {@link BufferedReader}.
	 * @param reader reader from which to load the library
	 * @return an instance of {@link MTLLibrary}
	 * @throws WFException if an error occurs during resource parsing
	 * @throws IOException if an I/O error occurs
	 */
	public MTLLibrary parse(BufferedReader reader) throws WFException, IOException;
	
}
