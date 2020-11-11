package com.bajins.clazz.threadlearning;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 使用Callable和Future创建线程
 * <p>
 * 1. 定义一个类实现`Callable`接口，并重写`call()`方法，该`call()`方法将作为线程执行体，并且有返回值
 * 2. 创建Callable实现类的实例，使用FutureTask类来包装Callable对象
 * 3. 使用FutureTask对象作为Thread对象的target创建并启动线程
 * 4. 调用FutureTask对象的get()方法来获得子线程执行结束后的返回值
 */
public class ThreadImplCallable implements Callable<String> {

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
        FutureTask<String> futureTask = new FutureTask<String>(new ThreadImplCallable());
        for (int i = 0; i < 20; i++) {
            new Thread(futureTask, String.valueOf(i)).start();
        }
        try {
            System.out.println("子线程的返回值：" + futureTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
