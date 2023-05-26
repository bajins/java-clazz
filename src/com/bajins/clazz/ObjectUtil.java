package com.bajins.clazz;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.Collection;
import java.util.Map;

/**
 * 对象工具类
 *
 * @author claer https://www.bajins.com
 * @program com.bajins.api.utils
 * @create 2018-06-10 22:48
 */
public class ObjectUtil {


    /**
     * 判断对象是否为空引用或大小为0
     * <p>
     * isAssignableFrom() 方法是从类继承的角度去判断，判断是否为某个类的父类。
     * instanceof 关键字是从实例继承的角度去判断，判断是否某个类的子类。
     * </p>
     *
     * @param obj
     * @return boolean
     */
    public static boolean isEmpty(Object obj) {
        System.out.println(obj instanceof CharSequence); // 判断实例对象是否为某个类或其子类
        System.out.println(CharSequence.class.isAssignableFrom(obj.getClass())); // 判断前面一个是否为后面一个类或其父类
        // 判断是否为空引用，也就是判断在堆内存中对象是否存在
        if (obj instanceof CharSequence) {
            // CharSequence是一个接口,用于表示有序的字符集合,String实现了它
            return ((CharSequence) obj).length() == 0;
        } else if (obj instanceof Collection) {
            // isEmpty先获取size的值在判断再返回
            // list.add(null) 会造成 isEmpty() 为 false, size() 为 1
            return ((Collection<?>) obj).isEmpty();
        } else if (obj instanceof Map) {
            // isEmpty先获取size的值在判断再返回
            return ((Map<?, ?>) obj).isEmpty();
        } else if (obj.getClass().isPrimitive()) { // 判断对象是int,float,double,boolean,char,short,long,void 或 其包装类

        } else if (obj instanceof GenericArrayType) {
            // 可能是Type对象，通过 {@link Method#getGenericReturnType()}或{@link Method#getGenericParameterTypes()} 而获取的泛型数组
            return Array.getLength(obj) == 0;
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        return false;
    }

    /**
     * 判断对象是否不为空或大小大于0
     *
     * @param obj
     * @return boolean
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 序列化对象
     *
     * @param object
     * @return
     * @throws IOException
     */
    public static byte[] serialize(Object object) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos);) {
            oos.writeObject(object);
            return baos.toByteArray();
        }
    }

    /**
     * 反序列化对象
     *
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais);) {
            return ois.readObject();
        }
    }

    public static void main(String[] args) {
        System.out.println(isEmpty(""));
    }
}