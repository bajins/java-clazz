package com.bajins.clazz;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 涉及日期时间相关包的原生API
 * <p>
 * time包下的所有
 * <p>
 * util包下的：Date、Timer、TimerTask、Calendar
 * <p>
 * util.concurrent包下的：TimeUnit
 */
public class Time {

    /**
     * 获取一天的开始时间
     *
     * @param date
     * @return
     */
    public static Date toDayStart(Date date) {
        // Calendar calendar = new GregorianCalendar();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取一天的结束时间
     *
     * @param date
     * @return
     */
    public static Date toDayEnd(Date date) {
        // Calendar calendar = new GregorianCalendar();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

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

        // 计算去年
        LocalDateTime previousYear = now.minus(1, ChronoUnit.YEARS);
        // 减30天
        LocalDateTime previousDay = now.minus(30, ChronoUnit.DAYS);
        // 减少6天
        LocalDateTime startTime = now.minusDays(6);

        // Date转换为LocalDateTime
        LocalDateTime localDateTime = new Date().toInstant().atZone(zoneId).toLocalDateTime();
        // 或者
        LocalDateTime localDateTime4 = LocalDateTime.ofInstant(new Date().toInstant(), zoneId);
        LocalDate localDate = localDateTime.toLocalDate();
        // 转LocalTime
        LocalTime localTime = localDateTime.toLocalTime();

        // LocalDateTime转换为Date
        Date from = Date.from(localDateTime1.toInstant(zoneOffset));
        // 使用ZonedDateTime将LocalDate转换为Instant。
        Instant instant = localDate.atStartOfDay(zoneId).toInstant();
        // 使用from()方法从Instant对象获取Date的实例
        Date date = Date.from(instant);
        // LocalDateTime转Date
        Date date1 = Date.from(localDateTime.atZone(zoneId).toInstant());

        // 计算今天是星期几
        int week = now.getDayOfWeek().getValue();
        // 计算上周日的日期
        LocalDateTime endTime = now.minusDays(week);

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

        // 计算前一个时间到后一个时间的持续时间（已过时间或剩余时间）
        Duration betweenTimeLeft = Duration.between(now, now.plusMinutes(10));
        System.out.print("计算还剩时间：");
        System.out.println(betweenTimeLeft.toMinutes());
        Duration betweenElapsedTime = Duration.between(now, now.minus(5, ChronoUnit.MINUTES));
        System.out.print("计算已过时间：");
        System.out.println(betweenElapsedTime.toMinutes());

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


        // 获取`Calendar`的实例
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 22);// 将日期设置为这个月的第几天
        calendar.get(Calendar.DAY_OF_MONTH);// 这个月的第几天,返回值是int
        calendar.get(Calendar.DAY_OF_YEAR);// 这一年的第几天
        calendar.add(Calendar.DAY_OF_MONTH, 3);// 在现有的日期上加3天
        calendar.getTime();// 返回`Date()`
        calendar.setTime(new Date());// 将日期设置为某日期

        calendar.setTime(date);//设置时间
        calendar.add(Calendar.YEAR, -1);//当前时间减去一年，即一年前的时间
        calendar.add(Calendar.MONTH, -1);//当前时间前去一个月，即一个月前的时间
        calendar.getTime();//获取一年前的时间，或者一个月前的时间
        int year = calendar.get(Calendar.YEAR);//获取年
        int month = calendar.get(Calendar.MONTH - 1);//获取月，因为第一个月是0，所以要- 1
        int day = calendar.get(Calendar.DATE);//获取日
        int first = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);//获取本月最小天数
        int last = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获取本月最大天数
        int time1 = calendar.get(Calendar.HOUR_OF_DAY);//获取当前小时
        int min = calendar.get(Calendar.MINUTE);//获取当前分钟
        int sec = calendar.get(Calendar.SECOND);//获取当前秒

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT> 08:00"));//获取东八区时间
    }
}
