package com.bajins.clazz;

import java.io.*;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;


/**
 * 文件IO操作
 *
 * @see java.io
 * @see Reader 字符读
 * @see Writer 字符写
 * @see FilterReader 自定义过滤 字符读
 * @see FilterWriter 自定义过滤 字符写
 * @see InputStream 字节读
 * @see OutputStream 字节写
 * @see DataInput 基本数据类型与字符串类型 字节读
 * @see DataOutput 基本数据类型与字符串类型 字节写
 * @see ObjectInput 对象 字节读
 * @see ObjectOutput 对象 字节写
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

    /**
     * 复制流
     *
     * <pre>
     * ByteArrayOutputStream baos = cloneInputStream(input);
     * // 打开两个新的输入流
     * InputStream stream = new ByteArrayInputStream(baos.toByteArray());
     * </pre>
     *
     * @param input
     * @return
     */
    private static ByteArrayOutputStream cloneInputStream(InputStream input) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        }
    }

    public static void main(String[] args) {
        // https://www.cnblogs.com/fortunely/p/14051310.html
        //Path path = Paths.get("foo", "bar", "baz.txt");
        //System.out.println(path.resolve("foo"));
        //System.out.println(path.resolveSibling("foo"));
        //System.out.println(path.relativize(Paths.get("bar")));
        //System.out.println(path.getFileName());
        //System.out.println(path.getFileSystem());
        //System.out.println(Paths.get("bar", "11").getFileName());


        File file = new File("C:\\Users\\claer\\Desktop\\ESN_ULPK_MAC145.bin");
        //ByteBuffer buffer = ByteBuffer.allocateDirect(5);

        try (RandomAccessFile raf = new RandomAccessFile(file, "r");) {
            raf.seek(441); // 偏移
            //int bytes = raf.skipBytes(441); // 要跳过的字节数
            StringJoiner joiner = new StringJoiner(":");
            for (int i = 0; i < 6; i++) {
                joiner.add(Integer.toHexString(raf.read()));
                //System.out.println(Integer.toString(raf.read(), 16));
            }
            System.out.println(joiner);
            //FileChannel channel = raf.getChannel();
            /*channel.read(buffer);
            buffer.flip();
            // Will be between 0 and Constants.BUFFER_SIZE
            int sizeInBuffer = buffer.remaining();*/

            /*MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_WRITE, 441, 5);
            System.out.println(buf);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("--------");

        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream in = new BufferedInputStream(fileInputStream);
             //ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        ) {
            long skip = in.skip(441); // 要跳过的字节数
            StringJoiner joiner = new StringJoiner(":");
            for (int i = 0; i < 6; i++) {
                joiner.add(Integer.toHexString(in.read()));
            }
            System.out.println(joiner);
            /*byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                System.out.println(Integer.toHexString(size));
                out.write(temp, 0, size);
            }
            byte[] content = out.toByteArray();
            System.out.println(Arrays.toString(content));*/
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         * BufferedInputStream 缓冲字节流读
         * BufferedOutputStream 缓冲字节流写
         * BufferedReader
         * BufferedWriter
         * ByteArrayInputStream 字节数组流读
         * ByteArrayOutputStream 字节数组流写
         * CharArrayReader
         * CharArrayWriter
         * DataInputStream
         * DataOutputStream
         * FileInputStream
         * FileOutputStream
         * FileReader
         * FileWriter
         * FilterInputStream
         * FilterOutputStream
         * InputStreamReader
         * OutputStreamWriter
         * LineNumberReader
         * ObjectInputStream
         * ObjectOutputStream
         * PipedInputStream
         * PipedOutputStream
         * PipedReader
         * PipedWriter
         * PrintStream
         * PrintWriter
         * PushbackInputStream
         * PushbackReader
         * RandomAccessFile
         * SequenceInputStream
         * StringReader
         * StringWriter
         *
         */
        // 字节流读写文本文件
        try (FileInputStream fis = new FileInputStream("text.txt");) {
            System.out.println("可读取的字节数：" + fis.available());
            int data;
            StringBuilder sb = new StringBuilder();
            while ((data = fis.read()) != -1) {
                sb.append((char) data);
            }
            System.out.println(sb);
        } catch (IOException e) {
            e.getStackTrace();
        }
        try (OutputStream fos = new FileOutputStream("text.txt", true);) {
            fos.write("test".getBytes());
        } catch (IOException e) {
            e.getStackTrace();
        }
        // 字符流读写文本文件
        try (FileReader fr = new FileReader("text.txt");
             BufferedReader br = new BufferedReader(fr);) {
            StringJoiner sj = new StringJoiner(System.lineSeparator());
            String line;
            while ((line = br.readLine()) != null) {//读取一行数据，返回字符串
                sj.add(line);
            }
            //String text = br.lines().collect(Collectors.joining(System.lineSeparator())); // 读取所有行
            System.out.println(sj);
        } catch (IOException e) {
            e.getStackTrace();
        }
        try (Scanner sc = new Scanner(new File("text.txt"))) {
            StringJoiner sj = new StringJoiner(System.lineSeparator());
            while (sc.hasNextLine()) {
                sj.add(sc.nextLine());
            }
            System.out.println(sj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter fw = new FileWriter("text.txt");
             BufferedWriter bw = new BufferedWriter(fw);) {
            bw.write("test");
        } catch (IOException e) {
            e.getStackTrace();
        }
        try (FileInputStream fis = new FileInputStream("text.txt");
             DataInputStream dis = new DataInputStream(fis);
             InputStreamReader isr = new InputStreamReader(dis);
             BufferedReader d = new BufferedReader(isr);) {
            StringJoiner sj = new StringJoiner(System.lineSeparator());
            String line;
            while ((line = d.readLine()) != null) {
                sj.add(line);
            }
            System.out.println(sj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream fos = new FileOutputStream("test1.txt");
             DataOutputStream out = new DataOutputStream(fos);) {
            out.writeBytes("test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
