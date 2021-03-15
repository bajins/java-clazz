package com.bajins.clazz;

import java.io.*;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 获取配置文件信息
 *
 * @author claer https://www.bajins.com
 * @program com.bajins.common.utils
 * @description PropertiesUtil
 * @create 2019-03-16 22:13
 */
public class PropertiesUtil {

    /**
     * 基于ClassLoder读取配置文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Properties getPropertiesL(String filePath) throws IOException {
        Properties properties = new Properties();
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceAsStream = contextClassLoader.getResourceAsStream(filePath)) {
            // 基于ClassLoder读取配置文件,该方式只能读取类路径下的配置文件，有局限但是如果配置文件在类路径下比较方便。
            properties.load(resourceAsStream);
            return properties;
        }
    }

    /**
     * 基于ClassLoder读取配置文件获取Properties
     *
     * @param filePath
     * @return
     */
    public static Properties getPropertiesC(String filePath) throws IOException {
        Properties prop = new Properties();
        InputStream inputStream = PropertiesUtil.class.getResourceAsStream(filePath);
        prop.load(inputStream);
        return prop;
    }

    /**
     * 根据文件名使用BufferedReader获取Properties
     *
     * @param filePath
     * @return
     */
    public static Properties getPropertiesB(String filePath) throws IOException {
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        // 该方式的优点在于可以读取任意路径下的配置文件
        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            properties.load(bufferedReader);
            return properties;
        }
    }

    /**
     * 使用InputStream缓冲输入流读取配置文件获取Properties
     *
     * @param filePath
     * @return
     */
    public static Properties getPropertiesI(String filePath) throws IOException {
        Properties prop = new Properties();
        File file = new File(filePath);
        // 通过输入缓冲流进行读取配置文件
        try (FileInputStream fileInputStream = new FileInputStream(file);
             InputStream InputStream = new BufferedInputStream(fileInputStream)) {
            // 加载输入流
            prop.load(InputStream);
            return prop;
        }
    }

    /**
     * 根据文件名使用ResourceBundle获取Properties
     *
     * @param filePath
     * @return
     */
    public static ResourceBundle getPropertiesR(String filePath) {
        // 通过 java.util.ResourceBundle 类来读取，这种方式比使用 Properties 要方便一些
        // config为属性文件名，放在包com.test.config下，如果是放在src下，直接用config即可
        return ResourceBundle.getBundle(filePath);
    }


    public static void main(String[] args) {
        try {
            Properties propertiesI = getPropertiesI("com/test/config/config.properties");
            System.out.println(propertiesI.getProperty("key"));

            ResourceBundle propertiesR = getPropertiesR("com/test/config/config.properties");
            System.out.println(propertiesR.getString("key"));

            Properties propertiesB = getPropertiesB("com/test/config/config.properties");
            System.out.println(propertiesB.getProperty("key"));

            Properties propertiesC = getPropertiesC("com/test/config/config.properties");
            System.out.println(propertiesC.getProperty("key"));

            Properties propertiesL = getPropertiesL("com/test/config/config.properties");
            System.out.println(propertiesL.getProperty("key"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
