package com.bajins.clazz;


import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 一些在工作中常用的编程思路（Thinking）
 */
public class CurdIdeas {

    /**
     * 比较一个表单列表在数据库中的数据做增查改删
     */
    public void crud() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        // 伪代码：表单数据
        List<DelayedManager.DelayedMessage> formList = new ArrayList<>();
        formList.add(new DelayedManager.DelayedMessage(1, "1", 21));
        formList.add(new DelayedManager.DelayedMessage(2, "2", 2)); // 增
        formList.add(new DelayedManager.DelayedMessage(4, "4", 57)); // 改
        formList.add(new DelayedManager.DelayedMessage(6, "6", 67));
        // 伪代码：查询的数据库数据
        List<DelayedManager.DelayedMessage> dbList = new ArrayList<>();
        dbList.add(new DelayedManager.DelayedMessage(1, "1", 21));
        dbList.add(new DelayedManager.DelayedMessage(3, "3", 35)); // 删
        dbList.add(new DelayedManager.DelayedMessage(4, "4", 53));
        dbList.add(new DelayedManager.DelayedMessage(6, "6", 67));

        // 把数据库数据list转map，方便接下来的代码获取并比较
        Map<String, DelayedManager.DelayedMessage> dbMap =
                dbList.stream().collect(Collectors.toMap(DelayedManager.DelayedMessage::getBody, Function.identity()));

        /*// 可能数据库表需要存每条数据的行号，为避免重复需要过滤出可用数字
        // 获取数据库中查出的所有已经存在的行号
        Set<Integer> seqSet = dbList.stream().map(DelayedMessage::getId).collect(Collectors.toSet());
        // 创建一个指定大小的顺序数字列表
        List<Integer> numberList =
                IntStream.range(1, formList.size() + seqSet.size()).boxed().collect(Collectors.toList());
        // 过滤已有的数字后可用数字
        List<Integer> availableNumbers =
                seqSet.stream().filter(item -> !numberList.contains(item)).collect(Collectors.toList());*/

        List<DelayedManager.DelayedMessage> addList = new ArrayList<>(); // 插入的数据
        List<DelayedManager.DelayedMessage> updateList = new ArrayList<>(); // 更新的数据
        List<DelayedManager.DelayedMessage> deleteList = null; // 删除的数据

        for (DelayedManager.DelayedMessage form : formList) { // 循环遍历表单数据
            String name = form.getBody();

            DelayedManager.DelayedMessage dbDto = dbMap.get(name); // 获取数据库数据
            if (dbDto == null) { // 如果不存在，则需要插入到数据库
                dbDto = new DelayedManager.DelayedMessage(1, "1", 1);
                addList.add(dbDto); // 新增的数据
                continue;
            }
            dbMap.remove(name); // 移除数据库中要保留的数据, 最后存在的就是需要删除的数据
            // 判断数据在数据库中是否相同, 如果相同说明数据没有变化
            if (BeanUtil.equalsFields(form, dbDto, "id", "body", "executeTime")) {
                continue;
            }
            updateList.add(form); // 有变化的数据
        }
        if (dbMap != null && !dbMap.isEmpty()) {
            deleteList = new ArrayList<>(dbMap.values()); // 最后需要删除数据库的数据
            // TODO: 请求数据库删除数据
        }
        if (addList != null && !addList.isEmpty()) {
            // TODO: 请求数据库插入数据
        }
        if (updateList != null && !updateList.isEmpty()) {
            // TODO: 请求数据库更新数据
        }
    }

    public static void main(String[] args) {
        
    }
}
