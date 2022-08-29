package com.bajins.clazz;


import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.*;

/**
 * 集合学习：字典、列表、数组、映射、队列、堆栈<br/>
 * stream 流：JDK1.8同时增加了接口函数调用的使用：把接口方法当作参数。见 <link>com.bajins.clazz.FunctionLearning</link>
 * https://blog.csdn.net/mu_wind/article/details/109516995
 *
 * @see Set 集
 * @see List 列表
 * @see Map 映射 HashMap、HashTable
 * @see HashMap
 * @see SortedSet
 * @see SortedMap
 * @see HashSet
 * @see TreeSet
 * @see TreeMap
 * @see WeakHashMap
 * @see NavigableMap
 * @see NavigableSet
 * @see Hashtable
 * @see java.util.concurrent.ConcurrentHashMap
 * @see java.util.concurrent.ConcurrentMap
 * ConcurrentHashMap.newKeySet()
 * @see java.util.concurrent.ConcurrentNavigableMap
 * @see java.util.concurrent.ConcurrentSkipListMap
 * @see java.util.concurrent.ConcurrentSkipListSet
 * @see ArrayList
 * @see LinkedList 可被用作堆栈（stack），队列（queue）或双向队列（deque）
 * @see AbstractList
 * @see AbstractSequentialList
 * @see Vector 性能是最差，所有的方法都加了synchronized来同步
 * @see Collections.SynchronizedList 能把所有 List 接口的实现类转换成线程安全的List，比 Vector 有更好的扩展性和兼容性
 * https://blog.csdn.net/weixin_44203321/article/details/114065191
 * https://blog.csdn.net/u012816626/article/details/111090575
 * @see CopyOnWriteArrayList 添加删除时加锁(ReentrantLock，非synchronized同步锁)，进行复制替换操作，最后再释放锁，插入的过程中会创建新的数组
 * @see CopyOnWriteArraySet
 * @see Stack 后进先出的堆栈
 * @see Delayed
 * @see AbstractQueue
 * @see Queue 队列
 * @see BlockingQueue 阻塞队列接口 https://www.cnblogs.com/WangHaiMing/p/8798709.html
 * @see WorkQueue
 * @see WorkQueueImpl
 * @see ArrayBlockingQueue 一个由数组结构组成的有界阻塞队列。
 * @see LinkedBlockingQueue 一个由链表结构组成的有界阻塞队列。
 * @see SynchronousQueue 一个不存储元素的阻塞队列，即直接提交给线程不保持它们。
 * @see PriorityBlockingQueue 一个支持优先级排序的无界阻塞队列。
 * @see DelayQueue 一个使用优先级队列实现的无界阻塞队列，只有在延迟期满时才能从中提取元素。
 * @see TransferQueue
 * @see LinkedTransferQueue 一个由链表结构组成的无界阻塞队列。与SynchronousQueue类似，还含有非阻塞方法。
 * @see BlockingDeque
 * @see LinkedBlockingDeque 一个由链表结构组成的双向阻塞队列。
 * @see ConcurrentLinkedDeque
 * @see ConcurrentLinkedQueue
 * @see AbstractCollection
 * @see Collection 集合框架的父接口 List、Set
 * @see Collections 各种有关集合操作的 静态多态方法 工具类
 * @see Collections#nCopies(int, Object)
 * @see Collections#EMPTY_LIST
 * @see Collections#synchronizedList
 * @see Arrays
 * @see Arrays#asList(Object[])
 * @see IntStream
 * @see LongStream
 * @see DoubleStream
 * @see Stream#collect(Collector) https://blog.csdn.net/qq_33591903/article/details/107990065
 * @see Collector 作为Stream的collect方法的参数
 * @see Collectors 实现Collector的工具类
 * @see Comparable 排序接口
 * @see Comparator 比较接口
 * @see Object#hashCode()
 */
public class CollectionsLearning {

    /**
     * Map学习
     */
    public static void MapLearning() {
        HashMap<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 1000; i++) { // 生成一千个数据
            map.put(i, i + "");
        }

