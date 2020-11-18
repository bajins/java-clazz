package com.bajins.clazz.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义注解处理
 */
public class AnnotationUtil {

    /**
     * 获取注解中对方法的描述信息
     *
     * @param clazz 类
     * @return 方法描述
     */
    public static <T extends Annotation> T getAnnotate(Class<T> clazz) {
        // 获取该类下的所有注解
        Annotation[] annotations = clazz.getAnnotations();
        // 获取所有构造函数
        Constructor<?>[] constructors = clazz.getConstructors();
        // 获取所有字段
        Field[] fields = clazz.getFields();
        // 泛型声明声明的类型
        TypeVariable<Class<T>>[] typeParameters = clazz.getTypeParameters();
        Package aPackage = clazz.getPackage();
        // 获取该类中的方法
        Method[] methods = clazz.getMethods();

        Map<String, Object> map = new HashMap<>(methods.length);
        for (Method method : methods) {
            EnableLog annotation = method.getAnnotation(EnableLog.class);
            map.put(method.getName(), annotation);
            Parameter[] parameters = method.getParameters();
            Arrays.stream(parameters).forEach(x -> {
                // 参数的注解，可能有多个注解
                Annotation[] annotations1 = x.getAnnotations();
            });
            // 方法的所有参数注解
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        }
        System.out.println(map);
        return (T) clazz.getAnnotation(EnableLog.class);
    }

    public static void main(String[] args) {

    }
}
