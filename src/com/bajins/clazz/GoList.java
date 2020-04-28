package com.bajins.clazz;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * List原生API使用示例
 */
public class GoList {
    public static void main(String[] args) {

        List<String> list = Arrays.asList("a","b","c");
        System.out.println("------------- 随机取值 -------------");
        // 方法一
        int index = (int) (Math.random()* list.size());
        System.out.println(list.get(index));

        // 方法二：shuffle 打乱顺序
        Collections.shuffle(list);
        System.out.println(list.get(0));
    }
}
