package com.opengg.core.render.shader;

import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.io.FileStringLoader;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShaderFile{
    private List<String> includes;
    private List<String> preprocessor;
    private String data;
    private ShaderFileType type;

    private List<ShaderUniform> uniforms = new ArrayList<>();
    private List<ShaderField> fields = new ArrayList<>();
    private List<ShaderFunction> code = new ArrayList<>();

    public ShaderFile(String name, String source){
        try{
            data = FileStringLoader.loadStringSequence(URLDecoder.decode(source, "UTF-8"));

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

            includes = new ArrayList<>();

            int uniformpos = data.indexOf("@uniforms");
            int fieldpos = data.indexOf("@fields");
            int codepos = data.indexOf("@code");

            String uniformsource = "";
            String fieldsource = "";
            String codesource = "";

            if(uniformpos != -1){
                if(fieldpos != -1){
                    uniformsource = data.substring(data.indexOf("\n", uniformpos), fieldpos).trim();
                }else if(codepos != -1){
                    uniformsource = data.substring(data.indexOf("\n", uniformpos), codepos).trim();
                }else{
                    uniformsource = data.substring(data.indexOf("\n", uniformpos)).trim();
                }
            }

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

            processUniforms(uniformsource);
            processFields(fieldsource);
            processFunctions(codesource);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void processUniforms(String uniformsource){
        List<String> alluniforms = new ArrayList<>();

        uniformsource = uniformsource.trim();
        int currentindex = 0;
        boolean parsed = false;
        while(!parsed){
            if(uniformsource.indexOf(';', currentindex) == -1) continue;

            String next = uniformsource.substring(currentindex, uniformsource.indexOf(';', currentindex) + 1);

            if(next.contains("{")){

                if(uniformsource.indexOf("};", currentindex) == -1){
                    throw new ShaderException("Reached end of file while searching for brace at line " + currentindex);
                }

                next = uniformsource.substring(currentindex, uniformsource.indexOf("};", currentindex) + 2);
                currentindex = uniformsource.indexOf("};", currentindex) + 2;
            }else{
                currentindex = uniformsource.indexOf(';', currentindex) + 1;
            }

            next = next.trim();

            alluniforms.add(next);

            if(currentindex>= uniformsource.length()) parsed = true;
        }

        for(var unifsource : alluniforms){
            ShaderUniform uniform = new ShaderUniform();

            if(unifsource.contains("{")){
                uniform.data = unifsource.substring(unifsource.indexOf("{") + 1, unifsource.indexOf("}")-1);
            }

            int namestart = 0;
            if(unifsource.contains("{")){
                namestart = unifsource.lastIndexOf(" ", unifsource.indexOf("{"));
                uniform.name = unifsource.substring(namestart, unifsource.indexOf("{")).trim();
            }else{
                namestart = unifsource.lastIndexOf(" ", unifsource.indexOf(";"));
                uniform.name = unifsource.substring(namestart, unifsource.indexOf(";")).trim();
            }

            int typestart = 0;
            if(unifsource.lastIndexOf(" ", namestart-1) == -1){
                uniform.type = unifsource.substring(0, namestart);
            }else{
                typestart = unifsource.lastIndexOf(" ", namestart-1);
                uniform.type = unifsource.substring(typestart, namestart).trim();
            }

            if(typestart != 0){
                uniform.modifiers = unifsource.substring(0, typestart).trim();
            }

            uniforms.add(uniform);
        }
    }

    private void processFields(String fullfieldsource){
        fullfieldsource = fullfieldsource.trim();
        fields = Arrays.stream(fullfieldsource.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> new ShaderField(s))
                .collect(Collectors.toList());
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
                    lastfunc = i;
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

    public List<ShaderUniform> getUniforms(){
        return uniforms;
    }

    public List<ShaderField> getFields(){
        return fields;
    }

    public List<ShaderFunction> getCode(){
        return code;
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

    public static class ShaderUniform{
        private String name = "";
        private String type = "";
        private String modifiers = "";
        private String data = "";
        private ShaderProgram.ShaderType usetype;

        public String getName(){
            return name;
        }

        public String getType(){
            return type;
        }

        public String getModifiers(){
            return modifiers;
        }

        public String getData(){
            return data;
        }

        @Override
        public String toString(){
            return "ShaderUniform{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", modifiers='" + modifiers + '\'' +
                    ", data='" + data + '\'' +
                    '}';
        }
    }

    public static class ShaderField{
        public ShaderField(String line){
            int namestart = 0;
            if(line.contains("=")){
                initialvalue = line.substring(line.indexOf("=") + 1, line.length()-1).trim();

                namestart = line.lastIndexOf(" ", line.indexOf("="));
                name = line.substring(namestart, line.indexOf("=")).trim();
            }else{
                namestart = line.lastIndexOf(" ");
                name = line.substring(namestart, line.length()-1).trim();
            }

            int typestart = 0;
            if(line.lastIndexOf(" ", namestart-1) == -1){
                type = line.substring(0, namestart);
            }else{
                typestart = line.lastIndexOf(" ", namestart-1);
                type = line.substring(typestart, namestart).trim();
            }

            if(typestart != 0){
                modifiers = line.substring(0, typestart).trim();
            }
        }

        private String name = "";
        private String type = "";
        private String modifiers = "";
        private String initialvalue = "";
        private ShaderProgram.ShaderType usetype;

        public String getName(){
            return name;
        }

        public String getType(){
            return type;
        }

        public String getModifiers(){
            return modifiers;
        }

        public String getInitialValue(){
            return initialvalue;
        }

        @Override
        public String toString(){
            return "ShaderField{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", modifiers='" + modifiers + '\'' +
                    ", initialvalue='" + initialvalue + '\'' +
                    '}';
        }
    }

    public enum ShaderFileType{
        VERT, FRAG, GEOM, TESSEVAL, TESSCONTROL, UTIL;
    }
}
