package com.bajins.clazz;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.StringJoiner;

/**
 * System原生API的使用示例
 *
 * @see System
 */
public class SystemLearning {


    /**
     * 获取所在类的行信息
     *
     * @param thread
     * @return
     */
    public static String getStackTraceInfo(Thread thread) {
        if (thread == null) {
            thread = Thread.currentThread();
        }
        StackTraceElement[] es = thread.getStackTrace();
        if (es.length <= 2) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("->", "", ": ");
        // 由于getStackTrace()返回自身，而getStackTraceInfo()不需要返回，所以在显示时-2
        /*for (int i = 0, len = es.length - 2; i < len; i++) {
            // 层级是自顶向下，所以输出的时候是反过来的
            StackTraceElement ei = es[es.length - i - 1];
            joiner.add(String.format("%s.%s#%d", ei.getClassName(), ei.getMethodName(), ei.getLineNumber()));
        }*/
        StackTraceElement ei = es[es.length - 1];
        joiner.add(String.format("%s.%s#%d", ei.getClassName(), ei.getMethodName(), ei.getLineNumber()));
        return joiner.toString();
    }


    /**
     * 拼接对象并打印
     *
     * @param objects
     * @return
     */
    public static void println(Object... objects) {
        StringBuilder sb = new StringBuilder(getStackTraceInfo(null));
        for (Object o : objects) {
            sb.append(o).append(" ");
        }
        System.out.println(sb.toString());
    }


    public static void main(String[] args) {
        // 从系统中检索主机名为本地主机的地址
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // 获取本机名称
        String hostName = addr != null ? addr.getHostName() : null;
        // 获得系统属性集
        Properties props = System.getProperties();
        // 操作系统名称
        String name = props.getProperty("os.name");
        // CPU构架
        String arch = props.getProperty("os.arch");
        // 操作系统版本
        String version = props.getProperty("os.version");
        // 用户的当前工作目录
        String dir = props.getProperty("user.dir");
        //String userDir = com.sun.javafx.runtime.SystemProperties.getProperty("userDir");
        // 获取用户桌面路径
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File desktopDir =fileSystemView.getHomeDirectory();
        String desktopPath = desktopDir.getAbsolutePath();
        System.out.println(desktopPath);

        // 获取系统账户
        String username = System.getProperty("user.name");
        // 获取本机的IP地址
        String hostAddress = addr != null ? addr.getHostAddress() : null;

        System.out.println(System.getenv("temp")); // 缓存目录
        System.out.println(System.getenv("tmp"));
        System.out.println(System.getProperty("java.io.tmpdir")); // 系统默认缓存目录
        System.out.println(System.getenv("userprofile")); // 用户目录
        System.out.println(System.getenv("homepath"));
        System.out.println(System.getProperty("user.home")); // 用户的主目录
        System.out.println(System.getenv("localappdata")); // 程序数据目录
        System.out.println(System.getenv("appdata"));
        System.out.println(System.getProperty("file.separator")); // 路径分隔符
        System.out.println(System.getProperty("path.separator"));
        System.out.println(System.getProperty("line.separator")); // 多个路径分隔符
        // 获取当前系统换行符
        // sun.security.action.GetPropertyAction
        /*GetPropertyAction getPropertyAction = new GetPropertyAction("line.separator");
        String lineSeparator = AccessController.doPrivileged(getPropertyAction);
        System.err.println("获取当前系统换行符：" + lineSeparator);*/
        System.err.println("获取当前系统换行符：" + System.lineSeparator());

        System.out.println(System.getProperty("sun.jnu.encoding")); // 操作系统默认字符编码
        System.out.println(System.getProperty("file.encoding")); // 操作系统文件的字符编码
        System.out.println(Charset.defaultCharset()); // 操作系统文件的字符编码

        System.out.println(System.getenv()); // 系统变量
        System.getProperties().list(System.out);

        System.out.println("---------- 项目绝对路径 -------------");
        System.out.println(SystemLearning.class.getResource("/")); // 如果不加“/”，则获取当前类的目录
        System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
        System.out.println(SystemLearning.class.getClassLoader().getResource(""));
        System.out.println(System.getProperty("user.dir"));
        System.out.println(System.getProperty("java.class.path"));
    }
}
