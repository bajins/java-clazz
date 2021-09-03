package com.bajins.clazz.workersexecutors;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池<br/>
 * https://www.cnblogs.com/bbgs-xc/p/12830939.html
 * https://blog.csdn.net/smile_Running/article/details/91409942
 * https://blog.csdn.net/wwj17647590781/article/details/117992344
 *
 * @see Executor
 * @see Executors
 * @see ExecutorService
 * @see ThreadPoolExecutor
 * @see CountDownLatch 使一个线程等待其他线程各自执行完毕后再执行 https://zhuanlan.zhihu.com/p/148231820
 * @see CyclicBarrier 计数器更像一个阀门，需要所有线程都到达，然后继续执行，计数器递增，提供reset功能，可以多次使用
 * https://zhuanlan.zhihu.com/p/265530418
 * https://blog.csdn.net/qq_39241239/article/details/87030142
 * https://blog.csdn.net/qq_38293564/article/details/80558157
 * @see Semaphore 信号量，控制同时访问特定资源的线程数量 https://www.cnblogs.com/crazymakercircle/p/13907012.html
 */
public class ThreadPool {

    // 用于计数线程是否执行完成
    private static final CountDownLatch countDownLatch = new CountDownLatch(10);

    public static void main(String[] args) {
        // 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        // 创建一个自增线程池，如果线程池可用线程数超过处理需要线程数，则新建线程，任务全部完成后,子线程会处于等待状态,不会死亡
        //ExecutorService executorService = Executors.newCachedThreadPool();

        // 创建一个定长线程池，支持定时及周期性任务执行。
        // schedule 线程在加入线程池时，会延时执行
        // scheduleAtFixedRate 当前任务没有执行完成，如果到达第二个任务执行时间，此时会等待
        // scheduleWithFixedDelay 当前任务结束后再次等一个间隔时间再进行执行
        //ExecutorService executorService = Executors.newScheduledThreadPool(5);

        // 初始化一个单线程的线程池
        //ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

        // 创建一个具有抢占式操作（并行操作）的线程池，每个线程都有一个任务队列存放任务（窃取算法），JDK1.8新增
        // 默认将available processors（最大同时执行线程数）作为其目标并行度级别，使用ForkJoinPool实现
        // 该work stealing池维护足以支持给定并行度级别的线程，并使用多个队列来减少争用。不能保证提交任务的执行顺序
        // 并行度级别对应于活动参与或可用于参与任务处理的最大线程数。实际的线程数可能会动态增长和收缩。
        //ExecutorService executorService = Executors.newWorkStealingPool();
        //System.out.println(Runtime.getRuntime().availableProcessors()); // CPU 核数
        for (int i = 0; i < 10; i++) {
            // 执行
            executorService.execute(new ThreadExample.ThreadImplRunnable());
            // 提交配合shutdown使用
            //executorService.submit(futureTask);
        }
        //executorService.shutdown();

        /**
         * 手动创建一个线程池
         */
        final int corePoolSize = 2; // 核心线程数
        final int maximumPoolSize = 5; // 最大线程数
        final long keepAliveTime = 1L; // 存活时间，线程没有任务执行时最多保持多久时间会终止
        // 阻塞队列，存储等待执行的任务
        final LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(3);
        // 默认拒绝策略：请求的线程>(阻塞队列大小 + 最大线程数 = 8)，到第9个线程来获取线程池中的线程时，就会抛出异常退出
        // AbortPolicy 拒绝并抛出异常。
        // CallerRunsPolicy 重试提交当前的任务，即再次调用运行该任务的execute()方法。
        // DiscardOldestPolicy 抛弃队列头部（最旧）的一个任务，并执行当前任务。
        // DiscardPolicy 抛弃当前任务。
        ThreadPoolExecutor.AbortPolicy abortPolicy = new ThreadPoolExecutor.AbortPolicy();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                TimeUnit.MILLISECONDS, blockingQueue, Executors.defaultThreadFactory(), abortPolicy);
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                for (int j = 0; j < 10; j++) {
                    System.out.println("线程" + Thread.currentThread() + " " + j);
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class TestWorkStealing {

        public static void main(String[] args) {
            // 创建一个具有抢占式操作的线程池 1。8 之后新增 每个线程都有一个任务队列存放任务
            ExecutorService executorService =
                    Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
            // CPU 核数
            System.out.println("CPU核数：" + Runtime.getRuntime().availableProcessors());
            //CountDownLatch countDownLatch = new CountDownLatch(20); // 计数器

            long start = System.currentTimeMillis();
            LinkedBlockingDeque<Future<String>> strings = new LinkedBlockingDeque<>();
            AtomicInteger a = new AtomicInteger(); // 原子
            for (int i = 1; i <= 20; i++) {
                Future<String> submit = executorService.submit(new Callable<String>() {
                    @Override
                    public String call() {
                        try {
                            TimeUnit.SECONDS.sleep(2);
                            a.getAndIncrement(); // 以原子的方式将当前值加 1，注意，这里返回的是自增前的值，也就是旧值
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            //countDownLatch.countDown();
                        }
                        return Thread.currentThread().getName();
                    }
                });
                strings.offer(submit);
            }
            /*try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //System.out.println("计数器统计：" + countDownLatch.getCount());
            executorService.shutdown();

            strings.forEach(f -> {
                try {
                    System.out.println(f.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            // 注意位置，如果CountDownLatch或CyclicBarrier没有启用的情况下在此处获取值才是正确的
            System.out.println("原子整数结果：" + a.get());
            System.out.println("执行共耗时：" + ((System.currentTimeMillis() - start) / 1000) + "s");
        }
    }
}
