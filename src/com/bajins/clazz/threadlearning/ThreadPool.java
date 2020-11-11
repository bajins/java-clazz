package com.bajins.clazz.threadlearning;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池
 */
public class ThreadPool {
    public static void main(String[] args) {
        // 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        // 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
        //ExecutorService executorService = Executors.newCachedThreadPool();
        // 创建一个定长线程池，支持定时及周期性任务执行。
        //ExecutorService executorService = Executors.newScheduledThreadPool();
        for (int i = 0; i < 10; i++) {
            // 执行
            executorService.execute(new RunnableThread());
            // 提交配合shutdown使用
            //executorService.submit(futureTask);
        }
        //executorService.shutdown();
    }
}

class RunnableThread implements Runnable {

    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("线程" + Thread.currentThread() + " " + i);
        }
    }
}