package com.bajins.clazz.workersexecutors;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池<br/>
 * <p>https://blog.csdn.net/qq_33591903/article/details/106239102</p>
 * <p>https://www.cnblogs.com/bbgs-xc/p/12830939.html<p/>
 * <p>https://blog.csdn.net/smile_Running/article/details/91409942<p/>
 * <p>https://blog.csdn.net/wwj17647590781/article/details/117992344<p/>
 * <p>https://blog.csdn.net/qq_44828283/article/details/115585626<p/>
 * <p>https://www.cnblogs.com/duanxz/p/5056222.html</p>
 *
 * @see Executor 执行器接口
 * @see Executors 生产具体的执行器的静态工厂，固定尺寸的线程池、可变尺寸线程池
 * <ul>
 *     <li>Executors.newSingleThreadExecutor() 单线程线程池
 *          <p>只有一个线程的线程池，因此所有提交的任务是顺序执行，如果没有任务执行，那么线程会一直等待</p>
 *          <p>创建大小为1的固定线程池，同时执行任务(task)的只有一个,其它的（任务）task都放在LinkedBlockingQueue中排队等待执行</p>
 *     </li>
 *     <li>Executors.newCachedThreadPool() 缓存线程池
 *          <p>线程池里有很多线程需要同时执行，老的可用线程将被新的任务触发重新执行，如果线程超过60秒内没执行，那么将被终止并从池中删除</p>
 *          <p>使用时，放入线程池的task任务会复用线程或启动新线程来执行，注意事项：启动的线程数如果超过整型最大值后会抛出RejectedExecutionException异常</p>
 *     </li>
 *     <li>Executors.newFixedThreadPool() 固定线程数线程池
 *         <p> 拥有固定线程数的线程池，如果没有任务执行，那么线程会一直等待</p>
 *         <p>创建固定大小(nThreads,大小不能超过int的最大值)的线程池，缓冲任务的队列为LinkedBlockingQueue,
 *         <p>使用时，在同执行的任务数量超过传入的线程池大小值后，将会放入LinkedBlockingQueue，在LinkedBlockingQueue中的任务需要等待线程空闲后再执行
 *         ，如果放入LinkedBlockingQueue中的任务超过整型的最大数时，抛出RejectedExecutionException，线程的运行不受加入顺序的影响。</p>
 *     </li>
 *     <li>Executors.newScheduledThreadPool() 周期性线程的线程池
 *         <p>用来调度即将执行的任务的线程池</p>
 *     </li>
 *     <li>Executors.newSingleThreadScheduledExecutor() 单线程周期性线程池
 *         <p>只有一个线程，用来调度执行将来的任务</p>
 *         <p>线程keepAliveTime为0，缓存任务的队列为DelayedWorkQueue，注意不要超过整型的最大值</p>
 *     </li>
 *     <li>Executors.newWorkStealingPool() 任务窃取线程池
 *         <p>创建一个具有抢占式操作（并行操作）的线程池，每个线程都有一个任务队列存放任务（窃取算法），JDK1.8新增</p>
 *         <p>默认将available processors（最大同时执行线程数）作为其目标并行度级别，使用ForkJoinPool实现</p>
 *         <p>该work stealing池维护足以支持给定并行度级别的线程，并使用多个队列来减少争用。</p>
 *         <p>不能保证提交任务的执行顺序，并行度级别对应于活动参与或可用于参与任务处理的最大线程数。实际的线程数可能会动态增长和收缩。</p>
 *     </li>
 * </ul>
 * @see ExecutorService
 * @see AbstractExecutorService
 * @see DelegatedExecutorService
 * @see FinalizableDelegatedExecutorService
 * @see DelegatedScheduledExecutorService
 * @see ScheduledExecutorService
 * @see ThreadFactory 线程工厂，用于创建单个线程，减少手工创建线程的繁琐工作，同时能够复用工厂的特性
 * @see CompletionService
 * @see ExecutorCompletionService
 * @see ThreadPoolExecutor
 * @see ScheduledThreadPoolExecutor
 * @see CountDownLatch 使一个线程等待其他线程各自执行完毕后再执行 https://zhuanlan.zhihu.com/p/148231820
 * @see CyclicBarrier 计数器更像一个阀门，需要所有线程都到达，然后继续执行，计数器递增，提供reset功能，可以多次使用
 * https://zhuanlan.zhihu.com/p/265530418
 * https://blog.csdn.net/qq_39241239/article/details/87030142
 * https://blog.csdn.net/qq_38293564/article/details/80558157
 * @see Semaphore 信号量，控制同时访问特定资源的线程数量 https://www.cnblogs.com/crazymakercircle/p/13907012.html
 * @see RejectedExecutionHandler 拒绝策略
 * @see CallerRunsPolicy 不会真正丢弃任务，当线程池未关闭，该策略直接在调用者线程中运行当前丢弃任务
 * @see AbortPolicy 直接抛出异常，阻止应用正常工作
 * @see DiscardPolicy 丢弃无法处理的任务，不做任何处理
 * @see DiscardOldestPolicy 丢弃最老的任务，也就是即将执行的任务，并尝试提交当前任务
 */
public class ThreadPool {

    // 用于计数线程是否执行完成
    private static final CountDownLatch countDownLatch = new CountDownLatch(10);

