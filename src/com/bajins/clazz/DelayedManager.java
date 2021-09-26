package com.bajins.clazz;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * 延时队列的使用示例
 *
 * @author claer woytu.com
 * @program com.bajins.common.utils.delayqueue
 * @description QueueManager
 * @create 2019-03-16 22:07
 */
public class DelayedManager {

    // 创建延时队列
    private static final DelayQueue<DelayedMessage> queue = new DelayQueue<>();
    // 线程池，消息队列中消息的延期时间到期时，自动启用一个线程消费添加到延时队列中的消息
    private static final ExecutorService exec = Executors.newSingleThreadExecutor();

    /**
     * 传入指定的毫秒进行初始化
     *
     * @param id
     * @param body        内容
     * @param millisecond 时间量：毫秒
     */
    public static void add(int id, String body, long millisecond) {
        queue.offer(new DelayedMessage(id, body, millisecond));
        execute();
    }

    /**
     * 传入指定基于时间的时间量和单位进行初始化
     *
     * @param id
     * @param body         内容
     * @param amountOfTime 时间量
     * @param timeUnit     时间单位
     */
    public static void add(int id, String body, long amountOfTime, TimeUnit timeUnit) {
        queue.offer(new DelayedMessage(id, body, amountOfTime, timeUnit));
        execute();
    }

    /**
     * 实例化一个延时消息并放到延时队列中
     *
     * @param id       消息id
     * @param body     消息内容
     * @param duration 延时的时间量
     */
    public static void add(int id, String body, Duration duration) {
        queue.offer(new DelayedMessage(id, body, duration));
        execute();
    }

    /**
     * 消费消息
     */
    public static void execute() {
        // 使用匿名内部类创建线程进行消费，此方式只适合创建一个消息就新建一个线程消费
        exec.execute(new Thread(() -> {
            try {
                DelayedMessage take = queue.take();
                System.out.println(take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        // 实现Runnable接口进行消费，此方式可以在添加完多个消息后进行消费，亦可添加单个后进行消费
        //exec.execute(new DelayedConsumer(queue));
        //exec.shutdown();
    }

    public static void main(String[] args) {
        //将延时消息放到延时队列中，延时15分钟
        add(3, "hello2", 10000 * 6 * 15);
        //将延时消息放到延时队列中，延时1分钟
        add(3, "hello2", Duration.ofMinutes(1));
        //将延时消息放到延时队列中，延时10s
        add(2, "hello3", Duration.ofSeconds(10));
        //将延时消息放到延时队列中，延时3s
        add(1, "world", 3, TimeUnit.SECONDS);

        // 实现Runnable接口进行消费，此方式可以在添加完多个消息后进行消费，亦可添加单个后进行消费
        //exec.execute(new DelayedConsumer(queue));
        //exec.shutdown();

    }

    /**
     * 实现Runnable接口进行消费，此方式可以在添加完多个消息后进行消费，亦可添加单个后进行消费
     */
    public static class DelayedConsumer implements Runnable {
        // 延时队列 ,消费者从其中获取消息进行消费
        private DelayQueue<DelayedMessage> queue;

        public DelayedConsumer(DelayQueue<DelayedMessage> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    DelayedMessage take = queue.take();
                    System.out.println(take);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 延迟队列消息
     *
     * @author https://www.bajins.com
     * @program com.bajins.common.utils.delayqueue
     * @description Message
     * @create 2019-03-16 21:16
     */
    public static class DelayedMessage implements Delayed {

        private int id;
        // 消息内容
        private String body;
        // 延迟时长（单位为纳秒），到期时间点的时间戳，判断延时时长。
        private long executeTime;

        /**
         * 传入指定的毫秒进行初始化
         *
         * @param id
         * @param body        内容
         * @param millisecond 时间量：毫秒
         */
        public DelayedMessage(int id, String body, long millisecond) {
            this.id = id;
            this.body = body;
            // 前面是新值单位，后面是传入值的单位：毫秒转纳秒
            long convert = TimeUnit.NANOSECONDS.convert(millisecond, TimeUnit.MILLISECONDS);
            this.executeTime = convert + System.nanoTime();
        }

        /**
         * 传入指定基于时间的时间量和单位进行初始化
         *
         * @param id
         * @param body         内容
         * @param amountOfTime 时间量
         * @param timeUnit     时间单位
         */
        public DelayedMessage(int id, String body, long amountOfTime, TimeUnit timeUnit) {
            this.id = id;
            this.body = body;
            // 前面是新值单位，后面是传入值的单位
            long convert = TimeUnit.NANOSECONDS.convert(amountOfTime, timeUnit);
            this.executeTime = convert + System.nanoTime();
        }

        /**
         * 传入基于时间的时间量进行初始化
         *
         * @param id
         * @param body     内容
         * @param duration 时间量
         */
        public DelayedMessage(int id, String body, Duration duration) {
            this.id = id;
            this.body = body;
            this.executeTime = duration.toNanos() + System.nanoTime();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public long getExecuteTime() {
            return executeTime;
        }

        public void setExecuteTime(long executeTime) {
            this.executeTime = executeTime;
        }

        /**
         * 返回剩余指定时间到当前时间的时间戳，如果返回的是负数则说明到期，否则还没到期
         *
         * @param unit
         * @return
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.executeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        /**
         * 时间设定小的 优先被消费
         *
         * @param delayed
         * @return
         */
        @Override
        public int compareTo(Delayed delayed) {
            DelayedMessage o = (DelayedMessage) delayed;
            return this.executeTime - o.getExecuteTime() > 0 ? 1 : -1;
        }

        @Override
        public String toString() {
            return "DelayedMessage{" +
                    "id=" + id +
                    ", body='" + body + '\'' +
                    ", excuteTime=" + executeTime +
                    '}';
        }
    }
}