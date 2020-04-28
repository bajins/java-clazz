package com.bajins.clazz;

import com.bajins.clazz.delayqueue.DelayedMessage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 文件IO操作
 */
public class IOStream {
    /**
     * 判断文件及目录是否存在，若不存在则创建文件及目录
     *
     * @param filepath 文件夹或文件路径
     * @return java.io.File
     */
    public static File checkExist(String filepath) throws IOException {
        String substring = filepath.substring(filepath.lastIndexOf(".") + 1);
        File file = new File(filepath);
        if (!file.exists()) {//判断文件目录不存在
            file.mkdir();
        }
        if (!substring.equals("") && substring == null && !file.isDirectory()) {
            file.createNewFile();//创建文件
        }
        return file;
    }

    /**
     * 判断路径是否存在，否：创建此路径
     *
     * @param dir      文件路径
     * @param realName 文件名
     * @throws IOException
     */
    public static File mkdirsmy(String dir, String realName) throws IOException {
        File file = new File(dir, realName);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        return file;
    }

    /**
     * 获取文件上下文路径
     *
     * @param name
     * @return
     * @throws IOException
     */
    public static String getPath(String name) throws IOException {
        return new File(name).getCanonicalPath();
    }


    /**
     * 获取文件夹下所有文件，包括子文件夹下的文件
     *
     * @param path
     * @return java.util.List<java.io.File>
     */
    public static List<File> getFiles(String path) {
        List<File> list = new ArrayList<File>();
        File file = new File(path); // 获取其file对象
        File[] fs = file.listFiles(); // 遍历path下的文件和目录，放在File数组中
        for (File f : fs) { // 遍历File[]数组
            // 若是目录，则递归该目录下的文件
            if (f.isDirectory()) {
                // 调用自身,查找子目录
                getFiles(f.getAbsolutePath());
            } else {
                list.add(f);
            }
        }
        return list;
    }

    /**
     * 删除某个文件夹下的所有文件夹和文件
     *
     * @param path
     * @return
     */
    public static void deleteFileAll(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            String[] fileList = file.list();
            for (int i = 0; i < fileList.length; i++) {
                File delFile = new File(path, fileList[i]);
                if (!delFile.isDirectory()) {
                    delFile.delete();
                } else if (delFile.isDirectory()) {
                    deleteFileAll(path + File.separator + fileList[i]);
                }
            }
        }
        file.delete();
    }


    public static void main(String[] args) {
        DelayedMessage delayedMessage = new DelayedMessage(1, "1111111111", 10000);
        Class<? extends DelayedMessage> clazz = delayedMessage.getClass();
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);
            try {
                Object o = field.get(delayedMessage);
                System.out.println(clazz.getSimpleName() + "  " + field.getName() + "  " + o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        System.out.println("--------------------");
        Arrays.stream(clazz.getFields()).forEach(System.out::println);
        System.out.println("--------------------");
        System.out.println(clazz.getSimpleName());

        System.out.println(StandardCharsets.UTF_8);

    }
}
