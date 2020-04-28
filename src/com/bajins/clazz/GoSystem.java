package com.bajins.clazz;

/**
 * System原生API的使用示例
 */
public class GoSystem {
    /**
     * 拼接字符串并打印
     *
     * @param str
     * @return
     */
    public static void println(String... str) {
        StringBuffer sb = new StringBuffer();
        for (String s : str) {
            sb.append(s).append(" ");
        }
        System.out.println(sb.toString());
    }


    public static void main(String[] args) {

    }
}
