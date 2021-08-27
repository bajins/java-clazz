package com.bajins.clazz;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * 序列化和反序列化
 */
public class SerializableLearning {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        /**
         * 在程序目录下创建一个temp.properties文件
         */
        File dirPath = new File(".");
        //创建一个储存文件temp.properties文件的绝对路径
        String filePath = Paths.get(dirPath.getCanonicalPath(), "src", "temp.properties").toString();

        // 创建一个序列化对象
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream)) {
            //这里需要序列化对象，所以使用writeObject方法
            oos.writeObject(new Person("张三", 1));//写入第一个对象
            oos.writeObject(new Person("李四", 2));//写入第二个对象
            oos.writeObject(new Person("王五", 3));//写入第三个对象
            //关闭资源
            oos.close();
        }
        // 创建一个反序列化对象
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fileInputStream)) {
            Person p1 = (Person) ois.readObject();//读取第一个对象
            Person p2 = (Person) ois.readObject();//读取第二个对象
            Person p3 = (Person) ois.readObject();//读取第三个对象
            //关闭资源
            //ois.close();

            System.out.println(p1);
            System.out.println(p2);
            System.out.println(p3);
        }


        // 创建一个序列化对象
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream)) {
            // 要存储的数据集合
            List<Person> writeList = new ArrayList<>();
            writeList.add(new Person("张三", 1));
            writeList.add(new Person("李四", 2));
            writeList.add(new Person("王五", 3));
            //这里需要序列化对象，所以使用writeObject方法
            oos.writeObject(writeList);
            //关闭资源
            oos.close();
        }
        // 创建一个反序列化对象
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fileInputStream)) {
            List<Person> readList = (List<Person>) ois.readObject();

            //遍历集合
            for (Person person : readList) {
                System.out.println(person);
            }
        }
    }

    public static class Person implements Serializable {

        private String name;
        private int id;

        public Person() {
        }

        public Person(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", id=" + id +
                    '}';
        }
    }
}
