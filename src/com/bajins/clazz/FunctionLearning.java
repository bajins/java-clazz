package com.bajins.clazz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

/**
 * 在方法中传递函数，做到方法重用<br/>
 * 在使用的方法参数中接收定义的接口，方法内调用该接口的方法，合并这些重复逻辑，或反过来把非重复逻辑在接口方法中实现
 */
public class FunctionLearning {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
        List<String> strings = Apply7.apListToUpperCase(list);
        List<String> stringList = Apply8.mapListToLowerCase(list);
        List<String> strings1 = Apply8.newListToLowerCase(list);
        List<String> test = Apply8.applyList(list, (r, t) -> r + t, "test");
    }

    /**
     * Java8使用lambda表达式、方法引用、函数接口（@FunctionalInterface只能有一个抽象方法的接口）等实现函数式编程
     * <p>
     * https://www.jianshu.com/p/3c27dfd647f1
     * https://www.orchome.com/935
     *
     * @see java.util.function 包下有默认的一系列定义好的函数接口
     * @see Consumer 接收一个对象，返回 void，比如 foreach
     * @see BiConsumer 接收两个对象，返回 void
     * @see IntConsumer
     * @see LongConsumer
     * @see DoubleConsumer
     * @see Function 接收一个对象，返回一个对象，比如 map
     * @see BiFunction 接收两个对象，返回一个对象
     * @see BinaryOperator 继承自BiFunction
     * @see Predicate 接收一个对象，返回 boolean，比如 filter
     * @see BiPredicate 接收两个对象，返回 boolean
     * @see Supplier 不接收对象，返回一个对象
     * @see BooleanSupplier 不接收对象，返回 boolean
     * @see Comparator
     * @see UnaryOperator
     */
    static class Apply8 {
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
     * 一般是Java8之前版本的使用方式：定义一个接口，然后使用new对该接口实例化作为参数传入方法
     */
    static class Apply7 {

        interface ApplyItem<T> {
            T getNewItem(T item);
        }

        public static List<String> mapList(List<String> list, ApplyItem<String> mapItem) {
            List<String> newList = new ArrayList<>();
            for (String word : list) {
                newList.add(mapItem.getNewItem(word));
            }
            return newList;
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
}

