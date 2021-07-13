package com.bajins.clazz;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

/**
 * 对象工具类
 *
 * @author claer https://www.bajins.com
 * @program com.bajins.api.utils
 * @create 2018-06-10 22:48
 * <p>
 * @see java.lang.reflect （reflection）反射：一个类的所有成员都可以进行反射操作，先得到类的字节码Class后再进行各种操作
 * @see Field 类的属性字段
 * @see Method 类的方法
 * @see Modifier
 * @see Parameter
 * @see Type
 * <p/>
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
        if (obj == null) {
            return true;
        } else if (obj instanceof CharSequence) {// CharSequence.class.isAssignableFrom(obj.getClass())
            // CharSequence是一个接口,用于表示有序的字符集合,String实现了它
            return ((CharSequence) obj).length() == 0;
        } else if (obj instanceof Collection) {// Collection.class.isAssignableFrom(obj.getClass())
            // isEmpty先获取size的值在判断再返回
            // list.add(null) 会造成 isEmpty() 为 false, size() 为 1
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {// Map.class.isAssignableFrom(obj.getClass())
            // isEmpty先获取size的值在判断再返回
            return ((Map) obj).isEmpty();
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
     * 使用Introspector进行map转bean
     *
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object mapToBeanByIntrospector(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = beanClass.newInstance();

        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            Method setter = property.getWriteMethod();
            if (setter != null) {
                setter.invoke(obj, map.get(property.getName()));
            }
        }
        return obj;
    }

    /**
     * 使用Introspector进行bean转map
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, Object> beanToMapByIntrospector(Object obj) throws IntrospectionException,
            InvocationTargetException, IllegalAccessException {
        if (isEmpty(obj)) {
            throw new IllegalArgumentException("传入的对象为空");
        }
        Map<String, Object> map = new HashMap<>();

        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            Method getter = property.getReadMethod();
            Object value = getter != null ? getter.invoke(obj) : null;
            map.put(property.getName(), value);
        }

        return map;
    }

    /**
     * 使用reflect进行map转bean
     *
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object mapToBeanByReflect(Map<String, Object> map, Class<?> beanClass) throws IllegalAccessException,
            InstantiationException {
        if (isEmpty(map)) {
            throw new IllegalArgumentException("传入的对象为空");
        }
        Object obj = beanClass.newInstance();

        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            field.set(obj, map.get(field.getName()));
        }
        return obj;
    }

    /**
     * 使用reflect进行bean转map
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, Object> beanToMapByReflect(Object obj) throws IllegalAccessException {
        if (isEmpty(obj)) {
            throw new IllegalArgumentException("传入的对象为空");
        }
        Map<String, Object> map = new HashMap<>();

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            //设置属性可以被访问
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }


    /**
     * 获取 类.方法
     *
     * @param thread Thread.currentThread()
     * @return java.lang.String
     */
    public static String getClassMethod(Thread thread) {
        StringBuilder method = new StringBuilder(thread.getStackTrace()[1].getClassName());
        method.append(".");
        method.append(thread.getStackTrace()[1].getMethodName());
        return method.toString();
    }

    /**
     * 获取 类.方法
     *
     * @param traceElement
     * @return
     */
    public static String getClassMethod(StackTraceElement traceElement) {
        StringBuilder method = new StringBuilder(traceElement.getClassName());
        method.append(".");
        method.append(traceElement.getMethodName());
        return method.toString();
    }

    /**
     * 获取 类.方法
     *
     * @param throwable new Throwable()
     * @return java.lang.String
     */
    public static String getClassMethod(Throwable throwable) {
        StringBuilder method = new StringBuilder(throwable.getStackTrace()[1].getClassName());
        method.append(".");
        method.append(throwable.getStackTrace()[1].getMethodName());
        return method.toString();
    }


    public static void main(String[] args) {
        System.out.println(isEmpty(""));
    }
}