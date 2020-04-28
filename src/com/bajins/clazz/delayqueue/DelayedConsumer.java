package com.bajins.clazz.delayqueue;

import java.util.concurrent.DelayQueue;

/**
 * 实现Runnable接口进行消费，此方式可以在添加完多个消息后进行消费，亦可添加单个后进行消费
 */
public class DelayedConsumer implements Runnable {
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
