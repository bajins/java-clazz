package com.bajins.clazz;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 对象工具类
 *
 * @author claer https://www.bajins.com
 * @program com.bajins.api.utils
 * @create 2018-06-10 22:48
 * <br/>
 * @see java.beans JavaBean是一种特殊的Java类,主要用于传递数据信息,这种Java类中的方法主要用于访问私有字段,且方法名符合某种命名规则，
 * JavaBean的属性是根据其中的setter和getter方法名推断出来的，它根本看不到java类内部的成员变量
 * @see Introspector 内省：对JavaBean类属性、事件的一种缺省处理方法，先得到属性描述器PropertyDecriptor后再进行各种操作
 * @see PropertyDescriptor 属性描述器：通过存储器导出一个JavaBean类的属性
 * <br/>
 * @see java.lang.reflect （reflection）反射：一个类的所有成员都可以进行反射操作，先得到类的字节码Class后再进行各种操作
 * @see Field 类的属性字段
 * @see Method 类的方法
 * @see Modifier
 * @see Parameter
 * @see Type
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
        System.out.println(obj instanceof CharSequence);
        System.out.println(CharSequence.class.isAssignableFrom(obj.getClass()));
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

    /**
     * 把List<Map>的Map中value取出装进LIST中
     *
     * @param maps List<Map>集合
     * @param key  Map中的key
     * @return java.util.List
     */
    public static List getKeyList(List<Map> maps, String key) {
        List list = new ArrayList<>();
        Iterator<Map> iterator = maps.iterator();
        while (iterator.hasNext()) {
            Map next = iterator.next();
            list.add(next.get(key));
        }
        return list;
    }

    /**
     * 泛型类，是在实例化类的时候指明泛型的具体类型；
     * 泛型方法，是在调用方法的时候指明泛型的具体类型 。
     * 说明：
     * 1）public 与 返回值中间<T>非常重要，可以理解为声明此方法为泛型方法。
     * 2）只有声明了<T>的方法才是泛型方法，泛型类中的使用了泛型的成员方法并不是泛型方法。
     * 3）<T> T的<T>表明该方法将使用泛型类型T，此时才可以在方法中使用泛型类型T。
     * 4）<T> T的T表明该方法返回的类型。
     * 5）与泛型类的定义一样，此处T可以随便写为任意标识，常见的如T、E、K、V等形式的参数常用于表示泛型。
     *
     * <p>
     * List 集合中随机获取一条数据
     *
     * @param list 传入的泛型实参
     * @param <T>  泛型类型T
     * @return T 返回值为T类型
     */
    public static <T> T getRandomList(List<T> list) {
        Random random = new Random();
        int n = random.nextInt(list.size());
        return list.get(n);
    }

    /**
     * List 集合中随机获取指定条数数据
     *
     * @param list   传入的泛型实参
     * @param length 获取多少条
     * @param <T>    泛型类型T
     * @return T 返回值为T类型
     */
    public static <T> List<T> getRandomListLimit(List<T> list, int length) {
        Random index = new Random();
        //存储已经被调训出来的List 中的 index
        List<Integer> indexList = new ArrayList<>();
        List<T> newList = new ArrayList<T>();
        for (int i = 0, j; i < length; i++) {
            //获取在 list.size 返回内的随机数
            j = index.nextInt(list.size());
            //判断是否重复
            if (!indexList.contains(j)) {
                //获取元素
                indexList.add(j);
                newList.add(list.get(j));
            } else {
                i--;//如果重复再来一次
            }
        }
        return newList;
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
                // 获取属性的get方法
                Method readMethod = pd.getReadMethod();
                // 调用get方法获得属性值
                Object lhsValue = readMethod.invoke(lhs);
                Object rhsValue = readMethod.invoke(rhs);
                // 比较值
                if (lhsValue instanceof List || lhsValue instanceof Map) {
                    continue;
                }
                if (lhsValue instanceof String && !lhsValue.equals(rhsValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(isEmpty(""));
    }
}