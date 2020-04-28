package com.bajins.clazz.delayqueue;


import java.time.Duration;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延迟队列消息
 *
 * @author claer woytu.com
 * @program com.bajins.common.utils.delayqueue
 * @description Message
 * @create 2019-03-16 21:16
 */
public class DelayedMessage implements Delayed {

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