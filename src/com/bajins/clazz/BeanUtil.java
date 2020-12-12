package com.bajins.clazz;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Java语言欠缺属性、事件、多重继承功能。
 * 如果要在Java程序中实现一些面向对象编程的常见需求，只能手写大量胶水代码/粘合代码（glue code），用途是粘合那些可能不兼容的代码。
 * <p>
 * Bean的含义是可重复使用的Java组件，所谓组件就是一个由可以自行进行内部管理的一个或几个类所组成、外界不了解其内部信息和运行方式的群体
 * <p>
 * Java Bean正是编写这套胶水代码的惯用模式或约定，必须达到如下条件：
 * <p>
 * 1、所有属性为private
 * 2、提供默认无参构造函数
 * 3、提供getter和setter
 * 4、可序列化：实现serializable接口
 * <p>
 * https://www.zhihu.com/question/19773379/answers/updated
 */
public class BeanUtil {
    /**
     * 合并对象
     *
     * @param origin      源数据
     * @param destination 目标对象，合并后数据
     * @param cover       true为覆盖，false为不覆盖
     * @param <T>
     */
    public static <T> void mergeObject(T origin, T destination, boolean cover) throws IllegalAccessException {
        if (origin == null || destination == null) {
            return;
        }
        // 如果不是同一个类
        if (!origin.getClass().equals(destination.getClass())) {
            return;
        }
        Field[] fields = destination.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field targetField = fields[i];
            // 如果属性为静态的
            if (Modifier.isStatic(targetField.getModifiers())) {
                continue;
            }
            if (!Modifier.isPublic(targetField.getDeclaringClass().getModifiers())) {
                targetField.setAccessible(true); // 设置属性可访问
            }
            Object valueD = targetField.get(origin); // 源数据
            Object valueO = targetField.get(destination); // 目标数据
            // 如果源数据不为空，且该属性不为序列化，可覆盖或目标数据为空
            if (null != valueD && !"serialVersionUID".equals(targetField.getName()) && (cover || null == valueO)) {
                targetField.set(destination, valueD); // 设值到目标对象属性
            }
            targetField.setAccessible(false);
        }

    }

    /**
     * 通过发现差异合并两个bean
     *
     * @param target
     * @param destination
     * @param <T>
     * @throws Exception
     */
    public static <T> void merge(T target, T destination) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
        // 迭代遍历所有属性
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            Method readMethod = descriptor.getReadMethod();
            // 仅复制可写属性
            if (readMethod != null) {
                Object originalValue = readMethod.invoke(target);
                // 仅复制目标值为null的值
                if (originalValue == null) {
                    Object defaultValue = readMethod.invoke(destination);
                    descriptor.getWriteMethod().invoke(target, defaultValue);
                }
            }
        }
    }
}
