package com.bajins.clazz;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.Locale;
import java.util.Properties;

/**
 * System原生API的使用示例
 */
public class GoSystem {
    /**
     * 拼接字符串并打印
     *
     * @param str
     * @return
     */
    public static void println(String... str) {
        StringBuffer sb = new StringBuffer();
        for (String s : str) {
            sb.append(s).append(" ");
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
        String hostName = addr.getHostName();

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

        // 用户的主目录
        String home = props.getProperty("user.home");

        // 获取系统账户
        String username = System.getProperty("user.name");

        // 获取本机的IP地址
        String hostAddress = addr.getHostAddress();


        try {
            // 获取本机MAC地址
            NetworkInterface ni = NetworkInterface.getByInetAddress(addr);
            //ni.getInetAddresses().nextElement().getAddress();
            byte[] mac = ni.getHardwareAddress();

            String sMAC = null;
            Formatter formatter = new Formatter();
            for (int i = 0; i < mac.length; i++) {
                sMAC = formatter.format(Locale.getDefault(), "%02X%s", mac[i], (i < mac.length - 1) ? "-" : "").toString();
            }
            System.out.println(sMAC);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
