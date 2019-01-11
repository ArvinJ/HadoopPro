# Singleton

单例对象的类必须保证只有一个实例存在。

## 一、懒汉式单例

指全局的单例实例在第一次被使用时构建。

```doc
对单例的实现可以分为两大类——懒汉式和饿汉式，他们的区别在于：
懒汉式：指全局的单例实例在第一次被使用时构建。(日常经常使用)
饿汉式：指全局的单例实例在类装载时构建。
```

```java
// Version 1
public class Single1 {
    private static Single1 instance;
    public static Single1 getInstance() {
        if (instance == null) {
            instance = new Single1();
        }
        return instance;
    }
}
```



#### 1.2把构造器改为私有的，这样能够防止被外部的类调用。

```java
// Version 1.1
public class Single1 {
    private static Single1 instance;
    private Single1() {}
    public static Single1 getInstance() {
        if (instance == null) {
            instance = new Single1();
        }
        return instance;
    }
}
```



注意：这种写法在大多数的时候也是没问题的。问题在于，当多线程工作的时候，如果有多个线程同时运行到if (instance == null)，都判断为null，那么两个线程就各自会创建一个实例——这样一来，就不是单例了。



#### 1.3synchronized

既然可能会因为多线程导致问题，那么加上一个同步锁吧！

线程安全，调用效率不高，但是能延时加载

```java
// Version 2 
public class Single2 {
    private static Single2 instance;
    private Single2() {}
    public static synchronized Single2 getInstance() {
        if (instance == null) {
            instance = new Single2();
        }
        return instance;
    }
}
/*
OK，加上synchronized关键字之后，getInstance方法就会锁上了。
如果有两个线程（T1、T2）同时执行到这个方法时，会有其中一个线程T1获得同步锁，得以继续执行，
而另一个线程T2则需要等待，当第T1执行完毕getInstance之后（完成了null判断、对象创建、获得返回值之后），
T2线程才会执行执行。——所以这端代码也就避免了Version1中，可能出现因为多线程导致多个实例的情况。
*/
```

但是，这种写法也有一个问题：给gitInstance方法加锁，虽然会避免了可能会出现的多个实例问题，但是会强制除T1之外的所有线程等待，实际上会对程序的执行效率造成负面影响。



#### 1.4双重检查（Double-Check）版本

由于JVM底层模型原因，偶尔会出问题，不建议使用

Version2代码相对于Version1d代码的效率问题，其实是为了解决1%几率的问题，而使用了一个100%出现的防护盾。那有一个优化的思路，就是把100%出现的防护盾，也改为1%的几率出现，使之只出现在可能会导致多个实例出现的地方。

```java
// Version 3 
public class Single3 {
    private static Single3 instance;
    private Single3() {}
    public static Single3 getInstance() {
        if (instance == null) {
            synchronized (Single3.class) {
                if (instance == null) {
                    instance = new Single3();
                }
            }
        }
        return instance;
    }
}
```

注意其中有两次if (instance == null)的判断，这个叫做『双重检查 Double-Check』。

 第一个if (instance == null)，其实是为了解决Version2中的效率问题，只有instance为null的时候，才进入synchronized的代码段——大大减少了几率。

 第二个if (instance == null)，则是跟Version2一样，是为了防止可能出现多个实例的情况。

```doc
—— 这段代码看起来已经完美无瑕了。

……

……

……

—— 当然，只是『看起来』，还是有小概率出现问题的。

 

这弄清楚为什么这里可能出现问题，首先，我们需要弄清楚几个概念：原子操作、指令重排。

 

知识点：什么是原子操作？

 

简单来说，原子操作（atomic）就是不可分割的操作，在计算机中，就是指不会因为线程调度被打断的操作。

 

比如，简单的赋值是一个原子操作：

m = 6; // 这是个原子操作
假如m原先的值为0，那么对于这个操作，要么执行成功m变成了6，要么是没执行m还是0，而不会出现诸如m=3这种中间态——即使是在并发的线程中。

 

而，声明并赋值就不是一个原子操作：

int n = 6; // 这不是一个原子操作
对于这个语句，至少有两个操作：

 

①声明一个变量n

②给n赋值为6

 

——这样就会有一个中间状态：变量n已经被声明了但是还没有被赋值的状态。

——这样，在多线程中，由于线程执行顺序的不确定性，如果两个线程都使用m，就可能会导致不稳定的结果出现。

 

知识点：什么是指令重排？

 

简单来说，就是计算机为了提高执行效率，会做的一些优化，在不影响最终结果的情况下，可能会对一些语句的执行顺序进行调整。

比如，这一段代码：

复制代码
int a ;   // 语句1 

a = 8 ;   // 语句2

int b = 9 ;     // 语句3

int c = a + b ; // 语句4
复制代码
正常来说，对于顺序结构，执行的顺序是自上到下，也即1234。

 

但是，由于指令重排的原因，因为不影响最终的结果，所以，实际执行的顺序可能会变成3124或者1324。

 

由于语句3和4没有原子性的问题，语句3和语句4也可能会拆分成原子操作，再重排。

 

——也就是说，对于非原子性的操作，在不影响最终结果的情况下，其拆分成的原子操作可能会被重新排列执行顺序。

 

OK，了解了原子操作和指令重排的概念之后，我们再继续看Version3代码的问题。

 

下面这段话直接从陈皓的文章(深入浅出单实例SINGLETON设计模式)中复制而来：

 

主要在于singleton = new Singleton()这句，这并非是一个原子操作，事实上在 JVM 中这句话大概做了下面 3 件事情。

 

1. 给 singleton 分配内存

 

2. 调用 Singleton 的构造函数来初始化成员变量，形成实例

 

3. 将singleton对象指向分配的内存空间（执行完这步 singleton才是非 null 了）

但是在 JVM 的即时编译器中存在指令重排序的优化。也就是说上面的第二步和第三步的顺序是不能保证的，最终的执行顺序可能是 1-2-3 也可能是 1-3-2。如果是后者，则在 3 执行完毕、2 未执行之前，被线程二抢占了，这时 instance 已经是非 null 了（但却没有初始化），所以线程二会直接返回 instance，然后使用，然后顺理成章地报错。

 

再稍微解释一下，就是说，由于有一个『instance已经不为null但是仍没有完成初始化』的中间状态，而这个时候，如果有其他线程刚好运行到第一层if (instance == null)这里，这里读取到的instance已经不为null了，所以就直接把这个中间状态的instance拿去用了，就会产生问题。

 

这里的关键在于——线程T1对instance的写操作没有完成，线程T2就执行了读操作。
```









