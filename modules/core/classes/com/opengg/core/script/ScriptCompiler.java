package com.opengg.core.script;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;

import javax.tools.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptCompiler {

    public static CompilationResult compileScript(String name, String imports, String script){
        GGConsole.log("Compiling script " + name);

        var compiler = ToolProvider.getSystemJavaCompiler();
        var diagnosticsCollector = new DiagnosticCollector<>();

        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        //out.println("package com.opengg.ext.scripts;");
        out.println(imports);
        out.println("""
                        import java.util.function.BiConsumer;
                        import com.opengg.core.script.Script;
                        import com.opengg.core.world.components.*;
                        public class %s extends Script{
                            public %s(){
                                super(%s, %s);
                            }
                            @Override
                            public void accept(ScriptComponent c, Float delta){
                                %s
                            }
                        }"""
                .formatted(name, name, GGInfo.getVersion(), GGInfo.getApplicationName(), script));
        out.close();
        var fileObject = new JavaSourceFromString(name, writer.toString());

        writer = new StringWriter();
        out = new PrintWriter(writer);
        out.println("""
                module com.opengg.scripts {
                    requires com.opengg.core;
                    requires com.opengg.console;
                    requires com.opengg.math;
                    requires com.opengg.base;
                    requires java.desktop;
                }""");

        out.close();
        var moduleObject = new JavaSourceFromString("module-info", writer.toString());
// set compiler's classpath to be same as the runtime's
        var options = List.of("-d", Resource.getAbsoluteFromLocal("resources/scripts/").toString(), "--release", "12", "--enable-preview", "--module-path", System.getProperty("jdk.module.path"));

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(fileObject, moduleObject);
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, diagnosticsCollector, options, null, compilationUnits);

        GGConsole.log("Compilation complete");

        boolean success = task.call();

        if(!success){
            GGConsole.error("Failed to compile class " + name);
            var error = diagnosticsCollector.getDiagnostics().stream()
                    .filter(d -> d.getKind() == Diagnostic.Kind.ERROR)
                    .map(d -> d.getKind() + "\nAt line " + d.getLineNumber() + ": " + d.getMessage(null))
                    .collect(Collectors.joining("\nERROR: \n"));
            GGConsole.error(error);
        }else{
            GGConsole.log("Compilation of " + name + " successful");
                /*Files.move(Path.of(Resource.getAbsoluteFromLocal("resources/scripts/com/opengg/ext/scripts/" + name + ".class")),
                           Path.of(Resource.getAbsoluteFromLocal("resources/scripts/" + name + ".class")), StandardCopyOption.REPLACE_EXISTING);*/
        }

        return new CompilationResult(diagnosticsCollector.getDiagnostics(), success, Resource.getAbsoluteFromLocal("resources/scripts/" + name + ".class").toString());
    }

    static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    public static class CompilationResult{
        private final List<Diagnostic<?>> diagnostics;
        private final boolean success;
        private final String resultLocation;

        public CompilationResult(List<Diagnostic<?>> diagnostics, boolean success, String resultLocation) {
            this.diagnostics = diagnostics;
            this.success = success;
            this.resultLocation = resultLocation;
        }

        public List<Diagnostic<?>> getDiagnostics() {
            return diagnostics;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getResultLocation() {
            return resultLocation;
        }
    }
}
