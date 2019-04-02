package com.opengg.core.render.shader.ggsl;

import com.opengg.core.console.GGConsole;
import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.render.shader.ShaderProgram;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ShaderFile{
    private List<String> includes;
    private List<String> glvals;
    private HashMap<String, String> preprocessor;
    private String version;
    private ShaderFileType type;
    private String name;

    private String compiledsource;

    private Parser.AbstractSyntaxTree tree;
    private boolean parsed;

    private static final Pattern multilineReplacer = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

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
            String data = FileStringLoader.loadStringSequence(URLDecoder.decode(source, StandardCharsets.UTF_8));

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

            try{data = runPreprocessor(data);}catch(Exception e){ return;}

            var lexer = new Lexer(data);
            lexer.process();
            var parser = new Parser(lexer);
            tree = parser.parse();
            parsed = true;


        }catch(IOException e){
            e.printStackTrace();
        }catch(Exception e){
            GGConsole.error("Failed to load shader file " + name + " (from file " + source + "): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String runPreprocessor(String data){
        if(!data.contains("@") && data.contains("#version")){
            throw new ShaderException("Attempted to load GLSL file as GGSL");
        }

        includes = new ArrayList<>();

        preprocessor = new HashMap<>();

        var lines = data.split("\n");

        for(var line : lines){
            if(line.indexOf("@") == 0){
                String varname = line.substring(0, !line.contains(" ") ? line.length() : line.indexOf(" ")).trim();
                String varval = line.substring(!line.contains(" ") ? line.length() : line.indexOf(" ")).trim();

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

        return data;
    }

    public void compile(){
        compiledsource = tree.nodes.stream()
                .map(this::process)
                .map(s -> s + "\n")
                .collect(Collectors.joining());

        compiledsource = compiledsource.replaceAll(";;", ";");
        for(var val : glvals){
            compiledsource = "#" + val + "\n" + compiledsource;
        }

    }

    public String process(Parser.Node node){
        var builder = new StringBuilder();
        if(node instanceof Parser.Body){
            var body = (Parser.Body) node;

            for(var node2 : body.expressions){
                builder.append(process(node2)).append(";\n");
            }
        }
        else if(node instanceof Parser.Function){
            var func = (Parser.Function) node;

            for(var modifier : func.modifiers.modifiers){
                builder.append(modifier.value).append(" ");
            }

            builder.append(func.type).append(" ");
            builder.append(func.name);
            builder.append("(");

            for(var arg : func.args.args){
                for(var modifier : arg.modifiers.modifiers){
                    builder.append(modifier.value).append(" ");
                }

                builder.append(arg.type.value).append(" ");
                builder.append(arg.name.value);

                if(func.args.args.indexOf(arg) != func.args.args.size()-1){
                    builder.append(", ");
                }
            }

            builder.append(") {\n");
            builder.append(process(func.body));
            builder.append("} \n");
        }
        else if(node instanceof Parser.Struct){
            var struct = (Parser.Struct) node;

            for(var modifier : struct.modifiers.modifiers){
                builder.append(modifier.value).append(" ");
            }


            builder.append("struct ").append(struct.name).append(" {\n");

            builder.append(process(struct.declarations));

            builder.append("} ").append(struct.variable).append(";\n");
        }
        else if(node instanceof Parser.Interface){
            var interfacee = (Parser.Interface) node;

            for(var modifier : interfacee.modifiers.modifiers){
                builder.append(modifier.value).append(" ");
            }

            builder.append(interfacee.accessor.value).append(" ");

            builder.append(interfacee.name).append(" {\n");

            builder.append(process(interfacee.declarations));

            builder.append("} ").append(interfacee.variable).append(";\n");
        }
        else if(node instanceof Parser.If){
            var iff = (Parser.If) node;
            builder.append("if(");
            builder.append(process(iff.conditional)).append("){\n");
            builder.append(process(iff.then)).append("}else{\n");
            builder.append(process(iff.els)).append("}\n");
        }
        else if(node instanceof Parser.While){
            var whil = (Parser.While) node;
            builder.append("while(");
            builder.append(process(whil.conditional)).append("){\n");
            builder.append(process(whil.contents)).append("}\n");
        }
        else if(node instanceof Parser.For){
            var forr = (Parser.For) node;
            builder.append("for(");
            builder.append(process(forr.assignment));
            if(!builder.toString().endsWith(";")){
                builder.append("; ");
            }else{
                builder.append(" ");
            }
            builder.append(process(forr.conditional)).append("; ");
            builder.append(process(forr.modifier)).append("){\n");
            builder.append(process(forr.contents)).append("}\n");
        }
        else if(node instanceof Parser.Expression){
            if(node instanceof Parser.Assignment){
                if(node instanceof Parser.Declaration){
                    var dec = (Parser.Declaration) node;

                    for(var mod : dec.modifiers.modifiers){
                        builder.append(mod.value).append(" ");
                    }

                    builder.append(dec.type).append(" ");
                }

                var assign = (Parser.Assignment) node;

                builder.append(assign.name);
                if(assign.assigntype != null){
                    builder.append(" ").append(assign.assigntype).append(" ");

                    builder.append(process(assign.value));
                }

                builder.append(";");
            }
            else if(node instanceof Parser.Identifier){
                builder.append(((Parser.Identifier)node).value);
            }
            else if(node instanceof Parser.FloatLiteral){
                builder.append(Float.valueOf(((Parser.FloatLiteral)node).value)).append("f");
            }
            else if(node instanceof Parser.IntegerLiteral){
                builder.append(Integer.valueOf(((Parser.IntegerLiteral)node).value));
            }
            else if(node instanceof Parser.BinaryOp){
                var binop = (Parser.BinaryOp) node;

                builder.append("(");
                builder.append(process(binop.left)).append(" ");
                builder.append(binop.op);
                builder.append(" ").append(process(binop.right));
                builder.append(")");
            }
            else if(node instanceof Parser.UnaryOp){
                var unop = (Parser.UnaryOp) node;

                if (unop.after) {
                    builder.append(process(unop.exp));
                    builder.append(unop.op);
                } else {
                    builder.append(unop.op);
                    builder.append(process(unop.exp));
                }
            }
            else if(node instanceof Parser.FieldAccessor){
                var access = (Parser.FieldAccessor)node;
                builder.append(process(access.value)).append(access.accessor.value);
            }
            else if(node instanceof Parser.TernaryOp){
                var ternary = (Parser.TernaryOp) node;

                builder.append("(");
                builder.append(process(ternary.conditional)).append(" ? ");
                builder.append(process(ternary.then)).append(" : ");
                builder.append(process(ternary.els));
                builder.append(")");
            }
            else if(node instanceof Parser.Return){
                builder.append("return ").append(process(((Parser.Return)node).returnValue));
            }
            else if(node instanceof Parser.FunctionCall){
                var fcall = (Parser.FunctionCall) node;

                builder.append(fcall.name).append("(");
                for(var arg : fcall.args.expressions){
                    builder.append(process(arg));
                    if(fcall.args.expressions.indexOf(arg) != fcall.args.expressions.size()-1){
                    builder.append(", ");
                    }
                }
                builder.append(")");
            }
        }

        return builder.toString();
    }

    public List<String> getIncludes(){
        return includes;
    }

    public ShaderFileType getType(){
        return type;
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

    public boolean isParsed(){
        return parsed;
    }

    public String getCompiledSource(){
        return compiledsource;
    }

    public enum ShaderFileType{
        VERT, FRAG, GEOM, TESSEVAL, TESSCONTROL, UTIL
    }
}
