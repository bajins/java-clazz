package com.bajins.clazz;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 涉及日期时间相关包的原生API
 */
public class GoTime {

    public static void main(String[] args) {

        // 设置一个基于时间的时间量
        Duration duration = Duration.ofMinutes(10);
        // 分钟转毫秒
        long l1 = duration.toMillis();
        System.out.println(l1);

        // 前面是新值单位，后面是传入值的单位：毫秒转纳秒
        long convert = TimeUnit.NANOSECONDS.convert(l1, TimeUnit.MILLISECONDS);
        System.out.println(convert);
        // 纳秒转毫秒
        long l2 = TimeUnit.NANOSECONDS.toMillis(convert);
        System.out.println(l2);

        // 返回当前JVM的高精度时间。该方法只能用来测量时段而和系统时间无关。
        // 它的返回值是从某个固定但随意的时间点开始的（可能是未来的某个时间）。不同的JVM使用的起点可能不同。
        // 相同的代码在不同机器运行导致结果可能不同。所以它很少用来计算，通常都是测量先后顺序和时间段
        long nanoTime = System.nanoTime();
        System.out.println(nanoTime);
        long convert1 = TimeUnit.NANOSECONDS.convert(20000, TimeUnit.MILLISECONDS) + nanoTime;
        System.out.println(convert1);
        long convert2 = TimeUnit.NANOSECONDS.convert(convert1 - System.nanoTime(), TimeUnit.NANOSECONDS);
        System.out.println(convert2);

        System.out.println("======================");

        // 默认系统时区
        ZoneId zoneId = ZoneId.systemDefault();
        // 设置时区
        ZoneId of = ZoneId.of("Asia/Shanghai");
        // 设置时区的偏移量
        ZoneOffset zoneOffset = ZoneOffset.ofHours(8);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        // 基于系统时间的毫秒
        long ctm = System.currentTimeMillis();
        System.out.println(ctm);
        // Date获取时间戳
        long time = new Date().getTime();
        System.out.println(time);
        // LocalDate获取时间戳
        long epochMilli = LocalDate.now().atStartOfDay().toInstant(zoneOffset).toEpochMilli();
        System.out.println(epochMilli);
        // LocalDateTime获取时间戳
        long toEpochMilli = LocalDateTime.now().toInstant(zoneOffset).toEpochMilli();
        System.out.println(toEpochMilli);

        // 传入毫秒时间戳和指定时区偏移量，转换为LocalDateTime
        LocalDateTime localDateTime1 = new Date(l2 + ctm).toInstant().atZone(of).toLocalDateTime();
        //LocalDateTime localDateTime1 = new Date(l2 + ctm).toInstant().atOffset(zoneOffset).toLocalDateTime();
        System.out.println(localDateTime1.format(dateTimeFormatter));
        LocalDateTime localDateTime2 = Instant.ofEpochMilli(l2 + ctm).atZone(of).toLocalDateTime();
        //LocalDateTime localDateTime2 = Instant.ofEpochMilli(l2 + ctm).atOffset(zoneOffset).toLocalDateTime();
        System.out.println(localDateTime2.format(dateTimeFormatter));

        // String 转 LocalDateTime
        LocalDateTime parse = LocalDateTime.parse("2020-04-28T00:52:53.072");
        System.out.println(parse);
        LocalDateTime parse1 = LocalDateTime.parse("2020-04-28 00:52:53.816", dateTimeFormatter);
        System.out.println(parse1);

        // LocalDateTime 转 String
        String format = parse.format(dateTimeFormatter);
        System.out.println(format);

        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now.format(dateTimeFormatter));

        // 增加时间
        LocalDateTime localDateTime3 = now.plusSeconds(l2);
        System.out.println(localDateTime3.format(dateTimeFormatter));

        // LocalDateTime转换为Date
        Date from = Date.from(localDateTime1.toInstant(zoneOffset));
        // Date转换为LocalDateTime
        LocalDateTime localDateTime = new Date().toInstant().atZone(zoneId).toLocalDateTime();

        // 前面一个时间 在 后面一个时间 之前
        boolean after = now.isAfter(localDateTime1);
        System.out.println(after);
        // 前面一个时间 在 后面一个时间 之后
        boolean before = now.isBefore(localDateTime1);
        System.out.println(before);
        // 前面一个时间 和 后面一个时间 相等
        boolean equals = now.equals(localDateTime1);
        System.out.println(equals);
        // 比较两个时间：1前面一个比后面一个晚，-1前面一个比后面一个早，0同一时刻
        int compareTo = now.compareTo(localDateTime1);
        System.out.println(compareTo);

        // 计算两个时间差
        Duration between = Duration.between(now, localDateTime1);
        System.out.println(between.toMinutes());

        System.out.println("============= 可使用的默认格式化格式 =============");
        System.out.println(now.format(DateTimeFormatter.ISO_LOCAL_DATE));
        //System.out.println(now.format(DateTimeFormatter.ISO_OFFSET_DATE));
        System.out.println(now.format(DateTimeFormatter.ISO_DATE));
        System.out.println(now.format(DateTimeFormatter.ISO_LOCAL_TIME));
        //System.out.println(now.format(DateTimeFormatter.ISO_OFFSET_TIME));
        System.out.println(now.format(DateTimeFormatter.ISO_TIME));
        System.out.println(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        //System.out.println(now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        //System.out.println(now.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        System.out.println(now.format(DateTimeFormatter.ISO_DATE_TIME));
        System.out.println(now.format(DateTimeFormatter.ISO_ORDINAL_DATE));
        System.out.println(now.format(DateTimeFormatter.ISO_WEEK_DATE));
        //System.out.println(now.format(DateTimeFormatter.ISO_INSTANT));
        System.out.println(now.format(DateTimeFormatter.BASIC_ISO_DATE));
        //System.out.println(now.format(DateTimeFormatter.RFC_1123_DATE_TIME));


        System.out.println("============= 创建定时任务 =============");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("TimerTask1 run" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            }
        }, 1000, 5000);//延时1s，之后每隔5s运行一次

        // cheduleWithFixedDelay跟schedule类似，而scheduleAtFixedRate与scheduleAtFixedRate一样会尽量减少漏掉调度的情况
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5);
        executorService.scheduleWithFixedDelay(() -> {
            String now1 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            System.out.println("ScheduledThreadPoolExecutor1 run:" + now1);
        }, 1, 2, TimeUnit.SECONDS);
    }
}