        // entrySet迭代，不能移除元素
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println("方法一：key =" + entry.getKey() + "---value=" + entry.getValue());
        }
        // Iterator迭代，可调用entries.remove()移除元素：使用了泛型，如果不使用泛型键值则需要强转
        Iterator<Map.Entry<Integer, String>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, String> entry = entries.next();
            //entries.remove(); // 移除后，该循环代码块内还能正常获取到当前index数据
            System.out.println("方法一iterator：key = " + entry.getKey() + "--value=" + entry.getValue());
        }

        // 遍历键
        for (Integer key : map.keySet()) {
            // 遍历键获取值：效率低，通过key得到value值更耗时
            String value = map.get(key);
            System.out.println("方法二：Key = " + key + ", Value = " + value);
        }
        Iterator<Integer> keyIterator = map.keySet().iterator();
        while (keyIterator.hasNext()) {
            Integer key = keyIterator.next();
            // 遍历键获取值：效率低，通过key得到value值更耗时
            String value = map.get(key);
            //keyIterator.remove();  // 移除后，该循环代码块内还能正常获取到当前index数据
            System.out.println("方法二iterator：Key = " + key + ", Value = " + value);
        }
        // 遍历值
        for (String value : map.values()) {
            System.out.println("方法二：value = " + value);
        }
        Iterator<String> iterator = map.values().iterator();
        //Iterator<String> iterator = map.values().stream().iterator(); // 不能调用remove()方法
        //Iterator<String> iterator = map.values().parallelStream().iterator(); // 不能调用remove()方法
        while (iterator.hasNext()) {
            String next = iterator.next();
            //iterator.remove();  // 移除后，该循环代码块内还能正常获取到当前index数据
            System.out.println("方法二iterator：value = " + next);
        }

        long start = System.currentTimeMillis();
        List<Integer> collect = map.keySet().stream().collect(Collectors.toList());
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        ArrayList<Integer> integers = new ArrayList<>(map.keySet());
        System.out.println(System.currentTimeMillis() - start); // stream 比 Arrays.copyOf 效率低

    }

    public static void main(String[] args) {
        // https://juejin.cn/post/6844903766106325006
        // 数组转List
        List<String> list = Arrays.asList("a", "b", "c", "d");
        /*list = Stream.of("a", "b", "c", "d").collect(Collectors.toList());
        list = Stream.of("a", "b", "c", "d").collect(Collectors.toCollection(ArrayList::new));
        list = new ArrayList<String>() {{// 使用匿名内部类初始化时构造数据
            addAll(Arrays.asList("a", "b", "c", "d"));
        }};*/
        // 取对象中的字段放到新的List中
        //List<String> userIds = list.stream().map(a -> a.getUserid()).collect(Collectors.toList());


        /*
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
        accounts.stream().collect(Collectors.toMap(Account::getId, account -> account));
        // 拼接key
        accounts.stream().collect(Collectors.toMap(k -> String.format("%s|%s", k.getId(), k.getUsername()), k -> k));
        accounts.stream().collect(Collectors.toMap(k -> {
            return k.getId() + k.getUsername();
        }, k -> k));
        accounts.stream().collect(HashMap::new, (n, v) -> n.put(v.getId() + k.getUsername(), v), HashMap::putAll);
        accounts.stream().collect(Collectors.toMap(new Function<Account, String>() {
            @Override
            public String apply(Account account) {
                return account.getId() + account.getUsername();  // (id+username)作为key
            }
        }, k -> k));
        */


        /*
         * List转Set
         */
        //Set<String> userIdSet = userList.stream().map(User::getId).collect(Collectors.toSet());
        //Set<String> userIdSet = userList.stream().map(User::getId).collect(Collectors.toCollection(TreeSet::new));


        /*
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

        /*
         * 取一定范围数据
         * start,end分别是第几个到第几个。
         * 注意的是此方法和substring一样，包含前不包含结尾，取下标索引
         * 另一个注意的地方是使用此方法会改变原始list列表，返回的这个子列表的幕后其实还是原列表；
         * 也就是说，修改这个子列表，将导致原列表也发生改变。
         */
        List<String> newList = list.subList(0, 2);
        /*
         * 排序
         */
        // 会改变原list数据
        Collections.<String>sort(list); // 顺序排列，根据类中字段排序可实现Comparable接口
        // 多个字段排序 String.CASE_INSENSITIVE_ORDER不区分字符串大小写
        //list.sort(Comparator.comparing(Student::getAge).thenComparing(Comparator.comparing(Student::getId)));
        /*Collections.sort(list, new Comparator<Field>() {
            @Override
            public int compare(Field u1, Field u2) {
                int diff = u1.hashCode() - u2.hashCode();
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return 0; // 相等
            }
        });*/
        Collections.reverse(list); // 倒序排列
        Collections.shuffle(list); // 混乱排序
        // 不改变原list，返回一个list的副本（ 排序之后的）
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

        // IntStream.range(0, 10).boxed().collect(Collectors.toList());
        List<Integer> intList = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        /*List<Integer> intList = new ArrayList<>(
                IntStream.range(0, 10).boxed().collect(Collectors.toCollection(ArrayList::new))
        );*/
        /*
         * 分组：
         * 将一个list按三个一组分成N个小的list，分组后的list不再是原list的视图，原list的改变不会影响分组后的结果
         */
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


        /*
         * 统计求和
         */
        Map<String, Object> map = new HashMap<>();
        map.put("name", "a");
        map.put("price", 500);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "a");
        map2.put("price", 1500);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("name", "b");
        map3.put("price", 300);

        List<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);
        maps.add(map2);
        maps.add(map3);
        Map<String, List<Map<String, Object>>> groupMap =
                maps.stream().collect(Collectors.groupingBy(e -> (String) e.get("name")));

        groupMap.forEach((k, vList) -> { // 循环遍历分组
            IntSummaryStatistics iss = vList.stream().collect(
                    Collectors.summarizingInt(e -> (Integer) e.get("price")));
            System.out.print(k);
            System.out.print(" = ");
            System.out.println(iss.getSum()); //求和
        });
        // 分组求和一步到位
        Map<String, IntSummaryStatistics> collect1 = maps.stream().collect(
                Collectors.groupingBy(e -> (String) e.get("name")
                        , Collectors.summarizingInt(e -> (Integer) e.get("price"))
                ));
        // 分组求和后并取最大值
        Map<String, Map<String, Object>> collect2 = maps.parallelStream().filter(Objects::nonNull).collect(
                Collectors.groupingBy(e -> (String) e.get("name"), Collectors.collectingAndThen(
                        Collectors.reducing((c1, c2) -> ((Integer) c1.get("price") > (Integer) c2.get("price")) ? c1
                                : c2)
                        , Optional::get)
                ));
        int price = maps.stream().mapToInt(x -> (Integer) x.get("price")).sum();
        long price$ = maps.stream().mapToInt(x -> (Integer) x.get("price")).summaryStatistics().getSum();
        int exact = Math.toIntExact(price$);
        int price_ = maps.stream().mapToInt(x -> (Integer) x.get("price")).reduce(0, Integer::sum);
        // BigDecimal使用聚合函数求和
        BigDecimal $price = maps.stream().map(x -> new BigDecimal((Integer) x.get("price"))).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        BigDecimal _price = maps.stream().collect(Collectors.reducing(BigDecimal.ZERO,
                x -> new BigDecimal((Integer) x.get("price")), BigDecimal::add));
        /*
         * 获取重复数据
         */
        // 方式一：distinct, 只能获取重复的主键
        List<Object> dl1 = maps.stream().map(t -> t.get("name")).distinct().collect(Collectors.toList());
        System.out.println(dl1.size() != list.size() ? "有重复" : "无重复");
        // 方式二：先分组统计
        Map<Object, Long> dl2 = maps.stream().collect(Collectors.groupingBy(t -> t.get("name"), Collectors.counting()));
        // 过滤出大于1（重复）的数据
        List<Object> collect3 = dl2.keySet().stream().filter(key -> dl2.get(key) > 1).collect(Collectors.toList());
        System.out.println("重复的数据：" + collect3);
        // 方式三：同方式二
        List<Map<String, Long>> collect4 = dl2.keySet().stream().filter(key -> dl2.get(key) > 1).map(key -> {
            Map<String, Long> tamp = new HashMap<>();
            tamp.put((String) key, dl2.get(key));
            return tamp;
        }).collect(Collectors.toList());
        System.out.println("重复的数据：" + collect4);
        /*
         * 过滤：
         * 在数据规模较小、单次操作花费较小时，串行操作直接计算，
         * 而parallel并行（数据量无排序要求时使用）操作需先对数据分片后多线程处理
         * 数据量比较小（100W以下），一般业务场景下尽量用普通循环
         */
        List<String> collect =
                list.stream().filter(entry -> entry.equals("a")).parallel().collect(Collectors.toList());
        // 判断是否存在某个值
        boolean a = list.stream().filter(entry -> entry.equals("a")).findAny().isPresent();
        boolean b = list.stream().allMatch(entry -> entry.equals("a"));

        /*
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

        // 交集 两个列表都有的数据
        List<String> intersection = list1.stream().filter(list2::contains).collect(Collectors.toList());
        System.out.println("---交集 intersection---");
        // 获取集合 重复元素
        List<String> intersection1 = list1.stream()
                .collect(Collectors.toMap(e -> e, e -> 1, (x, y) -> x + y)) // 获取元素出现频率的 Map ,键为元素 值为元素重复次数
                .entrySet().stream() // Set<Entry> 转为 Stream<Entry>
                .filter(e -> e.getValue() > 1) // 过滤元素出现次数大于 1 的 entry
                .map(e -> e.getKey()) // 获得 entry 的键（重复的元素）对应的 Stream
                .collect(Collectors.toList()); // 转换为 List
        intersection.parallelStream().forEach(System.out::println);

        // 差集 (list1 - list2) 前面一个列表在后面一个列表中没有的数据
        List<String> reduce1 = list1.stream().filter(item -> !list2.contains(item)).collect(Collectors.toList());
        System.out.println("---差集 reduce1 (list1 - list2)---");
        reduce1.parallelStream().forEach(System.out::println);

        // 并集 合并两个列表
        List<String> listAll = list1.parallelStream().collect(Collectors.toList());
        List<String> listAll2 = list2.parallelStream().collect(Collectors.toList());
        listAll.addAll(listAll2);
        System.out.println("---并集 listAll---");
        listAll.parallelStream().forEachOrdered(System.out::println);
        List<String> listAll3 =
                Stream.of(list1, list2).flatMap(Collection::stream).distinct().collect(Collectors.toList());

        // 去重并集
        List<String> listAllDistinct = listAll.stream().distinct().collect(Collectors.toList());
        System.out.println("---得到去重并集 listAllDistinct---");
        listAllDistinct.parallelStream().forEachOrdered(System.out::println);

        System.out.println("---原来的List1---");
        list1.parallelStream().forEachOrdered(System.out::println);
        System.out.println("---原来的List2---");
        list2.parallelStream().forEachOrdered(System.out::println);

        /*
         * 合并多个list
         */
        List<String> listAll4 = list1.stream().flatMap(listContainer -> list2.stream()).collect(Collectors.toList());

        List<List<String>> lists = new ArrayList<>(2);
        lists.add(listAll);
        lists.add(listAll2);
        lists.add(listAll3);
        // 多层嵌套合并
        List<String> listOf1 = lists.stream().flatMap(List::stream).collect(Collectors.toList());
    }


    private static <I, R> Function<I, R> castingIdentity() {
        return i -> (R) i;
    }

    public static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner,
                      Function<A, R> finisher, Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner,
                      Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, castingIdentity(), characteristics);
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }
    }

    static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

    public static <T> Collector<T, ?, BigDecimal> summingBigDecimal(ToBigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<>(() -> new BigDecimal[1], (a, t) -> {
            if (a[0] == null) {
                a[0] = BigDecimal.ZERO;
            }
            a[0] = a[0].add(mapper.applyAsBigDecimal(t));
        }, (a, b) -> {
            a[0] = a[0].add(b[0]);
            return a;
        }, a -> a[0], CH_NOID);
    }

    @FunctionalInterface
    public interface ToBigDecimalFunction<T> {
        BigDecimal applyAsBigDecimal(T value);
    }
}
