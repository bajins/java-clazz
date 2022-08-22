package com.bajins.clazz.workersexecutors;


/**
 * Synchronized示例
 * 内存可见性：https://www.jianshu.com/p/d3fda02d4cae https://blog.csdn.net/u013887008/article/details/79681609
 * 线程释放锁时，JMM会把该线程对应的本地内存中的共享变量刷新到主内存中。
 * 线程获取锁时，JMM会把该线程对应的本地内存置为无效，从而使得被监视器保护的临界区代码必须从主内存中读取共享变量。
 *
 * @see java.util.concurrent
 * @see Monitor https://www.cnblogs.com/tomsheep/archive/2010/06/09/1754419.html
 * @see java.util.concurrent.locks 锁类包
 * @see Lock
 * @see Condition 条件变量，很大程度上是为了解决Object.wait/notify/notifyAll难以使用的问题
 * @see LockSupport
 * @see ReadWriteLock
 * @see ReentrantLock
 * @see ReentrantReadWriteLock
 * @see StampedLock
 * @see AbstractQueuedSynchronizer
 * @see AbstractQueuedSynchronizer.Node
 * @see java.util.concurrent.atomic 原子类包，可解决并发、stream使用外部变量的问题
 * @see AtomicBoolean 原子更新布尔类型
 * @see AtomicInteger 原子更新整型
 * @see AtomicIntegerArray 原子更新整型数组里的元素
 * @see AtomicIntegerFieldUpdater 原子更新整型的字段的更新器
 * @see AtomicLong 原子更新长整型
 * @see AtomicLongArray 原子更新长整型数组里的元素
 * @see AtomicLongFieldUpdater 原子更新长整型字段的更新器
 * @see AtomicMarkableReference 原子更新带有标记位的引用类型，可以使用构造方法更新一个布尔类型的标记位和引用类型
 * @see AtomicReference 原子更新引用类型
 * @see AtomicReferenceArray 原子更新引用类型数组里的元素
 * @see AtomicReferenceFieldUpdater 原子更新引用类型的字段
 * @see AtomicStampedReference 原子更新带有版本号的引用类型
 * @see Striped64 以下JDK1.8新增的原子类的抽象类
 * @see DoubleAccumulator
 * @see DoubleAdder Atomic、Adder在低并发环境下，两者性能很相似。在高并发环境下，Adder 有更高的吞吐量，但有更高的空间复杂度
 * @see LongAccumulator
 * @see LongAdder
 * @see ThreadLocal 线程本地变量（副本）
 * @see ThreadLocalRandom
 * @see InheritableThreadLocal
 */
public class SynchronizedLearning {

    // 同步实例方法，锁住的是当前实例对象，不同对象实例访问时不会阻塞
    public synchronized void method() {
    }

    // 同步静态方法，锁住的是当前类，不同对象实例访问时都会被阻塞
    public static synchronized void methods() {
    }

    public void test() {
        // 同步代码块，锁住的是当前实例对象，相对于同步实例方法粒度更小
        synchronized (this) {
        }
        // 同步代码块，锁住的是类，可以是当前类或任意类
        synchronized (SynchronizedLearning.class) {
        }
        // 同步代码块，任意实例对象作为锁，锁住的是指定的实例对象，
        // 注意Integer、Character、Short、Long等有缓存，
        // 如果超出范围锁住的是新对象
        String lock = "";
        // String.intern()是一个Native方法,当调用 intern 方法时，
        // 如果常量池中已经该字符串，则返回池中的字符串；
        // 否则将此字符串添加到常量池中，并返回字符串的引用。
        synchronized (lock.intern()) {
        }
    }

    // 同步静态方法，锁住的是类，可以是当前类或任意类，
    // 不同对象实例访问时都会被阻塞
    public static void testStatic() {
        synchronized (SynchronizedLearning.class) {
        }
    }
}
