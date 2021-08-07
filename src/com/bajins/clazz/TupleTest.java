package com.bajins.clazz;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 使用代理反射机制测试元组实现
 */
public class TupleTest {

    public static void main(String[] args) {
        Object mixin = MixinProxy.newInstance(new Tuple.TwoTuple(new BasicImpl(), Basic.class),
                new Tuple.TwoTuple(new TimeStampedImp(), TimeStamped.class),
                new Tuple.TwoTuple(new SerialNumberedImpl(), SerialNumbered.class));
        Basic b = (Basic) mixin;
        TimeStamped t = (TimeStamped) mixin;
        SerialNumbered s = (SerialNumbered) mixin;
        b.set("hello");
        System.out.println(b.get());
        System.out.println(t.getStamp());
        System.out.println(s.getSerialNumber());
    }

}

class MixinProxy implements InvocationHandler {

    Map<String, Object> delegatesByMethod;

    public MixinProxy(Tuple.TwoTuple<Object, Class<?>>... pairs) {
        delegatesByMethod = new HashMap<String, Object>();
        for (Tuple.TwoTuple<Object, Class<?>> pair : pairs) {
            for (Method method : pair.second.getMethods()) {
                String methodName = method.getName();
                if (!delegatesByMethod.containsKey(methodName)) {
                    delegatesByMethod.put(methodName, pair.first);
                }
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Object delegate = delegatesByMethod.get(methodName);
        return method.invoke(delegate, args);
    }

    public static Object newInstance(Tuple.TwoTuple... pairs) {
        Class[] interfaces = new Class[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            interfaces[i] = (Class) pairs[i].second;
        }
        ClassLoader cl = pairs[0].first.getClass().getClassLoader();
        return Proxy.newProxyInstance(cl, interfaces, new MixinProxy(pairs));
    }

}


interface TimeStamped {
    long getStamp();
}

class TimeStampedImp implements TimeStamped {

    private final long timeStamp;

    public TimeStampedImp() {
        timeStamp = new Date().getTime();
    }

    @Override
    public long getStamp() {
        return timeStamp;
    }

}

interface SerialNumbered {
    long getSerialNumber();
}

class SerialNumberedImpl implements SerialNumbered {

    private static long counter = 1;

    private final long serialNumber = counter++;

    public long getSerialNumber() {
        return serialNumber;
    }

}

interface Basic {
    public void set(String val);

    public String get();
}

class BasicImpl implements Basic {
    private String value;

    public void set(String val) {
        value = val;
    }

    @Override
    public String get() {
        return value;
    }
}
