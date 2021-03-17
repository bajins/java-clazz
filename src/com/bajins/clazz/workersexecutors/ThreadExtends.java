package com.bajins.clazz.workersexecutors;

/**
 * 继承`Thread`类创建线程类
 */
public class ThreadExtends extends Thread{
    public void run() {
        try {
            // 休眠3秒
            Thread.sleep(1000 * 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            ThreadExtends threadTest = new ThreadExtends();
            threadTest.start();
        }
    }
}
