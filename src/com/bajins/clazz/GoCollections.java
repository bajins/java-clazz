package com.bajins.clazz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoCollections {
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
    }
}
