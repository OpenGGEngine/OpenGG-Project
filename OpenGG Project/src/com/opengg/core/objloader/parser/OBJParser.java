

package com.opengg.core.objloader.parser;

import com.opengg.core.exceptions.WFException;
import com.opengg.core.objloader.common.OBJLimits;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;
import java.net.URLDecoder;

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

	public OBJModel parse(URL paths) throws WFException, IOException {
                String path = URLDecoder.decode(paths.getFile(), "UTF-8");
                File f = new File(path);
                
		
		return parse(new BufferedReader(new BufferedReader(new FileReader(f))));
	}

	@Override
	public OBJModel parse(BufferedReader reader) throws WFException, IOException {
		final OBJParseRunner runner = new OBJParseRunner();
		return runner.run(reader, getLimits());
	}

    @Override
    public OBJModel parse(InputStream in) throws WFException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
