 bin/flume-ng agent -c conf/ -f conf/spooldir-hdfs.conf -n a1 -Dflume.root.logger=INFO,console



```doc
.ＣＯＭＰＬＥＴＥ　读取后 立即删除
##注意：不能往监控目中重复丢同名文件
## 通过spooldir来监控文件内容的变化
a1.sources.r1.type = spooldir
a1.sources.r1.spoolDir = /home/data
a1.sources.r1.fileHeader = true
a1.sources.r1.fileSuffix = .wenjinzhu  #更换原先的.COMPLETE后缀为.wenjinzhu
a1.sources.r1.deletePolicy= immediate
```





#### Flume ERROR

# java.io.IOException: Not a data file

```doc

我们的解决方案是：把spooldir的source跟踪的目录下的元数据文件删除既可。

flume跟踪的目录下会自动产生 .flumespool文件夹，里面有 .flumespool-main.meta 文件。该文件我理解的是存储的读取文件的 offset，

在读取该文件的时候出现了异常，将该文件删除，一切OK啦。缺点呢是可能会造成数据少量的重复
https://blog.csdn.net/NicoleSmith/article/details/78517959
```







#### flume 拦截器

拦截器是插件式组件，

设置在source和channel之间，

source接收到的时间，在写入channel之前，

拦截器都 可以进行转换或者删除这些事件（event）。

每个拦截器只处理同一个source接收到的事件。

可以自定义拦截器。

**Timestamp Interceptor(时间戳拦截器)**

该拦截器的作用是将时间戳插入到flume的事件报头中。



如果不使用任何拦截器，flume接受到的只有message。

source连接到时间戳拦截器的配置：

a1.sources.r1.interceptors = timestamp 
a1.sources.r1.interceptors.timestamp.type=timestamp 
a1.sources.r1.interceptors.timestamp.preserveExisting=false

//preserveExisting false 如果设置为true，若事件中报头已经存在，不会替换时间戳报头的值





**Host Interceptor(主机拦截器)**

source连接到主机拦截器的配置：

a1.sources.r1.interceptors = host 
a1.sources.r1.interceptors.host.type=host 
a1.sources.r1.interceptors.host.useIP=false 
a1.sources.r1.interceptors.timestamp.preserveExisting=true



**静态拦截器(Static Interceptor)**

a1.sources.r1.interceptors = static 
a1.sources.r1.interceptors.static.type=static 
a1.sources.r1.interceptors.static.key=logs 
a1.sources.r1.interceptors.static.value=logFlume 
a1.sources.r1.interceptors.static.preserveExisting=false



**正则过滤拦截器(Regex Filtering Interceptor)**

```doc
在日志采集的时候，可能有一些数据是我们不需要的，这样添加过滤拦截器，可以过滤掉不需要的日志，也可以根据需要收集满足正则条件的日志。参数默认值描述 type 类型名称REGEX_FILTER regex .* 匹配除“\n”之外的任何个字符 excludeEvents false 默认收集匹配到的事件。如果为true，则会删除匹配到的event，收集未匹配到的。
--------------------- 
以下配置的拦截器就只会接收日志消息中带有rm 或者kill的日志。
```



a1.sources.r1.interceptors = regex 
a1.sources.r1.interceptors.regex.type=REGEX_FILTER 
a1.sources.r1.interceptors.regex.regex=(rm)|(kill) 
a1.sources.r1.interceptors.regex.excludeEvents=false



##### 过滤器

```xml
 <!-- flume核心依赖 -->
        <dependency>
            <groupId>org.apache.flume</groupId>
            <artifactId>flume-ng-core</artifactId>
            <version>1.6.0</version>
        </dependency>


<!-- https://mvnrepository.com/artifact/log4j/log4j -->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>

```



```java
package com.wenjin.zhu.flume;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.apache.log4j.*;
import java.util.ArrayList;
import java.util.List;

/*
 * 1. 实现interceptor接口，并实现其方法，接口完全限定名为：org.apache.flume.interceptor.Interceptor; 
 * 自定义拦截器内部添加静态内部类，实现Builder接口，并实现其方法，接口完全限定名为：Interceptor.Builder
 * */
public class MyInterceptor implements Interceptor {
	// 自定义拦截器，实现Interceptor接口，并且实现其抽象方法

	private static Logger logger = Logger.getLogger(MyInterceptor.class);
	// 自定义拦截器参数，用来接收自定义拦截器flume配置参数

	public MyInterceptor() {
		// 拦截器构造方法，在自定义拦截器静态内部类的build方法中调用，用来创建自定义拦截器对象。
		logger.info("----------自定义拦截器构造方法执行");
	}

	public void initialize() {
		// 该方法用来初始化拦截器，在拦截器的构造方法执行之后执行，也就是创建完拦截器对象之后执行
		logger.info("----------自定义拦截器的initialize方法执行");
	}

	public void close() {
		// 该方法主要用来销毁拦截器对象值执行，一般是一些释放资源的处理
		logger.info("----------自定义拦截器close方法执行");
	}

	/**
	 * 拦截source发送到通道channel中的消息
	 *
	 * @param event
	 *            接收过滤的event
	 * @return event 根据业务处理后的event
	 */
	public Event intercept(Event event) {
		// 用来处理每一个event对象，该方法不会被系统自动调用，一般在 List<Event> intercept(List<Event> events)
		// 方法内部调用。
		logger.info("----------intercept(Event event)方法执行，处理单个event");
		//logger.info("----------接收到的自定义拦截器参数值param值为：" + param);
		// TODO 这里编写event的处理代码

		// 获取事件对象中的字节数据
		byte[] arr = event.getBody();
		logger.info("----编写event的处理代码前>>"+new String(event.getBody()).toString());
		// 将获取的数据转换成大写
		event.setBody(("++"+new String(arr).toUpperCase()+"--").getBytes());
		// 返回到消息中
		return event;
	}

	// 接收被过滤事件集合
	public List<Event> intercept(List<Event> events) {

		//logger.info("----------intercept(List<Event> events)方法执行");
		List<Event> list = new ArrayList<Event>();
		for (Event event : events) {
			logger.info("-----start-----过滤event执行>>"+new String(event.getBody()).toString());
			// 获取事件对象中的字节数据
			byte[] arr = event.getBody();
			// 将获取的数据转换成大写
			if(new String(arr).trim().contains("11111")) {
				list.remove(event);
				logger.info("----------过滤11111执行--"+new String(arr).trim());
			}else if("".equals(new String(arr).trim())) {
				list.remove(event);
				logger.info("----------过滤空行执行--");
			}
			else {
				list.add(intercept(event));
			}
			
			
		}
		return list;
	}

	public static class Builder implements Interceptor.Builder {
		// 获取配置文件的属性
		public Interceptor build() {
			return new MyInterceptor();
		}

		public void configure(Context context) {

		}
	}
}

```



对package 右键 Export   -->选择JAR file   打包 放入lib 下面。







#### flume 优化

 capacity  :   存储在channel中的events的最大数量
 transactionCapacity ： 每次数据由channel到sink传输的最大events的数量





