package com.bajins.clazz;

import java.util.Map;

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
public final class Tuple {
    /**
     * 一个Pair静态工厂方法
     *
     * @param a
     * @param b
     * @param <A>
     * @param <B>
     * @return
     */
    public static <A, B> TwoTuple<A, B> of(A a, B b) {
        return new TwoTuple<>(a, b);
    }

    /**
     * 一个triplet静态工厂方法
     *
     * @param a
     * @param b
     * @param c
     * @param <A>
     * @param <B>
     * @param <C>
     * @return
     */
    public static <A, B, C> ThreeTuple<A, B, C> of(A a, B b, C c) {
        return new ThreeTuple<>(a, b, c);
    }

    /**
     * 一个quadruplet静态工厂方法
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @param <A>
     * @param <B>
     * @param <C>
     * @param <D>
     * @return
     */
    public static <A, B, C, D> FourTuple<A, B, C, D> of(A a, B b, C c, D d) {
        return new FourTuple<>(a, b, c, d);
    }

    public static <A, B, C, D, E> FiveTuple<A, B, C, D, E> of(A a, B b, C c, D d, E e) {
        return new FiveTuple<>(a, b, c, d, e);
    }

    public static <A, B, C, D, E, F> SixTuple<A, B, C, D, E, F> of(A a, B b, C c, D d, E e, F f) {
        return new SixTuple<A, B, C, D, E, F>(a, b, c, d, e, f);
    }

    public static <A, B, C, D, E, F, G> SevenTuple<A, B, C, D, E, F, G> of(A a, B b, C c, D d, E e, F f, G g) {
        return new SevenTuple<>(a, b, c, d, e, f, g);
    }

    public static <A, B, C, D, E, F, G, H> EightTuple<A, B, C, D, E, F, G, H> of(A a, B b, C c, D d, E e, F f, G g,
                                                                                 H h) {
        return new EightTuple<>(a, b, c, d, e, f, g, h);
    }

    public static <A, B, C, D, E, F, G, H, I> NineTuple<A, B, C, D, E, F, G, H, I> of(A a, B b, C c, D d, E e, F f,
                                                                                      G g, H h, I i) {
        return new NineTuple<>(a, b, c, d, e, f, g, h, i);
    }

    public static <A, B, C, D, E, F, G, H, I, J> TenTuple<A, B, C, D, E, F, G, H, I, J> of(A a, B b, C c, D d, E e,
                                                                                           F f, G g, H h, I i, J j) {
        return new TenTuple<>(a, b, c, d, e, f, g, h, i, j);
    }

    public static <A, B, C, D, E, F, G, H, I, J, K> ElevenTuple<A, B, C, D, E, F, G, H, I, J, K> of(A a, B b, C c,
                                                                                                    D d, E e, F f,
                                                                                                    G g, H h, I i,
                                                                                                    J j, K k) {
        return new ElevenTuple<>(a, b, c, d, e, f, g, h, i, j, k);
    }

    public static <A, B, C, D, E, F, G, H, I, J, K, L> TwelveTuple<A, B, C, D, E, F, G, H, I, J, K, L> of(A a, B b,
                                                                                                          C c, D d,
                                                                                                          E e, F f,
                                                                                                          G g, H h,
                                                                                                          I i, J j,
                                                                                                          K k, L l) {
        return new TwelveTuple<>(a, b, c, d, e, f, g, h, i, j, k, l);
    }

    public static <A, B, C, D, E, F, G, H, I, J, K, L, M> ThirteenTuple<A, B, C, D, E, F, G, H, I, J, K, L, M> of(A a
            , B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m) {
        return new ThirteenTuple<>(a, b, c, d, e, f, g, h, i, j, k, l, m);
    }

    public static class TwoTuple<A, B> implements Map.Entry<A, B> {
        public final A first;
        public final B second;

        public TwoTuple(A a, B b) {
            first = a;
            second = b;
        }

        @Override
        public A getKey() {
            return first;
        }

        @Override
        public B getValue() {
            return second;
        }

        @Override
        public B setValue(B value) {
            return second;
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

    public static class SixTuple<A, B, C, D, E, F> extends FiveTuple<A, B, C, D, E> {
        public final F sixth;

        public SixTuple(A a, B b, C c, D d, E e, F f) {
            super(a, b, c, d, e);
            sixth = f;
        }
    }

    public static class SevenTuple<A, B, C, D, E, F, G> extends SixTuple<A, B, C, D, E, F> {
        public final G seventh;

        public SevenTuple(A a, B b, C c, D d, E e, F f, G g) {
            super(a, b, c, d, e, f);
            seventh = g;
        }
    }

    public static class EightTuple<A, B, C, D, E, F, G, H> extends SevenTuple<A, B, C, D, E, F, G> {
        public final H eighth;

        public EightTuple(A a, B b, C c, D d, E e, F f, G g, H h) {
            super(a, b, c, d, e, f, g);
            eighth = h;
        }
    }

    public static class NineTuple<A, B, C, D, E, F, G, H, I> extends EightTuple<A, B, C, D, E, F, G, H> {
        public final I ninth;

        public NineTuple(A a, B b, C c, D d, E e, F f, G g, H h, I i) {
            super(a, b, c, d, e, f, g, h);
            ninth = i;
        }
    }

    public static class TenTuple<A, B, C, D, E, F, G, H, I, J> extends NineTuple<A, B, C, D, E, F, G, H, I> {
        public final J tenth;

        public TenTuple(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j) {
            super(a, b, c, d, e, f, g, h, i);
            tenth = j;
        }
    }

    public static class ElevenTuple<A, B, C, D, E, F, G, H, I, J, K> extends TenTuple<A, B, C, D, E, F, G, H, I,
            J> {
        public final K eleventh;

        public ElevenTuple(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k) {
            super(a, b, c, d, e, f, g, h, i, j);
            eleventh = k;
        }
    }

    public static class TwelveTuple<A, B, C, D, E, F, G, H, I, J, K, L> extends ElevenTuple<A, B, C, D, E, F, G,
            H, I
            , J, K> {
        public final L twelfth;

        public TwelveTuple(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l) {
            super(a, b, c, d, e, f, g, h, i, j, k);
            twelfth = l;
        }
    }

    public static class ThirteenTuple<A, B, C, D, E, F, G, H, I, J, K, L, M> extends TwelveTuple<A, B, C, D, E, F
            , G,
            H, I, J, K, L> {
        public final M thirteenth;

        public ThirteenTuple(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m) {
            super(a, b, c, d, e, f, g, h, i, j, k, l);
            thirteenth = m;
        }
    }

    public static void main(String[] args) {
        FourTuple<String, Integer, Double, Long> tuple = Tuple.of("1", 2, 3.0, 4L);
        System.out.println(tuple);
    }
}
