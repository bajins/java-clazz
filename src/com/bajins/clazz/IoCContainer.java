package com.bajins.clazz;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 控制对象的创建和销毁（生命周期）
 * 将对象的控制权反转交给IoC容器
 * 所有的Bean的生命周期交由IoC容器管理
 */
public class IoCContainer {
    // 保存bean容器
    private final Map<String, Object> beans = new ConcurrentHashMap<>();

    /**
     * 获取bean
     *
     * @param beanId
     * @return
     */
    public Object getBean(String beanId) {
        return beans.get(beanId);
    }


    /**
     * 委托IOC容器实例化Bean
     *
     * @param clazz        要实例化Bean的class
     * @param beanId
     * @param paramBeanIds 实例化Bean的构造方法所需要的参数的BeanId（通过构造方法注入的依赖）
     */
    public void setBean(Class<?> clazz, String beanId, String... paramBeanIds) {
        // 组装实例化Bean中构造方法的参数
        Object[] paramValues = new Object[paramBeanIds.length];
        for (int i = 0; i < paramBeanIds.length; i++) {
            paramValues[i] = beans.get(paramBeanIds[i]);
        }
        Object bean = null;
        // 通过反射获取到class中所有的构造方法
        for (Constructor<?> constructor : clazz.getConstructors()) {
            try {
                // 所有被依赖的Bean通过构造方法注入，被依赖的Bean需要优先创建
                // 所以这里调用构造方法实例化Bean
                constructor.newInstance(paramValues);
            }
            // 用管道符捕获多个异常，在这种情况下异常参数变量e是final的
            catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (bean == null) {
            throw new RuntimeException("找不到合适的构造方法实例化Bean");
        }
        // 将实例化的bean保存
        beans.put(beanId, bean);
    }
}