    public static void main(String[] args) {
        // 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        // 创建一个自增线程池，如果线程池可用线程数少于处理需要线程数，则新建线程，任务全部完成后,子线程会处于等待状态,不会死亡
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
        System.out.println(Runtime.getRuntime().availableProcessors()); // CPU 核数
        for (int i = 0; i < 10; i++) {
            // 执行
            executorService.execute(new ThreadExample.ThreadImplRunnable());
            // 提交配合shutdown使用
           /* Future<?> future = executorService.submit(new ThreadExample.ThreadImplRunnable());
            // 可以取消执行
            future.cancel(true);

            try {
                if (future.get() != null) { // 可以获取返回结果，如果future.get()!=null 且无异常，表示执行成功
                    System.out.println("执行完成");
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }*/

            /*CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                // 异步执行的任务
                long startTime = System.currentTimeMillis();
                try {
                    Thread.sleep(2000); // 模拟耗时操作
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "异步执行耗时：" + (System.currentTimeMillis() - startTime) + "ms";
            }, executorService); // 使用线程池执行任务

            // 获取异步执行的结果
            future.thenAccept(result -> {
                System.out.println("结果：" + result);
            });

            // 如果需要阻塞等待结果并获取
            try {
                String result = future.get();
                System.out.println("阻塞获取的结果：" + result);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        }
        //executorService.shutdown(); // 停止接收新任务，原来的任务继续执行，再次submit()无效（关闭了提交通道）
        //executorService.shutdownNow(); // 停止接收新任务，原来的任务停止执行（正在运行和等待的）
        //executorService.awaitTermination(long timeOut, TimeUnit unit); // 当前线程阻塞，返回当前线程池是否已停止（true/false）

        /*
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
            countDownLatch.await(); // 等待所有线程都执行完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class TestWorkStealing {

        public static void main(String[] args) {
            // 创建一个具有抢占式操作的线程池 1。8 之后新增 每个线程都有一个任务队列存放任务
            ExecutorService executorService = Executors.newWorkStealingPool();
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
                countDownLatch.await(); // 等待所有线程都执行完成
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //System.out.println("计数器统计：" + countDownLatch.getCount());
            executorService.shutdown(); // 停止接收新任务，原来的任务继续执行，再次submit()无效（关闭了提交通道）

            strings.forEach(f -> {
                try {
                    System.out.println(f.get()); // 阻塞
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            // 注意位置，如果CountDownLatch或CyclicBarrier没有启用的情况下在此处获取值才是正确的
            System.out.println("原子整数结果：" + a.get());
            System.out.println("执行共耗时：" + ((System.currentTimeMillis() - start) / 1000) + "s");
        }
    }

    /**
     * 定义一个支持拆分计算的任务，无返回值
     */
    private static class PrintTaskRecursiveAction extends RecursiveAction {
        private int start;
        private int end;
        private final int MAXNUM = 100;

        /**
         * 构造实例传入任务需要的参数
         */
        public PrintTaskRecursiveAction(int start, int end) {
            this.start = start;
            this.end = end;
        }

        /**
         * 具体执行计算的任务的抽象方法重写
         */
        @Override
        protected void compute() {
            String tName = Thread.currentThread().getName();
            if ((end - start) < MAXNUM) {
                System.out.println(tName + "    start:" + start);
                System.out.println(tName + "    end:" + end);
            } else {
                int middle = (start + end) / 2;
                /*
                 * 大任务拆分为两个小任务，
                 */
                PrintTaskRecursiveAction subTask1 = new PrintTaskRecursiveAction(start, middle);
                PrintTaskRecursiveAction subTask2 = new PrintTaskRecursiveAction(middle, end);
                //分别执行两个小任务
                subTask1.fork();
                subTask2.fork();
            }
        }
    }


    /**
     * 定义一个支持拆分计算的任务，有返回值
     */
    private static class CalcNumCountTaskRecursiveTask extends RecursiveTask<Integer> {
        private int start;
        private int end;
        private final int MAXNUM = 30;

        /**
         * 构造实例传入任务需要的参数
         */
        public CalcNumCountTaskRecursiveTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        /**
         * 具体执行计算的任务的抽象方法重写
         */
        @Override
        protected Integer compute() {
            String tName = Thread.currentThread().getName();
            int count = 0;
            if ((end - start) < MAXNUM) {
                System.out.println("start:" + start);
                System.out.println("end:" + end);
                for (int i = start; i < end; i++) {
                    count += i;
                }
                return count;
            } else {
                int middle = (start + end) / 2;
                /*
                 * 大任务拆分为两个小任务，
                 */
                CalcNumCountTaskRecursiveTask subTask1 = new CalcNumCountTaskRecursiveTask(start, middle);
                CalcNumCountTaskRecursiveTask subTask2 = new CalcNumCountTaskRecursiveTask(middle, end);
                //分别执行两个小任务
                subTask1.fork();
                subTask2.fork();
                return subTask1.join() + subTask2.join();
            }
        }
    }

    public static class TestRecursive {
        public static void main(String[] args) throws ExecutionException, InterruptedException {
            // 定义一个支持拆分计算的任务，无返回值
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            PrintTaskRecursiveAction printTask = new PrintTaskRecursiveAction(1, 300);
            //线程池提交任务
            forkJoinPool.submit(printTask);
            forkJoinPool.awaitTermination(1, TimeUnit.SECONDS);
            //关闭提交接口
            forkJoinPool.shutdown();


            // 定义一个支持拆分计算的任务，有返回值
            forkJoinPool = new ForkJoinPool();
            CalcNumCountTaskRecursiveTask calcCountTask = new CalcNumCountTaskRecursiveTask(1, 101);
            //线程池提交任务
            ForkJoinTask<Integer> forkJoinTask = forkJoinPool.submit(calcCountTask);
            //获取执行结果
            System.out.println(forkJoinTask.get());
            //关闭提交接口
            forkJoinPool.shutdown();
        }
    }
}
