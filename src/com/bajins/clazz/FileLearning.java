package com.bajins.clazz;

import java.io.*;
import java.util.List;


/**
 *
 */
public class FileLearning {


    /**
     * 替换文件内容
     *
     * @param file 文件路径
     */
    public void replaceContent(String file) {
        try ( // 创建 FileReader对象和FileWriter对象.
              FileReader fr = new FileReader(file);
              FileWriter fw = new FileWriter(file);
              // 创建 输入、输入出流对象.
              BufferedReader reader = new BufferedReader(fr);
              BufferedWriter writer = new BufferedWriter(fw)) {// 会自动关闭 reader 和 writer.

            String line;
            StringBuffer sbf = new StringBuffer();
            //循环读取并追加字符
            while ((line = reader.readLine()) != null) {
                sbf.append(line);
            }
            System.out.println("替换前：" + sbf);
            /*替换内容*/
            String newString = sbf.toString().replace("{name}", "11").replace("{type}", "22");
            System.out.println("替换后：" + newString);
            writer.write(newString);  // 写入文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归查找文件
     *
     * @param baseDirPath 文件夹路径
     * @param fileNames   需要查找的文件名
     * @param result      查找到的文件
     */
    public static void findFiles(Object baseDirPath, final List<String> fileNames, List<File> result) {
        if (fileNames == null || fileNames.isEmpty()) {
            throw new IllegalArgumentException("需要查找的文件名不能为空");
        }
        if (baseDirPath == null) {
            throw new IllegalArgumentException("文件夹路径不能为空");
        }
        File baseDir;
        if (baseDirPath instanceof CharSequence) { // 是地址
            String dirPath = (String) baseDirPath;
            if (dirPath.isEmpty() || dirPath.trim().equals("")) {
                throw new IllegalArgumentException("文件夹路径不能为空");
            }
            baseDir = new File(dirPath); // 创建一个File对象
            if (!baseDir.exists()) {// 判断目录是否存在
                throw new IllegalArgumentException(String.format("文件查找失败：%s不存在！", baseDirPath));
            }
            if (!baseDir.isDirectory()) { // 判断是否为目录
                throw new IllegalArgumentException(String.format("文件查找失败：%s不是一个目录！", baseDirPath));
            }
        } else if (baseDirPath instanceof File) { // 是文件
            baseDir = (File) baseDirPath;
        } else {
            throw new IllegalArgumentException("请检查文件夹路径");
        }
        File[] files = baseDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        File tempFile;
        for (File file : files) {
            tempFile = file.getAbsoluteFile();
            if (fileNames.contains(file.getName())) {
                result.add(tempFile);
                continue;
            }
            if (file.isDirectory()) {
                findFiles(tempFile, fileNames, result);
            }
        }
    }
}
