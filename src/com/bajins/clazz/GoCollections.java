package com.bajins.clazz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GoCollections {

    /**
     * 将一个list按三个一组分成N个小的list
     *
     * @param list
     * @param groupSize
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int groupSize) {
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
        List<List<String>> subSets2 = new ArrayList<>();
        int index = 100;
        for (int i = 0; i < list.size(); i += 100) {
            if (i + 100 > list.size()) {
                index = list.size() - i;
            }
            subSets2.add(list.subList(i, i + index));
        }

    }
}
