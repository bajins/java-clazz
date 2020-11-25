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
     * @param obj   实例对象
     * @param clazz 类
     * @return 方法描述
     */
    public static <T extends Annotation, E> T getAnnotate(Object obj, Class<T> clazz, Class<E> eClass) throws IllegalAccessException {
        // 父级的class
        Class<? super T> superclass = clazz.getSuperclass();

        // 获取该类下的所有注解
        Annotation[] annotations = clazz.getAnnotations();
        // 获取所有构造函数
        Constructor<?>[] constructors = clazz.getConstructors();

        // 返回一个数组，其中包含反映该类对象表示的类或接口的所有可访问公共字段的字段，包括继承的字段，但是只能是public
        Field[] fields = clazz.getFields();
        // 返回反映由类对象表示的类或接口声明的所有字段的字段对象数组。这包括公共、受保护、默认（包）访问和专用字段，但不包括继承的字段
        Field[] declaredFields = clazz.getDeclaredFields();
        // 获取父级的字段
        Field[] supDeclaredFields = superclass.getDeclaredFields();
        // 把当前类和父级的字段放在一个数组中
        Field[] allDeclaredFields = new Field[declaredFields.length + supDeclaredFields.length];
        System.arraycopy(declaredFields, 0, allDeclaredFields, 0, declaredFields.length);
        System.arraycopy(supDeclaredFields, 0, allDeclaredFields, declaredFields.length, supDeclaredFields.length);
        for (Field field : allDeclaredFields) {
            System.out.print(field.getName() + "=" + field.get(obj));
            System.out.print(",类型：" + field.getType());
        }
        // 泛型声明中声明的类型
        TypeVariable<Class<T>>[] typeParameters = clazz.getTypeParameters();
        Package aPackage = clazz.getPackage();

        // 获取该类中的方法
        Method[] methods = clazz.getMethods();
        // 获取当前类的所有声明的方法，包括public、protected和private修饰的方法
        Method[] declaredMethods = clazz.getDeclaredMethods();

        Map<String, Object> map = new HashMap<>(methods.length);
        for (Method method : methods) {
            // 值true表示反射对象在使用时应禁止Java语言访问检查。值false表示反射的对象应该执行Java语言访问检查
            method.setAccessible(true);

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
