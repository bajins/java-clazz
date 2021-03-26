package com.bajins.clazz;


import java.util.*;
import java.util.stream.*;

/**
 * @see Set
 * @see List
 * @see Map
 * @see SortedSet
 * @see SortedMap
 * @see HashSet
 * @see TreeSet
 * @see ArrayList
 * @see LinkedList
 * @see AbstractList
 * @see AbstractSequentialList
 * @see Vector
 * @see AbstractCollection
 * @see Collection 集合框架的父接口
 * @see Collections 各种有关集合操作的 静态多态方法 工具类
 * @see Collections#nCopies(int, Object)
 * @see Collections#EMPTY_LIST
 * @see Arrays
 * @see Arrays#asList(Object[])
 * @see IntStream
 * @see LongStream
 * @see DoubleStream
 * @see Stream#collect(Collector)
 * @see Collector 作为Stream的collect方法的参数
 * @see Collectors 实现Collector的工具类
 */
public class CollectionsLearning {


    public static void main(String[] args) {
        // 数组转List
        List<String> list = Arrays.asList("a", "b", "c", "d");
        /*list = Stream.of("a", "b", "c", "d").collect(Collectors.toList());
        list = Stream.of("a", "b", "c", "d").collect(Collectors.toCollection(ArrayList::new));
        list = new ArrayList<String>() {{// 使用匿名内部类初始化时构造数据
            addAll(Arrays.asList("a", "b", "c", "d"));
        }};*/
        // 取对象中的字段放到新的List中
        //List<String> userIds = list.stream().map(a -> a.getUserid()).collect(Collectors.toList());


        /**
         * List转Map
         * Function接口中的默认方法 Function.identity()，这个方法返回自身对象
         */
        // key一样的情况下，如果不指定一个覆盖规则，会报错
        /*Map<Long, User> maps = userList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        maps = userList.stream().collect(Collectors.toMap(User::getId, t -> t));
        maps = userList.stream().collect(Collectors.toMap(User::getId, Function.identity(), (key1, key2) -> key2));
        // 希望得到的map的值不是对象，而是对象的某个属性
        Map<Long, String> maps = userList.stream().collect(Collectors.toMap(User::getId, User::getAge,
                (key1, key2) -> key2));

        accounts.stream().collect(Collectors.toMap(Account::getId, Account::getUsername));
        // 设置map的value值是实体本身，同Function.identity()
        accounts.stream().collect(Collectors.toMap(Account::getId, account -> account));*/


        /**
         * Map转List
         */
        /*List<String> result = new ArrayList(map.keySet());
        List<String> result2 = new ArrayList(map.values());
        List<String> result3 = map.keySet().stream().collect(Collectors.toList());
        List<String> result4 = map.values().stream().collect(Collectors.toList());*/


        System.out.println("------------- 随机取值 -------------");
        // 方法一
        int index = (int) (Math.random() * list.size());
        System.out.println(list.get(index));

        // 方法二：shuffle 打乱顺序
        Collections.shuffle(list);
        System.out.println(list.get(0));

        // 取一定范围数据
        // start,end分别是第几个到第几个。
        // 注意的是此方法和substring一样，包含前不包含结尾，取下标索引
        // 另一个注意的地方是使用此方法会改变原始list列表，返回的这个子列表的幕后其实还是原列表；
        // 也就是说，修改这个子列表，将导致原列表也发生改变。
        List newList = list.subList(0, 2);

        Collections.sort(list); // 顺序排列
        Collections.reverse(list); // 倒序排列
        Collections.shuffle(list); // 混乱排序
        List<Object> objects = Collections.emptyList();
        // 多个字段排序
        //list.sort(Comparator.comparing(Student::getAge).thenComparing(Comparator.comparing(Student::getId)));
        // 返回 对象集合以类属性一升序排序
        /*list.stream().sorted(Comparator.comparing(类::属性一));
        // 返回 对象集合以类属性一降序排序 注意两种写法
        list.stream().sorted(Comparator.comparing(类::属性一).reversed());// 先以属性一升序,结果进行属性一降序
        list.stream().sorted(Comparator.comparing(类::属性一,Comparator.reverseOrder()));// 以属性一降序
        // 返回 对象集合以类属性一升序 属性二升序
        list.stream().sorted(Comparator.comparing(类::属性一).thenComparing(类::属性二));
        // 返回 对象集合以类属性一降序 属性二升序 注意两种写法
        // 先以属性一升序,升序结果进行属性一降序,再进行属性二升序
        list.stream().sorted(Comparator.comparing(类::属性一).reversed().thenComparing(类::属性二));
        // 先以属性一降序,再进行属性二升序
        list.stream().sorted(Comparator.comparing(类::属性一,Comparator.reverseOrder()).thenComparing(类::属性二));
        // 返回 对象集合以类属性一降序 属性二降序 注意两种写法
        // 先以属性一升序,升序结果进行属性一降序,再进行属性二降序
        list.stream().sorted(Comparator.comparing(类::属性一).reversed().thenComparing(类::属性二,Comparator.reverseOrder()));
        // 先以属性一降序,再进行属性二降序
        list.stream().sorted(Comparator.comparing(类::属性一,Comparator.reverseOrder()).thenComparing(类::属性二,Comparator
        .reverseOrder()));
        // 返回 对象集合以类属性一升序 属性二降序 注意两种写法
        // 先以属性一升序,升序结果进行属性一降序,再进行属性二升序,结果进行属性一降序属性二降序
        list.stream().sorted(Comparator.comparing(类::属性一).reversed().thenComparing(类::属性二).reversed());
        // 先以属性一升序,再进行属性二降序
        list.stream().sorted(Comparator.comparing(类::属性一).thenComparing(类::属性二,Comparator.reverseOrder()));*/

        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);
        intList.add(3);
        intList.add(4);
        intList.add(5);
        // 将一个list按三个一组分成N个小的list，分组后的list不再是原list的视图，原list的改变不会影响分组后的结果

