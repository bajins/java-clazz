package com.bajins.clazz;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 解析Properties配置
 *
 * @version V1.0
 * @Title: Configuration.java
 * @Package com.bajins.clazz
 * @Description:
 * @author: https://www.bajins.com
 * @date: 2021年8月27日 下午12:33:55
 * @Copyright: 2021 bajins.com Inc. All rights reserved.
 */
public class Configuration extends Properties {

    private static final long serialVersionUID = -6303583923552229135L;

    /**
     * 使用默认的ClassLoader加载properties文件
     *
     * @param propPath
     * @throws IOException
     */
    public Configuration(String propPath) throws IOException {
        try (InputStream resourceAsStream = Configuration.class.getClassLoader().getResourceAsStream(propPath)) {
            // 基于ClassLoder读取配置文件,该方式只能读取类路径下的配置文件，有局限，但是如果配置文件在类路径下比较方便。
            super.load(resourceAsStream);
        }
    }

    /**
     * 使用指定的ClassLoader加载properties文件
     *
     * @param classLoader
     * @param propFile
     * @throws IOException
     */
    public Configuration(ClassLoader classLoader, String propFile) throws IOException {
        this(classLoader.getResource(propFile));
    }

    /**
     * 从{@link File}构造对象
     *
     * @param propFile
     * @throws IOException
     */
    public Configuration(File propFile) throws IOException {
        this(propFile.toURI().toURL());
    }

    public Configuration() {
        super();
    }

    public Configuration(Properties defaults) {
        super(defaults);
    }

    /**
     * 根据{@link URL}加载配置文件
     *
     * @param url
     * @throws IOException
     */
    public Configuration(URL url) throws IOException {
        if (null == url) {
            throw new NullPointerException("parameter [url] is null");
        }
        try (InputStream is = url.openStream();) {
            super.load(is);
        }
    }

    /**
     * 首字母大写，进行字母的ascii编码前移
     *
     * @param str 需要转换的字符串
     * @return
     */
    public static String captureStrToUpper(String str) {
        // 效率低下
        /*str = str.substring(0, 1).toUpperCase() + str.substring(1);
        return str;*/
        char[] chars = str.toCharArray();
        // chars[0] -= (chars[0] > 96 && chars[0] < 123) ? 32 : 0;
        if (chars[0] >= 97 && chars[0] <= 122) { // 检查ascii是大写
            chars[0] -= 32;
        }
        return String.valueOf(chars);
    }

    /**
     * 首字母小写，进行字母的ascii编码前移
     *
     * @param str 需要转换的字符串
     * @return
     */
    public static String captureStrToLower(String str) {
        // 效率低下
        /*str = str.substring(0, 1).toLowerCase(Locale.ROOT) + str.substring(1);
        return str;*/
        int point = str.codePointAt(0);
        if (point < 65 || point > 90) { // 判断首字符不是大写
            return str;
        }
        char[] chars = str.toCharArray();
        if (chars[0] >= 65 && chars[0] <= 90) { // 检查ascii是大写
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

    /**
     * 利用反射把配置中的属性值绑定到实列bean上
     *
     * @param <T>
     * @param clazz
     * @param prefix 前缀
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <T> T bindBean(Class<T> clazz, String prefix) throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {
        if (prefix == null) {
            prefix = "";
        }
        T instance = clazz.getDeclaredConstructor().newInstance(); // 创建实例化对象
        Field[] declaredFields = clazz.getDeclaredFields(); // 获取所有字段
        for (Field field : declaredFields) {
            String property = this.getProperty(prefix + field.getName());
            if (property == null || property.trim().equals("")) {
                continue;
            }
            Class<?> type = field.getType();

            field.setAccessible(true); // 设置属性可访问
            if (type.isAssignableFrom(Boolean.class)) {
                field.setBoolean(instance, Boolean.valueOf(property));
            } else if (type.isAssignableFrom(Byte.class)) {
                field.setByte(instance, Byte.valueOf(property));
            } else if (type.isAssignableFrom(Character.class)) {
                // field.setChar(instance, Character.valueOf(property));
            } else if (type.isAssignableFrom(Double.class)) {
                field.setDouble(instance, Double.valueOf(property));
            } else if (type.isAssignableFrom(Float.class)) {
                field.setFloat(instance, Float.valueOf(property));
            } else if (type.isAssignableFrom(Integer.class)) {
                field.setInt(instance, Integer.valueOf(property));
            } else if (type.isAssignableFrom(Long.class)) {
                field.setLong(instance, Long.valueOf(property));
            } else if (type.isAssignableFrom(Short.class)) {
                field.setShort(instance, Short.valueOf(property));
            } else {
                field.set(instance, property);
            }
            field.setAccessible(false); // 设置属性不可访问

            /*try { // 通过setter方法设置值
                Method method = clazz.getMethod("set" + captureStrToUpper(field.getName()), type);
                if (type.isAssignableFrom(Boolean.class)) {
                    method.invoke(instance, Boolean.valueOf(property));
                } else if (type.isAssignableFrom(Byte.class)) {
                    method.invoke(instance, Byte.valueOf(property));
                } else if (type.isAssignableFrom(Character.class)) {
                    // method.invoke(instance, Character.valueOf(property));
                } else if (type.isAssignableFrom(Double.class)) {
                    method.invoke(instance, Double.valueOf(property));
                } else if (type.isAssignableFrom(Float.class)) {
                    method.invoke(instance, Float.valueOf(property));
                } else if (type.isAssignableFrom(Integer.class)) {
                    method.invoke(instance, Integer.valueOf(property));
                } else if (type.isAssignableFrom(Long.class)) {
                    method.invoke(instance, Long.valueOf(property));
                } else if (type.isAssignableFrom(Short.class)) {
                    method.invoke(instance, Short.valueOf(property));
                } else {
                    method.invoke(instance, property);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalArgumentException
                    | InvocationTargetException e) {
                e.printStackTrace();
                continue;
            }*/
        }
        return instance;
    }

    public <T> List<T> bindBeanList(Class<T> clazz, String prefix)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Pattern pattern = Pattern.compile("(" + prefix + "\\[.*\\]).*");
        Map<String, List<String>> groupMap = this.stringPropertyNames().stream().filter(s -> { // 过滤数据
            Matcher matcher = pattern.matcher((CharSequence) s);
            return matcher.find();
        }).collect(Collectors.groupingBy(s -> { // 分组数据
            Matcher matcher = pattern.matcher((CharSequence) s);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return s;
        }));
        List<T> res = new ArrayList<>();
        for (String key : groupMap.keySet()) {
            res.add(bindBean(clazz, key + "."));
        }
        return res;
    }

    public static void main(String[] args) {
        try {
            Configuration configuration = new Configuration("test.properties");
            Test bindBean = configuration.bindBean(Test.class, "test.");
            System.out.println(bindBean);

            List<Test> bindBeanList = configuration.bindBeanList(Test.class, "test");
            System.out.println(bindBeanList);
        } catch (InstantiationException | IllegalAccessException | IOException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static class Test {
        private String id;
        private String username;
        private String userCode;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        @Override
        public String toString() {
            return "Test{" +
                    "id='" + id + '\'' +
                    ", username='" + username + '\'' +
                    ", userCode='" + userCode + '\'' +
                    '}';
        }
    }
}