#### 1.5终极版本：volatile

对于Version3中可能出现的问题（当然这种概率已经非常小了，但毕竟还是有的嘛~），解决方案是：只需要给instance的声明加上volatile关键字即可，Version4版本：

```java
// Version 4 
public class Single4 {
    private static volatile Single4 instance;
    private Single4() {}
    public static Single4 getInstance() {
        if (instance == null) {
            synchronized (Single4.class) {
                if (instance == null) {
                    instance = new Single4();
                }
            }
        }
        return instance;
    }
}
```

```doc
volatile关键字的一个作用是禁止指令重排，把instance声明为volatile之后，对它的写操作就会有一个内存屏障（什么是内存屏障？），这样，在它的赋值完成之前，就不用会调用读操作。

注意：volatile阻止的不singleton = new Singleton()这句话内部[1-2-3]的指令重排，而是保证了在一个写操作（[1-2-3]）完成之前，不会调用读操作（if (instance == null)）。

——也就彻底防止了Version3中的问题发生。
```



## 二、饿汉式单例

指全局的单例实例在类装载时构建的实现方式。

#### 2.1饿汉式实现

(线程安全，调用效率高，但是不能延时加载)

```java
//饿汉式实现

public class SingleB {

    private static final SingleB INSTANCE = new SingleB();

    private SingleB() {}

    public static SingleB getInstance() {

        return INSTANCE;

    }

}
```

#### 2.2其它实现方式

线程安全，调用效率高，可以延时加载

```java
// Effective Java 第一版推荐写法

public class Singleton {

    private static class SingletonHolder {

        private static final Singleton INSTANCE = new Singleton();

    }

    private Singleton (){}

    public static final Singleton getInstance() {

        return SingletonHolder.INSTANCE;

    }

}
/*
这种写法非常巧妙：

对于内部类SingletonHolder，它是一个饿汉式的单例实现，在SingletonHolder初始化的时候会由ClassLoader来保证同步，使INSTANCE是一个真·单例。

同时，由于SingletonHolder是一个内部类，只在外部类的Singleton的getInstance()中被使用，所以它被加载的时机也就是在getInstance()方法第一次被调用的时候。

——它利用了ClassLoader来保证了同步，同时又能让开发者控制类加载的时机。从内部看是一个饿汉式的单例，但是从外部看来，又的确是懒汉式的实现。

简直是神乎其技。

*/


```





枚举类   线程安全，调用效率高，不能延时加载，可以天然的防止反射和反序列化调用

```java
// Effective Java 第二版推荐写法

public enum SingleInstance {
    INSTANCE;
    public void fun1() { 
        // do something
    }
}

// 使用

SingleInstance.INSTANCE.fun1();
```

 神乎其技 由于创建枚举实例的过程是线程安全的，所以这种写法也没有同步的问题。



#### 2.3什么时候是类装载时？

```doc
前面提到了单例在类装载时被实例化，那究竟什么时候才是『类装载时』呢？

不严格的说，大致有这么几个条件会触发一个类被加载：

1. new一个对象时

2. 使用反射创建它的实例时

3. 子类被加载时，如果父类还没被加载，就先加载父类

4. jvm启动时执行的主类会首先被加载
```

## 三、如何选用

-单例对象 占用资源少，不需要延时加载，枚举 好于 饿汉

-单例对象 占用资源多，需要延时加载，静态内部类 好于 懒汉式











