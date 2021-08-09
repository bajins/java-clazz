package com.bajins.clazz;

/**
 * 函数需要返回多个参数，有以下方式
 * <p>
 * 集合类：Map、List、Set、数组，类型都必须一致为 Object
 * 封装对象：普通对象（复用性不高）、利用泛型自定义元组
 * </p>
 * <p>
 * 元组和列表list一样，都可能用于数据存储，包含多个数据；<br/>
 * 但和列表不同的是：列表只能存储相同的数据类型，而元组不一样，它可以存储不同的数据类型
 * 这里使用泛型方法实现，利用参数类型推断，编译器可以找出具体的类型
 * </p>
 * https://stackoverflow.com/questions/521171/a-java-collection-of-value-pairs-tuples
 */
public class Tuple {
    public static <A, B> TwoTuple<A, B> tuple(A a, B b) {
        return new TwoTuple<>(a, b);
    }

    /**
     * 一个triplet
     *
     * @param a
     * @param b
     * @param c
     * @param <A>
     * @param <B>
     * @param <C>
     * @return
     */
    public static <A, B, C> ThreeTuple<A, B, C> tuple(A a, B b, C c) {
        return new ThreeTuple<>(a, b, c);
    }

    public static class TwoTuple<A, B> {
        public final A first;
        public final B second;

        public TwoTuple(A a, B b) {
            first = a;
            second = b;
        }
    }

    public static class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
        public final C third;

        public ThreeTuple(A a, B b, C c) {
            super(a, b);
            third = c;
        }
    }
}
