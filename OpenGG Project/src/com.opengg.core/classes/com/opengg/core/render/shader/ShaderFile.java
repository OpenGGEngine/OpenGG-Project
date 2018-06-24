package com.opengg.core.render.shader;

import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.io.FileStringLoader;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ShaderFile{
    private List<String> includes;
    private List<String> glvals;
    private HashMap<String, String> preprocessor;
    private String data;
    private String version;
    private ShaderFileType type;
    private String name;

    private List<ShaderField> fields = new ArrayList<>();
    private List<ShaderFunction> code = new ArrayList<>();

    public ShaderFile(String name, String source){
        try{
            this.name = name;
            data = FileStringLoader.loadStringSequence(URLDecoder.decode(source, "UTF-8"));

            data = data.trim().replaceAll(" +", " ");
            String ending = source.substring(source.lastIndexOf(".") + 1);
            switch(ending){
                case "vert":
                    type = ShaderFile.ShaderFileType.VERT;
                    break;
                case "tesc":
                    type = ShaderFile.ShaderFileType.TESSCONTROL;
                    break;
                case "tese":
                    type = ShaderFile.ShaderFileType.TESSEVAL;
                    break;
                case "geom":
                    type = ShaderFile.ShaderFileType.GEOM;
                    break;
                case "frag":
                    type = ShaderFile.ShaderFileType.FRAG;
                    break;
                default:
                    type = ShaderFile.ShaderFileType.UTIL;
            }

            if(data.indexOf("@") == -1 && data.indexOf("#version") != -1){
                throw new ShaderException("Attempted to load GLSL file as GGSL");
            }

            includes = new ArrayList<>();

            preprocessor = new HashMap<>();

            var lines = data.split("\n");

            for(var line : lines){
                if(line.indexOf("@") == 0){
                    String varname = line.substring(0, line.indexOf(" ") == -1 ? line.length() : line.indexOf(" ")).trim();
                    String varval = line.substring(line.indexOf(" ") == -1 ? line.length() : line.indexOf(" ")).trim();

                    preprocessor.merge(varname, varval, (s1,s2) -> s1 + ":" + s2);
                }
            }

            version = preprocessor.getOrDefault("@version", "4.2");

            if(!version.matches("^\\d[.]\\d")){
                if(version.matches("^\\d[.]\\d\\d")){
                    version = version.substring(0, version.length()-1);
                }else{
                    version = version.charAt(0) + "." + version.charAt(1);
                }
            }

            if(!version.matches("^\\d[.]\\d"))
                throw new ShaderException("Encountered malformed version: " + preprocessor.getOrDefault("@version", "4.2"));

            glvals = valuesFromPreprocessor("@glsl");

            includes = valuesFromPreprocessor("@include");

            int uniformpos = data.indexOf("@uniforms");
            int fieldpos = data.indexOf("@fields");
            int codepos = data.indexOf("@code");

            String uniformsource = "";
            String fieldsource = "";
            String codesource = "";

            if(fieldpos != -1){
                if(codepos != -1){
                    fieldsource = data.substring(data.indexOf("\n", fieldpos), codepos).trim();
                }else{
                    fieldsource = data.substring(data.indexOf("\n", fieldpos)).trim();
                }
            }

            if(codepos != -1){
                codesource = data.substring(data.indexOf("\n", codepos)).trim();
            }

            if(codepos == -1 && fieldpos == -1 && uniformpos == -1){
                return;
            }

            if(fieldpos != -1) processFields(fieldsource);
            if(codepos != -1) processFunctions(codesource);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void processFields(String fullfieldsource){
        fullfieldsource = fullfieldsource.trim();
        if(fullfieldsource.isEmpty()) return;
        List<String> allfields = new ArrayList<>();

        int currentindex = 0;
        boolean parsed = false;
        while(!parsed){
            if(fullfieldsource.indexOf(';', currentindex) == -1) continue;

            String next = fullfieldsource.substring(currentindex, fullfieldsource.indexOf(';', currentindex) + 1);

            if(next.contains("{")){

                if(fullfieldsource.indexOf("};", currentindex) == -1){
                    throw new ShaderException("Reached end of file while searching for brace at line " + currentindex);
                }

                next = fullfieldsource.substring(currentindex, fullfieldsource.indexOf("};", currentindex) + 2);
                currentindex = fullfieldsource.indexOf("};", currentindex) + 2;
            }else{
                currentindex = fullfieldsource.indexOf(';', currentindex) + 1;
            }

            next = next.trim();

            allfields.add(next);

            if(currentindex>= fullfieldsource.length()) parsed = true;
        }

        for(var line : allfields){
            String initialvalue = "";
            String name = "";
            String type = "";
            String modifiers = "";
            String data = "";
            String layoutdata = "";
            int loc = -1;

            int namestart = 0;
            int typestart = 0;
            int valuestart = 0;
            int layoutstart = 0;
            boolean hasvalue = false;
            boolean hasdata = false;
            boolean haslayout = false;

            if(line.contains("layout")){
                haslayout = true;
                layoutstart = line.indexOf("layout");
                layoutdata = line.substring(line.indexOf("(", layoutstart) + 1, line.indexOf(")", layoutstart));
                line = line.replace(line.substring(line.indexOf("layout"), line.indexOf(")") + 1), "");
            }

            if(line.contains("=")){
                hasvalue = true;
                valuestart = line.indexOf("=");
                initialvalue = line.substring(valuestart + 1, line.length()-1).trim();
            }

            if(line.contains("{")){
                hasdata = true;
                data = line.substring(line.indexOf("{")+1,line.indexOf("}")).trim();
            }

            if(hasvalue && !hasdata){
                name = line.substring(line.lastIndexOf(" ", line.lastIndexOf("=")-2), line.lastIndexOf("=")).trim();
            }else if(hasdata){
                name = line.substring(line.lastIndexOf(" ", line.indexOf("{")-2), line.indexOf("{")).trim();
            }else{
                name = line.substring(line.lastIndexOf(" ", line.indexOf(";")-2), line.indexOf(";")).trim();
            }

            namestart = line.indexOf(name);

            if(line.lastIndexOf(" ", namestart-2) == -1){
                type = line.substring(0, namestart).trim();
            }else{
                typestart = line.lastIndexOf(" ", namestart-2);
                type = line.substring(typestart, namestart).trim();
            }

            if(typestart != 0){
                modifiers = line.substring(0, typestart).trim();
            }

            ShaderField field = new ShaderField();
            field.initialvalue = initialvalue;
            field.data = data;
            field.modifiers = modifiers;
            field.name = name;
            field.type = type;
            field.loc = loc;
            field.layoutdata = layoutdata;

            fields.add(field);
        }
    }

    private void processFunctions(String functionsource){
        List<String> allfunctions = new ArrayList<>();
        allfunctions = allfunctions.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        int lastfunc = 0;
        int lastpos = 0;
        int bracecounter = 0;
        boolean started = false;

        for(int i = 0; i < functionsource.length(); i++){
            char ch = functionsource.charAt(i);

            if(ch == '{'){
                bracecounter++;
            }else if(ch == '}'){
                bracecounter--;
                if(bracecounter == 0){
                    allfunctions.add(functionsource.substring(lastfunc, i + 1).trim());
                    lastfunc = i + 1;
                }
            }
        }

        if(bracecounter != 0){
            throw new ShaderException("Reached end of file while parsing");
        }

        for(var funcsource : allfunctions){
            ShaderFunction function = new ShaderFunction();

            function.data = funcsource.substring(funcsource.indexOf("{") + 1, funcsource.lastIndexOf("}")-1).trim();

            function.args = funcsource.substring(funcsource.indexOf("(") + 1, funcsource.indexOf(")")).trim();

            int namestart = 0;
            if(funcsource.lastIndexOf(" ", funcsource.indexOf("(")) == -1){
                function.name = funcsource.substring(0, funcsource.indexOf("(")).trim();;
            }else{
                namestart = funcsource.lastIndexOf(" ", funcsource.indexOf("("));
                function.name = funcsource.substring(namestart, funcsource.indexOf("(")).trim();
            }

            if(namestart == 0){
                function.returntype = "";
            }else{
                function.returntype = funcsource.substring(0, namestart).trim();
            }

            code.add(function);
        }
    }

    public List<String> getIncludes(){
        return includes;
    }

    public ShaderFileType getType(){
        return type;
    }

    public List<ShaderField> getFields(){
        return fields;
    }

    public List<ShaderFunction> getCode(){
        return code;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getGlValues() {
        return glvals;
    }

    public String getName(){
        return name;
    }

    public List<String> valuesFromPreprocessor(String id){
        return Arrays.stream(preprocessor.getOrDefault(id, "").split(":"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public static class ShaderFunction{
        private String name = "";
        private String returntype = "";
        private String args = "";
        private String data = "";
        private ShaderProgram.ShaderType usetype;

        public String getName(){
            return name;
        }

        public String getReturntype(){
            return returntype;
        }

        public String getArgs(){
            return args;
        }

        public String getData(){
            return data;
        }

        @Override
        public String toString(){
            return "ShaderFunction{" +
                    "name='" + name + '\'' +
                    ", returntype='" + returntype + '\'' +
                    ", args='" + args + '\'' +
                    ", data='" + data + '\'' +
                    '}';
        }
    }


    public static class ShaderField{
        private String name = "";
        private String type = "";
        private String modifiers = "";
        private String initialvalue = "";
        private String data = "";
        private String layoutdata = "";
        private int loc = -1;
        private ShaderProgram.ShaderType usetype;

        public String getName(){
            return name;
        }

        public String getType(){
            return type;
        }

        public String getData(){
            return data;
        }

        public String getModifiers(){
            return modifiers;
        }

        public String getInitialValue(){
            return initialvalue;
        }

        public String getLayoutData(){
            return layoutdata;
        }

        public int getLocation(){
            return loc;
        }

        @Override
        public String toString(){
            return "ShaderField{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", modifiers='" + modifiers + '\'' +
                    ", initialvalue='" + initialvalue + '\'' +
                    ", loc='" + loc + '\'' +
                    '}';
        }
    }

    public enum ShaderFileType{
        VERT, FRAG, GEOM, TESSEVAL, TESSCONTROL, UTIL;
    }
}
