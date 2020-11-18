package com.bajins.clazz.annotation;

import java.lang.annotation.*;


/**
 * 自定义注解
 */
// 作用域
@Target({ElementType.TYPE // 类或接口（包括注释类型）或枚举声明
        , ElementType.FIELD // 字段声明（包括枚举常量）
        , ElementType.PARAMETER // 参数
        , ElementType.METHOD // 方法
        , ElementType.CONSTRUCTOR // 构造函数
        , ElementType.LOCAL_VARIABLE // 局部变量
        , ElementType.ANNOTATION_TYPE // 注释类型
        , ElementType.PACKAGE // 包
        , ElementType.TYPE_PARAMETER // 类型参数
        , ElementType.TYPE_USE // 使用类型
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableLog {

    /**
     * 描述
     *
     * @return
     */
    String description() default "描述";

    String desc();

    /**
     * 状态类型
     */
    int state() default -1;

    /**
     * 是否有效
     *
     * @return
     */
    boolean isValid() default true;
}
