package com.bajins.clazz;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LRU、LFU、FIFO淘汰算法
 * <p>
 * 以ConcurrentHashMap作为缓存的存储结构。
 * 因为ConcurrentHashMap的线程安全的，所以基于此实现的LocalCache在多线程并发环境的操作是安全的。
 * 在JDK1.8中，ConcurrentHashMap是支持完全并发读，这对本地缓存的效率也是一种提升。
 * 通过调用ConcurrentHashMap对map的操作来实现对缓存的操作。
 *
 * @author claer https://www.bajins.com
 * @program com.bajins.api.utils.cache
 * @description LocalCache
 * @create 2018-12-20 21:40
 */
public class LocalCache {

    /**
     * 默认有效时长,单位:秒
     */
    private static final int DEFUALT_TIMEOUT = 3600;

    private static final long SECOND_TIME = 1000;

    private static final Map<String, Object> map;

    private static final Timer timer;

    /**
     * 初始化
     */
    static {
        timer = new Timer();
        map = new ConcurrentHashMap<>();
    }

    /**
     * 私有构造函数,工具类不允许实例化
     */
    private LocalCache() {

    }

    /**
     * 清除缓存任务类
     */
    static class CleanWorkerTask extends TimerTask {

        private String key;

        public CleanWorkerTask(String key) {
            this.key = key;
        }

        @Override
        public void run() {
            LocalCache.remove(key);
        }
    }

    /**
     * 增加缓存
     *
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        map.put(key, value);
        timer.schedule(new CleanWorkerTask(key), DEFUALT_TIMEOUT);
    }

    /**
     * 增加缓存
     *
     * @param key
     * @param value
     * @param timeout 有效时长
     */
    public static void put(String key, Object value, int timeout) {
        map.put(key, value);
        timer.schedule(new CleanWorkerTask(key), timeout * SECOND_TIME);
    }

    /**
     * 增加缓存
     *
     * @param key
     * @param value
     * @param expireTime 过期时间
     */
    public static void put(String key, Object value, Date expireTime) {
        map.put(key, value);
        timer.schedule(new CleanWorkerTask(key), expireTime);
    }

    /**
     * 批量增加缓存
     *
     * @param m
     */
    public static void putAll(Map<String, Object> m) {
        map.putAll(m);

        for (String key : m.keySet()) {
            timer.schedule(new CleanWorkerTask(key), DEFUALT_TIMEOUT);
        }
    }

    /**
     * 批量增加缓存
     *
     * @param m
     */
    public static void putAll(Map<String, Object> m, int timeout) {
        map.putAll(m);

        for (String key : m.keySet()) {
            timer.schedule(new CleanWorkerTask(key), timeout * SECOND_TIME);
        }
    }

    /**
     * 批量增加缓存
     *
     * @param m
     */
    public static void putAll(Map<String, Object> m, Date expireTime) {
        map.putAll(m);

        for (String key : m.keySet()) {
            timer.schedule(new CleanWorkerTask(key), expireTime);
        }
    }

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    public static Object get(String key) {
        return map.get(key);
    }

    /**
     * 查询缓存是否包含key
     *
     * @param key
     * @return
     */
    public static boolean containsKey(String key) {
        return map.containsKey(key);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public static void remove(String key) {
        map.remove(key);
    }

    /**
     * 删除缓存
     *
     * @param o
     */
    public static void remove(Object o) {
        map.remove(o);
    }

    /**
     * 返回缓存大小
     *
     * @return
     */
    public static int size() {
        return map.size();
    }

    /**
     * 清除所有缓存
     *
     * @return
     */
    public static void clear() {
        if (size() > 0) {
            map.clear();
        }
        timer.cancel();
    }

}
