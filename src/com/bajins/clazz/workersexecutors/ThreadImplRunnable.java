package com.bajins.clazz.workersexecutors;

/**
 * 实现`Runnable`接口创建线程
 */
public class ThreadImplRunnable implements Runnable {
    public void run() {
        try {
            // 休眠3秒
            Thread.sleep(1000 * 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            ThreadImplRunnable rtt = new ThreadImplRunnable();
            new Thread(rtt, String.valueOf(i)).start();
        }
    }
}
