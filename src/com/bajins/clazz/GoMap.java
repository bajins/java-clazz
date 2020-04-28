package com.bajins.clazz;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Map的原生api使用示例
 */
public class GoMap {
    public static void main(String[] args) {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(1, "1111");
        map.put(2, "2222");

        // entrySet迭代，不能移除元素
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println("方法一：key =" + entry.getKey() + "---value=" + entry.getValue());
        }

        // 遍历键
        for (Integer key : map.keySet()) {
            System.out.println("方法二：key = " + key);
        }
        Iterator<Integer> keyIterator = map.keySet().iterator();
        while (keyIterator.hasNext()) {
            Integer key = keyIterator.next();
            System.out.println("方法二：key = " + key);
        }
        // 遍历值
        for (String value : map.values()) {
            System.out.println("方法二：value = " + value);
        }

        // Iterator迭代，可调用entries.remove()移除元素：使用了泛型，如果不使用泛型键值则需要强转
        Iterator<Map.Entry<Integer, String>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, String> entry = entries.next();
            System.out.println("方法三：key = " + entry.getKey() + "--value=" + entry.getValue());
        }

        // 遍历键获取值：效率低，通过key得到value值更耗时
        for (Integer key : map.keySet()) {
            String value = map.get(key);
            System.out.println("方法四：Key = " + key + ", Value = " + value);
        }
        Iterator<Integer> keyIterator1 = map.keySet().iterator();
        while (keyIterator1.hasNext()) {
            Integer key = keyIterator1.next();
            String value = map.get(key);
            System.out.println("方法四：Key = " + key + ", Value = " + value);
        }
    }
}
