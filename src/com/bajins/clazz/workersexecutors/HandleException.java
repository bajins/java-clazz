package com.bajins.clazz.workersexecutors;

/**
 * 捕获异常
 * @see Thread
 */
public class HandleException {

    public static void main(String[] args) {
        // https://blog.csdn.net/wild46cat/article/details/89076832
        /* 在以下两种方法中如果抛出了新的异常则会被JVM忽略。*/
        // 捕获所有程序为直接处理的，由虚拟机抛出的异常。
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("出现异常");
                e.printStackTrace();
            }
        });

        int a = 10 / 0;
        System.out.println(a);

        // 捕获该线程中抛出的程序本身未处理的异常。
        Thread.currentThread().setUncaughtExceptionHandler((t1, e) -> {
            System.out.println("出现异常");
            e.printStackTrace();
        });
    }
}
