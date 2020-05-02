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


        System.out.println("==================== 测试拼接效率 ====================");


        String clazz = "class";
        String method = "method";

        long start1 = System.nanoTime();
        String join = String.join(".", clazz, method);
        long end1 = System.nanoTime();
        GoSystem.println("String.join耗时:", end1 - start1);

        long start2 = System.nanoTime();
        String add = clazz + "." + method;
        long end2 = System.nanoTime();
        GoSystem.println("使用+号耗时:", end2 - start2);

        long start3 = System.nanoTime();
        StringBuilder stringBuilder = new StringBuilder(clazz);
        stringBuilder.append(".");
        stringBuilder.append(method);
        String s1 = stringBuilder.toString();
        long end3 = System.nanoTime();
        GoSystem.println("StringBuilder耗时:", end3 - start3);

        long start4 = System.nanoTime();
        StringBuffer stringBuffer = new StringBuffer(clazz);
        stringBuffer.append(".");
        stringBuffer.append(method);
        String s = stringBuffer.toString();
        long end4 = System.nanoTime();
        GoSystem.println("StringBuffer耗时:", end4 - start4);

        long start5 = System.nanoTime();
        String format = String.format("%s.%s", clazz, method);
        long end5 = System.nanoTime();
        GoSystem.println("String.format %s.%s耗时:", end5 - start5);

        long start6 = System.nanoTime();
        String format1 = String.format("%1$s.%2$s", clazz, method);
        long end6 = System.nanoTime();
        GoSystem.println("String.format %1$s.%2$s耗时:", end6 - start6);

    }
}
