package com.bajins.clazz;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * String的原生API使用示例
 */
public class GoString {


    public static void main(String[] args) {
        String[] addr = {"北京", "南京", "重庆", "西安"};
        // 数组转List
        List<String> list = Arrays.asList(addr);

        char[] charArray = {'a', 'b', 'c'};
        int[] intArray = {1, 2, 3, 4};

        System.out.println("------------- 固定分隔符 -------------");

        GoSystem.println("CharArray转String：", String.copyValueOf(charArray));

        // 必须将普通数组 boxed才能在 map 里面 toString
        String str2 = Arrays.stream(intArray).boxed().map(i -> i.toString()).reduce("", String::concat);
        GoSystem.println("IntArray转String：", str2);

        // // 必须将普通数组 boxed才能在 map 里面引用 Object::toString
        String str3 = Arrays.stream(intArray).boxed().map(Object::toString).reduce("", String::concat);
        GoSystem.println("IntArray转String：", str3);


        System.out.println("------------- 可自定义分隔符 -------------");

        // 必须将普通数组 boxed才能在 map 里面 toString
        String str1 = Arrays.stream(intArray).boxed().map(i -> i.toString()).collect(Collectors.joining(","));
        GoSystem.println("IntArray转String：", str1);

        GoSystem.println("StringArray转String：", Arrays.stream(addr).collect(Collectors.joining(",")));
        GoSystem.println("StringArray转String：", Arrays.toString(addr));

        GoSystem.println("Array转String：", String.join(",", addr));
        GoSystem.println("List转String：", String.join(",", list));
        GoSystem.println("List转String：", list.stream().collect(Collectors.joining(",")));

        String str = "abcdefg";
        String childStr = "c";
        // 获取子字符串在字符串中倒数第二次出现的下标
        int i = str.lastIndexOf(childStr, str.lastIndexOf(childStr) + 1);

        // 获取子字符串在字符串中第二次出现的下标
        int i1 = str.indexOf(childStr, str.indexOf(childStr) + 1);

        // 使用 java.util.concurrent.ThreadLocalRandom 来生成有边界的Int，专为多线程并发使用的随机数生成器
        int i2 = ThreadLocalRandom.current().nextInt(1, 10);

    }
}