        // 通过grouping by
        Map<Integer, List<Integer>> groups = intList.stream().collect(Collectors.groupingBy(s -> (s - 1) / 3));
        List<List<Integer>> subSets = new ArrayList<>(groups.values());
        List<Integer> lastPartition = subSets.get(2);

        // 通过partition by
        // partitioningBy 其实是一种特殊的 groupingBy，
        // 它依照条件测试的是否两种结果来构造返回的数据结构，get(true) 和 get(false) 能即为全部的元素对象
        Map<Boolean, List<Integer>> groups1 = intList.stream().collect(Collectors.partitioningBy(s -> s > 3));
        List<List<Integer>> subSets1 = new ArrayList<>(groups.values());
        List<Integer> lastPartition1 = subSets.get(1);

        // 过滤
        // 在数据规模较小、单次操作花费较小时，串行操作直接计算，而parallel并行（数据量无排序要求时使用）操作需先对数据分片后多线程处理
        // 数据量比较小（100W以下），一般业务场景下尽量用普通循环
        List<String> collect =
                list.stream().filter(entry -> entry.equals("a")).parallel().collect(Collectors.toList());
        // 判断是否存在某个值
        boolean a = list.stream().filter(entry -> entry.equals("a")).findAny().isPresent();
        //boolean a = list.stream().allMatch(entry -> entry.equals("a"));

        /**
         * 去重
         */
        // 根据name去重
        /*List<Person> unique = persons.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Person::getName))), ArrayList::new));*/
        // 根据name,sex两个属性去重
        /*List<Person> unique = persons.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                () -> new TreeSet<>(Comparator.comparing(o -> o.getName() + ";" + o.getSex()))), ArrayList::new));*/

        // 提取出list对象中的一个属性并去重
        //List<String> stIdList2 = stuList.stream().map(Person::getId).distinct().collect(Collectors.toList());

        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        list1.add("5");
        list1.add("6");

        List<String> list2 = new ArrayList<>();
        list2.add("2");
        list2.add("3");
        list2.add("7");
        list2.add("8");

        // 交集
        List<String> intersection = list1.stream().filter(item -> list2.contains(item)).collect(Collectors.toList());
        System.out.println("---交集 intersection---");
        intersection.parallelStream().forEach(System.out::println);

        // 差集 (list1 - list2)
        List<String> reduce1 = list1.stream().filter(item -> !list2.contains(item)).collect(Collectors.toList());
        System.out.println("---差集 reduce1 (list1 - list2)---");
        reduce1.parallelStream().forEach(System.out::println);

        // 差集 (list2 - list1)
        List<String> reduce2 = list2.stream().filter(item -> !list1.contains(item)).collect(Collectors.toList());
        System.out.println("---差集 reduce2 (list2 - list1)---");
        reduce2.parallelStream().forEach(System.out::println);

        // 并集
        List<String> listAll = list1.parallelStream().collect(Collectors.toList());
        List<String> listAll2 = list2.parallelStream().collect(Collectors.toList());
        listAll.addAll(listAll2);
        System.out.println("---并集 listAll---");
        listAll.parallelStream().forEachOrdered(System.out::println);

        // 去重并集
        List<String> listAllDistinct = listAll.stream().distinct().collect(Collectors.toList());
        System.out.println("---得到去重并集 listAllDistinct---");
        listAllDistinct.parallelStream().forEachOrdered(System.out::println);

        System.out.println("---原来的List1---");
        list1.parallelStream().forEachOrdered(System.out::println);
        System.out.println("---原来的List2---");
        list2.parallelStream().forEachOrdered(System.out::println);

    }
}
