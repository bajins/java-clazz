package com.bajins.clazz;

import com.sun.istack.internal.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils {

    /**
     * 将一个list按三个一组分成N个小的list
     *
     * @param list      集合数据
     * @param groupSize 几个分割一组
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int groupSize) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        int length = list.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize;
        List<List<T>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            newList.add(list.subList(fromIndex, toIndex));
        }
        return newList;
    }

    /**
     * Java8 Stream分割list集合
     *
     * @param list      集合数据
     * @param splitSize 几个分割一组
     * @return 集合分割后的集合
     */
    public static <T> List<List<T>> splitListStream(List<T> list, int splitSize) {
        //判断集合是否为空
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        //计算分割后的大小
        int maxSize = (list.size() + splitSize - 1) / splitSize;
        //开始分割
        return Stream.iterate(0, n -> n + 1)
                .limit(maxSize)
                .parallel()
                .map(a -> list.parallelStream().skip(a * splitSize).limit(splitSize).collect(Collectors.toList()))
                .filter(b -> !b.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 分割list集合
     *
     * @param list      集合数据
     * @param splitSize 几个分割一组
     * @return 集合分割后的集合
     */
    public static <T> List<List<T>> splitListOriginal(List<T> list, int splitSize) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        Map<Integer, List<T>> map = new HashMap<Integer, List<T>>();
        if (splitSize <= 0) {
            splitSize = 1;
        }
        int k = 0;
        int size;
        for (int i = 0; i < (size = list.size()); i += splitSize) {
            List<T> newList;
            try {
                newList = list.subList(i, i + splitSize);
            } catch (Exception e) {
                newList = list.subList(i, size);
            }
            map.put(k, newList);
            k++;
        }
        List<List<T>> listAll = new ArrayList<List<T>>();
        for (List<T> value : map.values()) {
            listAll.add(value);
        }
        return listAll;
    }

    /**
     * 分割list集合
     *
     * @param source 集合数据
     * @param n      分成n个list
     * @return 集合分割后的集合
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        if (isEmpty(source)) {
            return Collections.emptyList();
        }
        List<List<T>> result = new ArrayList<>();
        int size = source.size();
        if (n <= 0) {
            n = 1;
        }
        //先计算出余数
        int remainder = size % n;
        //然后是商
        int number = size / n;
        //偏移量
        int offset = 0;
        for (int i = 0; i < n; i++) {
            List<T> value;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    /**
     * 分割list集合
     *
     * @param source 集合数据
     * @param n      几个分割一组
     * @return 集合分割后的集合
     */
    public static <T> List<List<T>> averageAssign1(List<T> source, int n) {
        if (isEmpty(source)) {
            return Collections.emptyList();
        }
        List<List<T>> result = new ArrayList<>();
        int size = source.size();
        if (n <= 0) {
            n = 1;
        }
        int remainder = size % n;
        int number = size / n;
        for (int i = 0; i < number; i++) {
            result.add(source.subList(i * n, (i + 1) * n));
        }
        if (remainder > 0) {
            result.add(source.subList(size - remainder, size));
        }
        return result;
    }

    /**
     * 判断集合是否为空
     *
     * @param collection
     * @return
     */
    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断map是否为空
     *
     * @param map
     * @return
     */
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 过滤条件 .filter(distinctByKey(User::getId))
     *
     * @param keyExtractor
     * @param <T>
     * @return
     */
    private <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> concurrentHashMap = new ConcurrentHashMap<>();
        return t -> concurrentHashMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("111");
        list.add("222");
        list.add("333");
        list.add("444");
        // 原始
        List<List<String>> lists = CollectionUtils.splitList(list, 2);
    }
}
