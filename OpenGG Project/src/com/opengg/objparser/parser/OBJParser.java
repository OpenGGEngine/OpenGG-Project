

package com.opengg.objparser.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.opengg.objparser.common.OBJLimits;
import com.opengg.exceptions.WFException;

/**
 * Default implementation of the {@link IOBJParser}
 * interface.
 * 
 *
 */
public class OBJParser implements IOBJParser {
	
	private OBJLimits limits;
	
	/**
	 * Creates a new instance of {@link OBJParser}.
	 */
	public OBJParser() {
		super();
	}
	
	@Override
	public void setLimits(OBJLimits limits) {
		this.limits = limits;
	}
	
	@Override
	public OBJLimits getLimits() {
		return limits;
	}

	@Override
	public OBJModel parse(InputStream in) throws WFException, IOException {
		final Reader reader = new InputStreamReader(in);
		return parse(new BufferedReader(reader));
	}

	@Override
	public OBJModel parse(BufferedReader reader) throws WFException, IOException {
		final OBJParseRunner runner = new OBJParseRunner();
		return runner.run(reader, getLimits());
	}

}
