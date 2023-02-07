package com.bajins.clazz.workersexecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * 6种方式创建线程
 *
 * @see Callable
 * @see RunnableFuture
 * @see RunnableScheduledFuture
 * @see ScheduledFuture
 * @see Future
 * @see FutureTask
 * @see CompletableFuture
 * @see Thread
 * @see Runnable
 * @see CountDownLatch 计数器
 * @see CyclicBarrier 循环栅栏：一个可循环利用的屏障：让所有线程都等待完成后才会继续下一步行动 https://www.jianshu.com/p/043ac5689002
 * @see Phaser 线程屏障同步器
 * @see ForkJoinPool 用于把大的计算任务，拆分为多个小的计算任务 https://blog.csdn.net/beFocused/article/details/104443000
 * @see RecursiveAction
 * @see RecursiveTask
 * @see ForkJoinTask
 * @see CountedCompleter
 * <ul>
 *     <li>https://blog.csdn.net/Xp545945/article/details/106402529</li>
 *     <li>https://blog.csdn.net/beFocused/article/details/104565841</li>
 * </ul>
 */
public class ThreadExample {

    // 用于计数线程是否执行完成
    private static final CountDownLatch countDownLatch = new CountDownLatch(10);

    public static void main(String[] args) {
        // 直接在函数体使用，匿名内部类，其实也是属于继承`Thread`创建线程类实现方式的特例
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        // 休眠3秒
                        Thread.sleep(1000 * 3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                    System.out.println(Thread.currentThread().getName());
                }
            });
            thread.start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() {
                //System.out.println("展示线程：" + Thread.currentThread().getName());
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //countDownLatch.countDown();
                }
                return "展示线程：" + Thread.currentThread().getName();
            }
        });
        for (int i = 0; i < 20; i++) {
            new Thread(futureTask, String.valueOf(i)).start();
        }
    }

    /**
     * 继承`Thread`类创建线程类
     */
    public static class ThreadExtends extends Thread {
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

    /**
     * 实现`Runnable`接口创建线程
     */
    public static class ThreadImplRunnable implements Runnable {
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

    /**
     * 使用Callable和Future创建线程
     * <p>
     * 1. 定义一个类实现`Callable`接口，并重写`call()`方法，该`call()`方法将作为线程执行体，并且有返回值
     * 2. 创建Callable实现类的实例，使用FutureTask类来包装Callable对象
     * 3. 使用FutureTask对象作为Thread对象的target创建并启动线程
     * 4. 调用FutureTask对象的get()方法获得子线程执行结束后的返回值，多个线程执行同一个FutureTask时，只会返回最后一个执行完成的线程执行结果
     */
    public static class ThreadImplCallable implements Callable<String> {

        @Override
        public String call() throws Exception {
            try {
                // 休眠3秒
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Thread.currentThread().getName();
        }

        public static void main(String[] args) {
            // 多个线程执行同一个FutureTask的时候，只会计算一次
            FutureTask<String> futureTask = new FutureTask<>(new ThreadImplCallable());
            /*FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
                @Override
                public String call() {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        //countDownLatch.countDown();
                    }
                    return Thread.currentThread().getName();
                }
            });*/
            for (int i = 0; i < 20; i++) {
                new Thread(futureTask, String.valueOf(i)).start();
            }
            try {
                // 判断futureTask是否计算完成（类似于cas自旋锁）
                while (!futureTask.isDone()) {

                }
                System.out.println("子线程的返回值：" + futureTask.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            /**
             * 多个任务，开启多线程去执行，并依次获取返回的执行结果
             */

            //创建一个FutureTask list来放置所有的任务
            List<FutureTask<String>> futureTasks = new ArrayList<>();
            for (Integer i = 0; i < 10; i++) {
                FutureTask<String> futureTask1 = new FutureTask<>(new ThreadImplCallable());
                futureTasks.add(futureTask1);
                new Thread(futureTask1).start();
            }

            // 根据任务数，依次的去获取任务返回的结果，这里获取结果时会依次返回，若前一个没返回，则会等待，阻塞
            for (FutureTask futureTask1 : futureTasks) {
                try {
                    System.out.println(futureTask1.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 定义一个支持拆分计算的任务，继承RecursiveAction类
     */
    private static class RecursiveActionExtends extends RecursiveAction {
        private int start;
        private int end;
        private final int MAX_NUM = 100;

        /**
         * 构造实例传入任务需要的参数
         */
        public RecursiveActionExtends(int start, int end) {
            this.start = start;
            this.end = end;
        }

        /**
         * 具体执行计算的任务的抽象方法重写
         */
        @Override
        protected void compute() {
            String tName = Thread.currentThread().getName();
            if ((end - start) < MAX_NUM) {
                System.out.println(tName + "    start:" + start);
                System.out.println(tName + "    end:" + end);
            } else {
                int middle = (start + end) / 2;
                /**
                 * 大任务拆分为两个小任务
                 */
                RecursiveActionExtends subTask1 = new RecursiveActionExtends(start, middle);
                RecursiveActionExtends subTask2 = new RecursiveActionExtends(middle, end);
                // 分别执行两个小任务
                subTask1.fork();
                subTask2.fork();
            }
        }

        /**
         * 执行计算任务
         */
        public static void main(String[] args) throws InterruptedException {
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            RecursiveActionExtends printTask = new RecursiveActionExtends(1, 300);
            // 线程池提交任务
            forkJoinPool.submit(printTask);
            boolean b = forkJoinPool.awaitTermination(1, TimeUnit.SECONDS); // 执行任务
            // 关闭提交接口
            forkJoinPool.shutdown();
        }
    }

    /**
     * 定义一个支持拆分计算的任务，继承RecursiveTask类(可以有返回值)
     */
    private static class RecursiveTaskExtends extends RecursiveTask<Integer> {
        private int start;
        private int end;
        private final int MAX_NUM = 30;

        /**
         * 构造实例传入任务需要的参数
         */
        public RecursiveTaskExtends(int start, int end) {
            this.start = start;
            this.end = end;
        }

        /**
         * 具体执行计算的任务的抽象方法重写
         */
        @Override
        protected Integer compute() {
            Integer count = 0;
            if ((end - start) < MAX_NUM) {
                System.out.println("start:" + start);
                System.out.println("end:" + end);
                for (int i = start; i < end; i++) {
                    count += i;
                }
                return count;
            }
            int middle = (start + end) / 2;
            /**
             * 大任务拆分为两个小任务，
             */
            RecursiveTaskExtends subTask1 = new RecursiveTaskExtends(start, middle);
            RecursiveTaskExtends subTask2 = new RecursiveTaskExtends(middle, end);
            // 分别执行两个小任务
            subTask1.fork();
            subTask2.fork();
            return subTask1.join() + subTask2.join(); // 合并两个子任务结果

        }

        /**
         * 执行计算任务
         */
        public static void main(String[] args) throws InterruptedException, ExecutionException {
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            RecursiveTaskExtends calcCountTask = new RecursiveTaskExtends(1, 101);
            // 线程池提交任务
            ForkJoinTask<Integer> forkJoinTask = forkJoinPool.submit(calcCountTask);
            // 获取执行结果
            System.out.println(forkJoinTask.get());
            // 关闭提交接口
            forkJoinPool.shutdown();
        }
    }
}
