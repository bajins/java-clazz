package com.bajins.clazz;

import java.io.*;


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
}
