package com.opengg.core.render.shader;

import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.io.FileStringLoader;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private List<ShaderStruct> structs = new ArrayList<>();


    private static final Pattern multilineReplacer = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern structFinderPattern = Pattern.compile("struct\\s.*?\\}\\s*?\\;", Pattern.DOTALL | Pattern.MULTILINE);
    private static final Pattern functionFinderPattern = Pattern.compile("\\)\\s*\\{", Pattern.DOTALL | Pattern.MULTILINE);
    private static final Pattern splitterPattern = Pattern.compile(";(?![^{]*})");
    private static final Pattern layoutDataRegex = Pattern.compile("(?<=layout\\()(.*)(?=\\))", Pattern.DOTALL);
    private static final Pattern layoutRegex = Pattern.compile("layout\\(.*\\)", Pattern.DOTALL);
    private static final Pattern valueDataRegex = Pattern.compile("(?<==)(.*)", Pattern.DOTALL);
    private static final Pattern valueRegex = Pattern.compile("=.*", Pattern.DOTALL);
    private static final Pattern structPattern = Pattern.compile("(?<=\\{)(.*)(?=\\})", Pattern.DOTALL);

    private static Pattern[] modPatterns;

    private static String[] modifiers = new String[]{"in", "out", "inout", "uniform", "const"};
    private static String[] types = new String[]{"float", "int", "uint", "bool", "double",
            "bvec2", "ivec2", "uvec2", "dvec2", "vec2",
            "bvec3", "ivec3", "uvec3", "dvec3", "vec3",
            "bvec4", "ivec4", "uvec4", "dvec4", "vec4",
            "mat2", "mat3", "mat4",
            "sampler", "sampler2D", "sampler2DArray", "samplerCube"};

    static {
        modPatterns = Arrays.stream(modifiers)
                .map(s -> Pattern.compile("\\W" + s + "\\W"))
                .toArray(Pattern[]::new);
    }

    private List<String> alltypes = new ArrayList<>();

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


            data = data.replaceAll("@.*?\n", "");
            data = data.replaceAll("\n\n", "\n");

            data = data.replaceAll("//.*?\n", "");

            data = multilineReplacer.matcher(data).replaceAll("");

            process(data);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void process(String data){
        Matcher structFinderMatcher = structFinderPattern.matcher(data);

        while(structFinderMatcher.find()){
            processStruct(structFinderMatcher.group());
            data = data.replace(structFinderMatcher.group(), "");
        }

        Matcher functionFinderMatcher = functionFinderPattern.matcher(data);

        List<String> funcs = new ArrayList<>();

        while (functionFinderMatcher.find()){
            int start = functionFinderMatcher.start();

            int realstart = 0;
            if(data.lastIndexOf(";", start) > realstart) realstart = data.lastIndexOf(";", start);
            if(data.lastIndexOf("}", start) > realstart) realstart = data.lastIndexOf("}", start);
            realstart++;
            String nfunc = findNextFunc(data, realstart);

            data = data.replace(nfunc, "");
            functionFinderMatcher = functionFinderPattern.matcher(data);
        }

        Arrays.stream(splitterPattern.split(data))
                .map(String::trim)
                .forEach(this::processField);
    }

    private void processField(String line) {
        if(line.contains("struct")) {
            processStruct(line);
            return;
        }
        String initialvalue = "";
        String name = "";
        String type = "";
        List<String> fieldmodifiers = new ArrayList<>();
        String layoutdata = "";
        int loc = -1;

        int namestart = 0;
        int typestart = 0;
        int valuestart = 0;
        boolean hasvalue = false;
        boolean hasdata = false;

        Matcher containsLayout = layoutRegex.matcher(line);
        if(containsLayout.find()){
            Matcher datamatch = layoutDataRegex.matcher(containsLayout.group());

            datamatch.find();
            layoutdata = datamatch.group(1);
            line = line.replace(containsLayout.group(), "");
        }

        Matcher containsData = valueRegex.matcher(line);
        if(containsData.find()){
            Matcher datamatch = valueDataRegex.matcher(containsData.group());

            datamatch.find();
            initialvalue = datamatch.group(1);
            line = line.replace(containsData.group(), "");
        }else{
            line = line.replaceAll(";\\s*$", "");
        }

        for(var pattern : modPatterns){
            Matcher modMatcher = pattern.matcher(line);
            if(modMatcher.find()){
                fieldmodifiers.add(pattern.pattern().replace("\\W", ""));
                line = line.replace(modMatcher.group(), "");
            }
        }

        line = line.trim();

        ShaderField field = new ShaderField();
        field.initialvalue = initialvalue;
        field.modifiers = fieldmodifiers;
        field.name = line;
        field.loc = loc;
        field.layoutdata = layoutdata;

        fields.add(field);
    }

    private void processStruct(String line) {
        String name;
        String data;

        line = line.replaceAll("\\s*struct\\s*", "");

        Matcher matcher = structPattern.matcher(line);
        matcher.find();
        data = matcher.group();

        name = line.substring(0, line.indexOf("{"));

        ShaderStruct struct = new ShaderStruct();
        struct.name = name;
        struct.data = data;
        structs.add(struct);
    }

    private String findNextFunc(String source, int start){
        int bracecounter = 0;

        for(int i = start; i < source.length(); i++){
            char ch = source.charAt(i);

            if(ch == '{'){
                bracecounter++;
            }else if(ch == '}'){
                bracecounter--;
                if(bracecounter == 0){
                    String value = source.substring(start, i + 1);
                    processFunction(value);
                    return value;
                }
            }
        }


        throw new ShaderException("Reached end of file while parsing");

    }

    private void processFunction(String funcsource) {
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

    public List<ShaderStruct> getStructs(){
        return structs;
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

    public static class ShaderStruct{
        private String name;
        private String data;

        public String getName() {
            return name;
        }

        public String getData() {
            return data;
        }

        @Override
        public String toString() {
            return "ShaderStruct{" +
                    "name='" + name + '\'' +
                    ", data='" + data + '\'' +
                    '}';
        }
    }

    public static class ShaderField{
        private String name = "";
        private String type = "";
        private List<String> modifiers = new ArrayList<>();
        private String initialvalue = "";
        private String layoutdata = "";
        private int loc = -1;
        private ShaderProgram.ShaderType usetype;

        public String getName(){
            return name;
        }

        public String getType(){
            return type;
        }

        public List<String> getModifiers(){
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
