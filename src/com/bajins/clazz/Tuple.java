package com.bajins.clazz;

/**
 * 函数需要返回多个参数，有以下方式
 * <p>
 * 集合类：Map、List、Set、数组，类型都必须一致为 Object
 * 封装对象：普通对象（复用性不高）、利用泛型自定义元组
 * </p>
 * <p>
 * 元组Tuple（Pair二元元组、Triple三元元组、多元元组）和列表list一样，都可能用于数据存储，包含多个数据；<br/>
 * 但和列表不同的是：列表只能存储相同的数据类型，而元组不一样，它可以存储不同的数据类型<br/>
 * 这里使用泛型方法实现，利用参数类型推断，编译器可以找出具体的类型
 * </p>
 * https://stackoverflow.com/questions/521171/a-java-collection-of-value-pairs-tuples
 */
public class Tuple {
    /**
     * 一个Pair
     *
     * @param a
     * @param b
     * @param <A>
     * @param <B>
     * @return
     */
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

    public static <A, B, C, D> FourTuple<A, B, C, D> tuple(A a, B b, C c, D d) {
        return new FourTuple<>(a, b, c, d);
    }

    public static <A, B, C, D, E> FiveTuple<A, B, C, D, E> tuple(A a, B b, C c, D d, E e) {
        return new FiveTuple<>(a, b, c, d, e);
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

    public static class FourTuple<A, B, C, D> extends ThreeTuple<A, B, C> {
        public final D fourth;

        public FourTuple(A a, B b, C c, D d) {
            super(a, b, c);
            fourth = d;
        }
    }

    public static class FiveTuple<A, B, C, D, E> extends FourTuple<A, B, C, D> {
        public final E fifth;

        public FiveTuple(A a, B b, C c, D d, E e) {
            super(a, b, c, d);
            fifth = e;
        }
    }

    public static void main(String[] args) {
        FourTuple<String, Integer, Double, Long> tuple = Tuple.tuple("1", 2, 3.0, 4L);
        System.out.println(tuple);
    }
}
