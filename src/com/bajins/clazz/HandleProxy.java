package com.bajins.clazz;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class HandleProxy<T> {
    private Class<T> clazz;

    public HandleProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T getInstance(InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, handler);
    }
}
