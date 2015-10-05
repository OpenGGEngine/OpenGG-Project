
package com.opengg.core.objloader.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.opengg.core.objloader.common.MTLLimits;
import com.opengg.core.exceptions.WFException;

/**
 * Default implementation of the {@link IMTLParser}
 * interface.
 * 
 *
 */
public class MTLParser implements IMTLParser {
	
	private MTLLimits limits;
	
	/**
	 * Creates a new {@link MTLParser} instance.
	 */
	public MTLParser() {
		super();
	}
	
	@Override
	public void setLimits(MTLLimits limits) {
		this.limits = limits;
	}
	
	@Override
	public MTLLimits getLimits() {
		return limits;
	}

	@Override
	public MTLLibrary parse(InputStream in) throws WFException, IOException {
		final Reader reader = new InputStreamReader(in);
		return parse(new BufferedReader(reader));
	}

	@Override
	public MTLLibrary parse(BufferedReader reader) throws WFException, IOException {
		final MTLParseRunner runner = new MTLParseRunner();
		return runner.run(reader, getLimits());
	}

}
