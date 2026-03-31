package org.kob.botrunningsystem.utils.sandbox;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

public class JdkCompiler {
    public static Map<String, byte[]> compile(String fullClassName, String code) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager stdManager = compiler.getStandardFileManager(diagnostics, null, null);

        // 使用完整类名进行编译，确保与字节码中的类名一致
        MemoryJavaFileObject javaFile = new MemoryJavaFileObject(fullClassName, code);
        MemoryClassFileManager classManager = new MemoryClassFileManager(stdManager);

        JavaCompiler.CompilationTask task = compiler.getTask(null, classManager, diagnostics, null, null, Collections.singletonList(javaFile));
        if (!task.call()) {
            throw new RuntimeException("编译失败: " + diagnostics.getDiagnostics());
        }
        return classManager.getAllByteCodes();
    }

    private static class MemoryJavaFileObject extends SimpleJavaFileObject {
        private final String code;
        protected MemoryJavaFileObject(String className, String code) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }
        @Override public CharSequence getCharContent(boolean ignoreEncodingErrors) { return code; }
    }

    private static class MemoryClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final Map<String, MemoryClassFileObject> map = new HashMap<>();
        protected MemoryClassFileManager(StandardJavaFileManager fileManager) { super(fileManager); }
        @Override public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
            MemoryClassFileObject file = new MemoryClassFileObject(className);
            map.put(className, file);
            return file;
        }
        public Map<String, byte[]> getAllByteCodes() {
            Map<String, byte[]> byteCodes = new HashMap<>();
            for (Map.Entry<String, MemoryClassFileObject> entry : map.entrySet()) {
                byteCodes.put(entry.getKey(), entry.getValue().outputStream.toByteArray());
            }
            return byteCodes;
        }

    }

    private static class MemoryClassFileObject extends SimpleJavaFileObject {
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        protected MemoryClassFileObject(String className) { super(URI.create("bytes:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS); }
        @Override public OutputStream openOutputStream() { return outputStream; }
    }
}