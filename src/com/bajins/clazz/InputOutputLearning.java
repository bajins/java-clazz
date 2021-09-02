package com.bajins.clazz;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * 文件IO操作
 *
 * @see java.io
 * @see java.nio
 * @see java.net
 * @see AutoCloseable
 * @see Closeable
 * @see Channel
 * @see AsynchronousChannel
 */
public class InputOutputLearning {
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

    /**
     * 复制文件，执行了几个读和写操作try的数据，效率低
     *
     * @param resource
     * @param target
     * @throws Exception
     */
    public static void copyFile(File resource, File target) throws IOException {
        long start = System.currentTimeMillis();
        try (FileInputStream inputStream = new FileInputStream(resource);// 文件输入流并进行缓冲
             //BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             FileOutputStream outputStream = new FileOutputStream(target);// 文件输出流并进行缓冲
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            // 缓冲数组
            // 大文件 可将 1024 * 2 改大一些，但是 并不是越大就越快
            byte[] bytes = new byte[1024 * 2];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                bufferedOutputStream.write(bytes, 0, len);
            }
            // 刷新输出缓冲流
            bufferedOutputStream.flush();
        }
        long end = System.currentTimeMillis();

        System.out.println("耗时：" + (end - start) / 1000 + " s");
    }

    /**
     * 使用FileChannel复制，根据官方文档说明应该比文件流复制的速度更快
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(source);// 文件输入流并进行缓冲
             FileOutputStream outputStream = new FileOutputStream(dest);// 文件输出流并进行缓冲
             FileChannel inputChannel = inputStream.getChannel();
             FileChannel outputChannel = outputStream.getChannel()) {

            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }


    public static void main(String[] args) {
        // https://www.cnblogs.com/fortunely/p/14051310.html
        Path path = Paths.get("foo", "bar", "baz.txt");
        System.out.println(path.resolve("foo"));
        System.out.println(path.resolveSibling("foo"));
        System.out.println(path.relativize(Paths.get("bar")));
        System.out.println(path.getFileName());
        System.out.println(path.getFileSystem());
        System.out.println(Paths.get("bar", "11").getFileName());
    }
}
