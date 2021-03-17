package com.bajins.clazz.workersexecutors;

public class ThreadExample {
    public static void main(String[] args) {
        // 直接在函数体使用，匿名内部类，其实也是属于第二种实现方式的特例
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        // 休眠3秒
                        Thread.sleep(1000 * 3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName());
                }
            });
            thread.start();
        }
        // JDK1.8及以上
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    // 休眠3秒
                    Thread.sleep(1000 * 3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName());
            }).start();
        }
    }
}
