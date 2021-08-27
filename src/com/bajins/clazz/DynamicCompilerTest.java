package com.bajins.clazz;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 动态编译生成源码并加载（动态代理）
 * https://blog.csdn.net/u010398771/article/details/90474813
 * https://github.com/pfmiles/kan-java
 * https://pfmiles.github.io/blog/dynamic-java
 */
public class DynamicCompilerTest {
    private final Writer out;
    private final JavaFileManager fileManager;
    private final DiagnosticListener<? super JavaFileObject> diagnosticListener;
    private final Iterable<String> options;
    private final Iterable<String> classes;
    private final Iterable<? extends JavaFileObject> compilationUnits;
    private final static JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    public DynamicCompilerTest(Writer out, JavaFileManager fileManager,
                               DiagnosticListener<? super JavaFileObject> diagnosticListener, Iterable<String> options,
                               Iterable<String> classes, Iterable<? extends JavaFileObject> compilationUnits) {
        this.out = out;
        this.fileManager = fileManager;
        this.diagnosticListener = diagnosticListener;
        this.options = options;
        this.classes = classes;
        this.compilationUnits = compilationUnits;
    }

    public boolean compile() {
        JavaCompiler.CompilationTask task = COMPILER.getTask(out, fileManager, diagnosticListener, options, classes,
                compilationUnits);
        // 同步调用
        return task.call();
    }

    public static void main(String[] args) throws IOException {
        //我们文件路径
        String path = "C:\\CalledClass.java";
        String content = readContext(path);
        // 自定义 JavaFileManager 方便自定义输入和输出
        MyJavaFileManager myJavaFileManager = new MyJavaFileManager(COMPILER.getStandardFileManager(null, null,
                StandardCharsets.UTF_8));
        DynamicCompilerTest dynamicTest = new DynamicCompilerTest(null
                , myJavaFileManager
                , System.out::println, null, null, Arrays.asList(new StringJavaFileObject(content, "CalledClass")));
        boolean b = dynamicTest.compile();
        myJavaFileManager.getByteArrayJavaFileObjects().forEach(b1 -> {
            try {
                ByteArrayOutputStream o = (ByteArrayOutputStream) b1.openOutputStream();
                // 获取字节码
                byte[] classByteArray = o.toByteArray();
                // 加载对象 ，这里是有问题的，我们应该 是在ClassLoader 中编译，并加载
                ClassLoader loader = new ClassLoader() {
                    @Override
                    protected Class<?> findClass(String name) throws ClassNotFoundException {
                        return defineClass(name, classByteArray, 0, classByteArray.length);
                    }
                };
                Class<?> clazz = loader.loadClass("CalledClass");
                if (Runnable.class.isAssignableFrom(clazz)) {
                    Runnable instance = (Runnable) clazz.newInstance();
                    instance.run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        });
        System.out.println(b ? "成功" : "失败");
    }

    private static String readContext(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        return new String(bytes);
    }

    //输入对象
    static class StringJavaFileObject extends SimpleJavaFileObject {

        private String content;

        /**
         * Construct a SimpleJavaFileObject of the given kind and with the
         * given URI.
         */
        public StringJavaFileObject(String content, String className) {
            super(URI.create("string:///" + className.replace(".", "/") + ".java"), Kind.SOURCE);
            this.content = content;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return content;
        }
    }

    // 输出对象
    static class ByteArrayJavaFileObject extends SimpleJavaFileObject {

        /**
         * Construct a SimpleJavaFileObject of the given kind and with the
         * given URI.
         */
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        public ByteArrayJavaFileObject(String name) {
            super(URI.create("bytes:///" + name.replace(".", "/") + ".class"), Kind.CLASS);
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return outputStream;
        }
    }

    static class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        // 就是一个装饰着模式  ForwardingJavaFileManager

        /**
         * Creates a new instance of ForwardingJavaFileManager.
         *
         * @param fileManager delegate to this file manager
         */
        public MyJavaFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        private final Set<ByteArrayJavaFileObject> byteArrayJavaFileObjects = new ConcurrentSkipListSet();

        public Set<ByteArrayJavaFileObject> getByteArrayJavaFileObjects() {
            return byteArrayJavaFileObjects;
        }

        // 有字节码的输出的时候 我们自定义一个JavaFileObject 来接受输出了
        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
                                                   FileObject sibling) throws IOException {
            if (JavaFileObject.Kind.CLASS == kind) {
                ByteArrayJavaFileObject byteArrayJavaFileObject = new ByteArrayJavaFileObject(className);
                byteArrayJavaFileObjects.add(byteArrayJavaFileObject);
                return byteArrayJavaFileObject;
            } else {
                return super.getJavaFileForOutput(location, className, kind, sibling);
            }
        }
    }
}