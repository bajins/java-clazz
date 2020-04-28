package com.bajins.clazz;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 生成唯一ID
 *
 * @author claer admin@bajins.com
 */
public class GenerateId {


    /**
     * 获取UUID
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成订单号,订单号的组成：
     * 两位年+两位月+两位日+两位小时+两位分+两位秒+当天的订单总数+1
     *
     * @param count
     * @return
     */
    public static String getGuid(int count) {
        // 获取年月日数字
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        // 获取时间戳
        String time = dateFormat.format(new Date());
        // 获取三位随机数
        // int ran=(int) ((Math.random()*9+1)*100);
        // 0 代表前面补充0
        // 15 代表长度为15
        // d 代表参数为正数型
        return time + String.format("%015d", count + 1);
    }

    /**
     * 根据时间和随机数生成29位唯一id
     *
     * @return
     */
    public static String getOrderIdByTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate = sdf.format(new Date());
        String valueOf = String.valueOf(((Math.random() * 9 + 1) * 10000000000L));
        int hashCode = valueOf.hashCode();
        if (hashCode < 0) {// 有可能是负数
            hashCode = -hashCode;
        }
        // 0 代表前面补充0
        // 15 代表长度为15
        // d 代表参数为正数型
        return newDate + String.format("%015d", hashCode);
    }

    /**
     * 根据UUID生成唯一ID
     *
     * @return
     */
    public static String getOrderIdByUUId() {
        int machineId = 1;// 最大支持1-9个集群机器部署
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {// 有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0
        // 15 代表长度为15
        // d 代表参数为正数型
        return machineId + String.format("%015d", hashCodeV);
    }

    public static void main(String[] args) {
        int count = 10000;// 设置并发数量
        // 线程池准备
        CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        long start = System.currentTimeMillis();// 开始时间
        for (int i = 0; i < count; i++) {
            executorService.execute(new GenerateId().new Task(cyclicBarrier, i));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();// 结束时间
        System.out.println("总共耗时!---------" + (end - start));// 总共耗时
    }

    /**
     * 实现多线程接口类
     */
    public class Task implements Runnable {
        private CyclicBarrier cyclicBarrier;
        private int count;

        public Task(CyclicBarrier cyclicBarrier, int count) {
            this.cyclicBarrier = cyclicBarrier;
            this.count = count;
        }

        @Override
        public void run() {
            try {
                // 等待所有任务准备就绪
                cyclicBarrier.await();

                //				String orderIdByTime = getOrderIdByTime();
                //				System.out.println(orderIdByTime);
                String orderIdByUUId = getOrderIdByUUId();
                System.out.println(orderIdByUUId);
            } catch (Exception e) {
                System.out.println("出现超时的线程" + count);
                e.printStackTrace();
            }
        }
    }

}