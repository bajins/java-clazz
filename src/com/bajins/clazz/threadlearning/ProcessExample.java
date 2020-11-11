package com.bajins.clazz.threadlearning;

import java.io.IOException;

/**
 * 进程
 */
public class ProcessExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        //如何用java语言开启一个进程

        //方式一：使用Runtime的exec方法
        Runtime.getRuntime().exec("notepad");
        //方式二：使用ProcessBuilder类中的start方法
        ProcessBuilder pb = new ProcessBuilder("notepad");
        Process p1 = pb.start();
        p1.waitFor();
    }
}
