

package com.opengg.core.io.objloader.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import com.opengg.core.io.objloader.common.OBJLimits;
import com.opengg.core.exceptions.WFException;
import com.opengg.core.io.objloader.parser.OBJModel;

/**
 * The {@link IOBJParser} interface provides methods
 * through which users can parse OBJ wavefront 
 * resources.
 * 
 *
 */
public interface IOBJParser {
	
	/**
	 * Sets the limits that need to be followed
	 * during parsing.
	 * <p>
	 * If <code>null</code> is specified, then no
	 * constraints will be applied.
	 * @param limits the limits to follow during parsing
	 */
	public void setLimits(OBJLimits limits);
	
	/**
	 * Returns the limits that are applied during parsing.
	 * <p>
	 * If <code>null</code> is returned, then no limits
	 * have been set.
	 * @return an instance of {@link OBJLimits} or <code>null</code>
	 * if no constraints should be applied.
	 */
	public OBJLimits getLimits();
	
	/**
	 * Parses a 3D model (OBJ) resource from
	 * the specified {@link InputStream}.
	 * @param in stream from which to parse the model
	 * @return an instance of {@link OBJModel}
	 * @throws WFException if an error occurs during resource parsing
	 * @throws IOException if an I/O error occurs
	 */
	public OBJModel parse(InputStream in) throws WFException, IOException;

	/**
	 * Parses a 3D model (OBJ) resource from the
	 * specified {@link BufferedReader}.
	 * @param reader reader from which to parse the model
	 * @return an instance of {@link OBJModel}
	 * @throws WFException if an error occurs during resource parsing
	 * @throws IOException if an I/O error occurs
	 */
	public OBJModel parse(BufferedReader reader) throws WFException, IOException;
}
