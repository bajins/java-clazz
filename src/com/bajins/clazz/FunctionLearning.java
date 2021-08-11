package com.bajins.clazz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

/**
 * 在方法中传递函数，做到方法重用
 */
public class FunctionLearning {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        List<String> strings = Apply7.apListToUpperCase(list);
        List<String> stringList = Apply8.mapListToLowerCase(list);
        List<String> strings1 = Apply8.newListToLowerCase(list);
        List<String> test = Apply8.applyList(list, (r, t) -> r + t, "test");
    }
}

/**
 * Java8使用lambda 表达式、方法引用、函数接口（@FunctionalInterface只有一个抽象方法的接口）等实现函数式编程
 *
 * @see java.util.function 包下有默认的一系列定义好的函数接口
 * @see Consumer 接收一个对象，返回 void，比如 foreach
 * @see Function 接收一个对象，返回一个对象，比如 map
 * @see Predicate 接收一个对象，返回 boolean，比如 filter
 * @see Supplier 不接收对象，返回一个对象
 * @see BiConsumer 接受两个输入参数
 * @see BiFunction
 * @see BinaryOperator
 * @see BiPredicate
 * @see Comparator
 */
class Apply8 {
    /**
     * @param list
     * @param mapFn 函数接口
     * @return
     */
    public static List<String> mapList(List<String> list, Function<String, String> mapFn) {
        List<String> newList = new ArrayList<>();
        for (String word : list) {
            newList.add(mapFn.apply(word));
        }
        return newList;
    }

    public static List<String> mapListToUpperCase(List<String> list) {
        return mapList(list, String::toUpperCase); // ::方法引用
    }

    public static List<String> mapListToLowerCase(List<String> list) {
        return mapList(list, String::toLowerCase);
    }

    public static List<String> mapListToRepeat(List<String> list) {
        return mapList(list, item -> item + item); // lambda 表达式
    }

    /////////////////////////////////////////////////////////////////////////////////////

    public static <R, T> List<String> newList(List<String> list, Function<R, T> mapFn) {
        List<String> newList = new ArrayList<>();
        for (String word : list) {
            newList.add((String) mapFn.apply((R) word));
        }
        return newList;
    }

    public static List<String> newListToLowerCase(List<String> list) {
        return newList(list, (Function<String, String>) String::toLowerCase);
    }

    public static List<String> newListToRepeat(List<String> list) {
        return newList(list, (Function<String, String>) item -> item + item); // lambda 表达式
    }


    ///////////////////////////////////////////////////////////////////////////////////////////

    @FunctionalInterface
    interface ApplyFunction<R, T> {
        T apply(R r, T t);
    }

    public static List<String> applyList(List<String> list, ApplyFunction<String, String> mapFn, String t) {
        List<String> newList = new ArrayList<>();
        for (String word : list) {
            newList.add(mapFn.apply(word, t));
        }
        return newList;
    }

}

/**
 * Java8之前使用方式：<br/>
 * 定义一个接口<br/>
 * 函数参数中接收接口，调用接口的方法，合并这些重复逻辑
 */
class Apply7 {

    interface ApplyItem<T> {
        T getNewItem(T item);
    }

    public static List<String> mapList(List<String> list, ApplyItem<String> mapItem) {
        List<String> newlist = new ArrayList<>();
        for (String word : list) {
            newlist.add(mapItem.getNewItem(word));
        }
        return newlist;
    }

    public static List<String> apListToUpperCase(List<String> list) {
        return mapList(list, new ApplyItem<String>() {
            @Override
            public String getNewItem(String item) {
                return item.toUpperCase();
            }
        });
    }
}

