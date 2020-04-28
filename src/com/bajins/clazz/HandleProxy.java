package com.bajins.clazz;

public class HandleProxy<T> {
    private Class<T> clazz;

    public HandleProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    //public T getInstance(){
    //    Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{clazz},new ProxyIn)
    //
    //}
}
