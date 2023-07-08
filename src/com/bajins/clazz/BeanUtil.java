package com.bajins.clazz;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Java语言欠缺属性、事件、多重继承功能。
 * 如果要在Java程序中实现一些面向对象编程的常见需求，只能手写大量胶水代码/粘合代码（glue code），用途是粘合那些可能不兼容的代码。
 * <p>
 * https://www.oracle.com/java/technologies/javase/javabeans-spec.html
 * Bean的含义是可重复使用的Java组件，所谓组件就是一个由可以自行进行内部管理的一个或几个类所组成、外界不了解其内部信息和运行方式的群体
 * <p>
 * Java Bean正是编写这套胶水代码的惯用模式或约定，必须达到如下条件：
 * <p>
 * 1、所有属性为private
 * 2、提供默认无参构造函数
 * 3、提供getter和setter
 * 4、可序列化：实现serializable接口
 * <p>
 * https://blog.csdn.net/goodbye_youth/category_9124345.html
 *
 * @see java.beans JavaBean是一种特殊的Java类,主要用于传递数据信息,这种Java类中的方法主要用于访问私有字段,且方法名符合某种命名规则，
 * JavaBean的属性是根据其中的setter和getter方法名推断出来的，它根本看不到java类内部的成员变量
 * @see Introspector 内省：对JavaBean类属性、事件的一种缺省处理方法，先得到属性描述器PropertyDecriptor后再进行各种操作
 * @see PropertyDescriptor 属性描述器：通过存储器导出一个JavaBean类的属性
 * https://www.zhihu.com/question/19773379/answers/updated
 * @see java.lang.instrument
 * @see java.lang.invoke 反射调用相关（含Lambda调用实现）
 * @see java.lang.reflect （reflection）反射：一个类的所有成员都可以进行反射操作，先得到类的字节码Class后再进行各种操作
 * @see Executable
 * @see Field 类的属性字段
 * @see Method 类的方法
 * @see Modifier
 * @see Parameter
 * @see Type
 * @see java.lang.ref
 * @see Reference
 * @see WeakReference
 * @see Entry
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

    /**
     * 比较两个对象指定的属性值是否相等
     *
     * @param lhs    第一个对象
     * @param rhs    第二个对象
     * @param fields 需要比较的属性字段
     * @return 相同返回true，不同则返回false
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static boolean equalsFields(Object lhs, Object rhs, String... fields) throws IntrospectionException,
            InvocationTargetException, IllegalAccessException {
        Class<?> lhsClazz = lhs.getClass();
        Class<?> rhsClazz = rhs.getClass();
        if (lhsClazz != rhsClazz) {
            return false;
        }
        // 数组转Map
        Map<String, String> fieldMap = Arrays.stream(fields).collect(Collectors.toMap(e -> e, Function.identity()));
        // 获取JavaBean的所有属性
        PropertyDescriptor[] pds = Introspector.getBeanInfo(lhsClazz, Object.class).getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            // 遍历获取属性名
            String name = pd.getName();
            if (name.equals(fieldMap.get(name))) {
                // 获取属性类型
                Class<?> propertyType = pd.getPropertyType();
                // 获取属性的get方法
                Method readMethod = pd.getReadMethod();
                // 调用get方法获得属性值
                Object lhsValue = readMethod.invoke(lhs);
                Object rhsValue = readMethod.invoke(rhs);
                // 比较值
                if (lhsValue instanceof List || lhsValue instanceof Map) {
                    continue;
                }
                if (lhsValue instanceof CharSequence && !lhsValue.equals(rhsValue)) {
                    return false;
                }
                if (propertyType.getName().equals(Integer.class.getName())) {

                }
            }
        }
        return true;
    }

    /**
     * 获取一个接口的所有实现类
     * <p>
     * Spring https://blog.csdn.net/RickyIT/article/details/78198859#%E5%AE%9E%E7%8E%B0
     * <p>
     * 只能获取项目中自己定义的接口,不能获取到JDK或者是其他Jar包中的接口,因为这个工具的原理就是扫描编译后的classes目录
     *
     * @param target
     * @return
     */
    public static ArrayList<Class<?>> getInterfaceImpls(Class<?> target) throws ClassNotFoundException {
        ArrayList<Class<?>> subclassaes = new ArrayList<>();
        // 判断class对象是否是一个接口
        if (!target.isInterface()) {
            throw new IllegalArgumentException("Class对象不是一个interface");
        }
        String basePackage = Objects.requireNonNull(target.getClassLoader().getResource("")).getPath();
        File[] files = new File(basePackage).listFiles();
        // 存放class路径的list
        ArrayList<String> classpaths = new ArrayList<>();
        for (File file : files) {
            // 扫描项目编译后的所有类
            if (file.isDirectory()) {
                listPackages(file.getName(), classpaths);
            }
        }
        // 获取所有类,然后判断是否是 target 接口的实现类
        for (String classpath : classpaths) {
            Class<?> classObject = Class.forName(classpath);
            if (classObject.getSuperclass() == null) {
                continue; // 判断该对象的父类是否为null
            }
            Set<Class<?>> interfaces = new HashSet<>(Arrays.asList(classObject.getInterfaces()));
            if (interfaces.contains(target)) {
                subclassaes.add(Class.forName(classObject.getName()));
            }
        }
        return subclassaes;
    }

    /**
     * 获取项目编译后的所有的.class的字节码文件
     * 这么做的目的是为了让 Class.forName() 可以加载类
     *
     * @param basePackage 默认包名
     * @param classes     存放字节码文件路径的集合
     * @return
     */
    public static void listPackages(String basePackage, List<String> classes) {
        URL url = BeanUtil.class.getClassLoader().getResource("./" + basePackage.replaceAll("\\.", "/"));
        File directory = new File(url.getFile());
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            // 如果是一个目录就继续往下读取(递归调用)
            if (file.isDirectory()) {
                listPackages(basePackage + "." + file.getName(), classes);
            } else {
                // 如果不是一个目录,判断是不是以.class结尾的文件,如果不是则不作处理
                String classpath = file.getName();
                if (".class".equals(classpath.substring(classpath.length() - ".class".length()))) {
                    classes.add(basePackage + "." + classpath.replaceAll(".class", ""));
                }
            }
        }
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
        Object obj = beanClass.getDeclaredConstructor().newInstance();

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
        if (Objects.isNull(obj)) {
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
    public static Object mapToBeanByReflect(Map<String, Object> map, Class<?> beanClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        if (Objects.isNull(map)) {
            throw new IllegalArgumentException("传入的对象为空");
        }
        Object obj = beanClass.getDeclaredConstructor().newInstance();

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
        if (Objects.isNull(obj)) {
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
     * Lambda 函数调用实现
     * <a href="https://mp.weixin.qq.com/s/VxszHufvXJn_Mp32Mq_Cdw">Java 的 Lambda 是怎么实现的？（一） Java 的函数解析</a>
     * <a href="https://mp.weixin.qq.com/s/ywAJOk1F4Rmu_rzRpzlgcA">Java 的 Lambda 是怎么实现的？（二）invokedynamic 的调用过程</a>
     * <a href="https://mp.weixin.qq.com/s/h9fyeHV0PsVgo_-8wZEUcg">Java 的 Lambda 是怎么实现的？（三）实现 Lambda</a>
     * <pre>
     *     try {
     *         MethodType mt = MethodType.methodType(String.class, String.class);
     *         MethodHandle handle = MethodHandles.publicLookup().findVirtual(String.class, "concat", mt);
     *         String result = (String) handle.invoke("Method", "Handle");
     *         System.out.println(result);
     *     } catch (NoSuchMethodException e) {
     *         e.printStackTrace();
     *     } catch (IllegalAccessException e) {
     *         e.printStackTrace();
     *     } catch (Throwable e) {
     *         e.printStackTrace();
     *     }
     * </pre>
     *
     * @param refc   实例对象的类
     * @param name   实例对象的类中方法
     * @param rtype  实例对象的类中方法的返回类型
     * @param ptypes 实例对象的类中方法的形参类型，用来确定具体调用的方法（考虑重载情况）
     * @param args   调用方法的传入参数实例
     * @return
     * @throws Throwable
     */
    public Object invokeMethod(Class<?> refc, String name, Class<?> rtype, Class<?>[] ptypes, Object... args) throws Throwable {
        MethodType mt = MethodType.methodType(rtype, ptypes);
        MethodHandle handle = MethodHandles.publicLookup().findVirtual(refc, name, mt);
        return handle.invoke(args);
    }

    /**
     * 获取 类.方法
     *
     * @param traceElement
     * @return
     */
    public static String getClassMethod(StackTraceElement traceElement) {
        return traceElement.getClassName() + "." + traceElement.getMethodName();
    }

    /**
     * 获取 类.方法
     *
     * @param thread Thread.currentThread()
     * @return java.lang.String
     */
    public static String getClassMethod(Thread thread) {
        return getClassMethod(thread.getStackTrace()[1]);
    }

    /**
     * 获取 类.方法
     *
     * @param throwable
     * @return java.lang.String
     */
    public static String getClassMethod(Throwable throwable) {
        return getClassMethod(throwable.getStackTrace()[1]);
    }


    /**
     * Java Class类的clone()方法默认为浅拷贝模式，只能实现Java基础类型的按值拷贝操作，对对象拷贝时默认为按址拷贝。
     * 这里采用对一个对象进行序列化和反序列化的方式来实现对象的深拷贝
     * <p>
     * 或者使用JSON
     *
     * @param src
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> T deepClone(T src) throws IOException, ClassNotFoundException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
             ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);) {
            objectOutputStream.writeObject(src);
            return (T) objectInputStream.readObject();
        }
    }

    public static void main(String[] args) {

    }
}
