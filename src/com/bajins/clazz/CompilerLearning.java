package com.bajins.clazz;

import javax.tools.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * 编译API使用
 */
public class CompilerLearning {
    public static void main(String[] args) throws IOException {
        // 得到一个JavaCompiler接口的实例
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // DiagnosticListener的实现，得到诊断信息
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        // 控制输入、输出
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);

        String fullFileName = "com.bajins.dynamic.".replaceAll("\\.", java.io.File.separator) + "Calculator.java";
        // 获取需要编译的源代码：从文件或者字符流中获取
        Iterable<? extends JavaFileObject> files =
                standardFileManager.getJavaFileObjectsFromStrings(Arrays.asList(fullFileName));
        // 生成编译任务抽象
        JavaCompiler.CompilationTask task = compiler.getTask(null, standardFileManager, diagnostics, null, null, files);
        // 调用编译源代码任务
        Boolean call = task.call();

        // 编译java源代码
        int result = compiler.run(null, null, null, "F:\\demo\\Test.java");
        System.out.println(result == 0 ? "编译成功" : "编译失败");

        // 执行java 命令 , 空参数为当前所在文件夹
        Process process = Runtime.getRuntime().exec("java Test", null, new File("F:\\demo\\"));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            System.out.println(str);
        }
    }
}
