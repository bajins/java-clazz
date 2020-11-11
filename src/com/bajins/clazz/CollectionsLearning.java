package com.bajins.clazz;

import com.sun.istack.internal.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionsLearning {

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


    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("111");
        list.add("222");
        list.add("333");
        list.add("444");

        Collections.sort(list); // 顺序排列
        Collections.reverse(list); // 倒序排列
        Collections.shuffle(list); // 混乱排序
        List<Object> objects = Collections.emptyList();

        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);
        intList.add(3);
        intList.add(4);
        intList.add(5);
        // 将一个list按三个一组分成N个小的list，分组后的list不再是原list的视图，原list的改变不会影响分组后的结果

        // 通过grouping by
        Map<Integer, List<Integer>> groups = intList.stream().collect(Collectors.groupingBy(s -> (s - 1) / 3));
        List<List<Integer>> subSets = new ArrayList<>(groups.values());
        List<Integer> lastPartition = subSets.get(2);

        // 通过partition by
        // partitioningBy 其实是一种特殊的 groupingBy，
        // 它依照条件测试的是否两种结果来构造返回的数据结构，get(true) 和 get(false) 能即为全部的元素对象
        Map<Boolean, List<Integer>> groups1 = intList.stream().collect(Collectors.partitioningBy(s -> s > 3));
        List<List<Integer>> subSets1 = new ArrayList<>(groups.values());
        List<Integer> lastPartition1 = subSets.get(1);

        // 原始
        List<List<String>> lists = splitList(list, 2);

    }
}
