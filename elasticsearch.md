#一、安装elasticsearch6.4
注意：在此安装之前，不管是linux系统还是windows系统必须先安装好jdk1.8及更高版本。
JDK1.8安装方式在我的简书中有可以自行查找一下。
##windows 10  环境下
###1.先点击下面的下载地址进行下载
https://www.elastic.co/downloads/elasticsearch
（建议使用elastic时，下载elastic 最新版本，每个版本之前的差异比较大，5.x以后把之前低版本的api类及方法更改与删除很多，前后分别实现的过程差别大）在这里我使用的是elasticsearch6.4
![elasticsearch6.4.3安装包.png](https://upload-images.jianshu.io/upload_images/2848877-2d360183931ad99a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
这里是elasticsearch的所有历史版本
![elasticsearch历史版本.png](https://upload-images.jianshu.io/upload_images/2848877-2a4b604f9dc0ed13.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

解压下载的压缩文件，我们可以看到
![解压后的文件内容](https://upload-images.jianshu.io/upload_images/2848877-3c629dae2ceb6ae6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###2.修改解压包中的配置文件
####2.1打开并编辑D:\elasticsearch-6.4.3\elasticsearch-6.4.3\config\elasticsearch.yml

```xml
# 集群的名字  
cluster.name: zwj-cluster
# 节点名字  
node.name: node-1   
# 数据存储目录（多个路径用逗号分隔）也就是 索引数据的存储路径
path.data: /home/zwj/es/data  
# 日志目录  
path.logs: /home/zwj/es/logs  
#本机的ip地址
network.host: 192.168.1.102 
#设置集群中master节点的初始列表，可以通过这些节点来自动发现新加入集群的节点
discovery.zen.ping.unicast.hosts: ["192.168.1.102"]
#discovery.zen.minimum_master_nodes: 2  
# 设置节点间交互的tcp端口（集群）,(默认9300)  
transport.tcp.port: 9300  
# 设置对外服务的http端口，默认为9200 
http.port: 9200  
# 增加参数，使head插件可以访问es  
http.cors.enabled: true  
http.cors.allow-origin: "*"
#指定该节点是否有资格被选举成为master节点，默认是true，es是默认集群中的第一台机器为master，如果这台机挂了就会重新选举master
node.master: true 
#允许该节点存储数据(默认开启)  
node.data: true

```

####2.2 编辑D:\elasticsearch-6.4.3\elasticsearch-6.4.3\config\jvm.options
![原来的配置](https://upload-images.jianshu.io/upload_images/2848877-5ddeb4a008a60a1b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
-Xms1g 改成-Xms512m
-Xmx1g 改成-Xmx512m
![修改后的](https://upload-images.jianshu.io/upload_images/2848877-065cb2e633defa04.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

####2.3运行elasticsearch
进入/bin目录点击elasticsearch.bat运行elasticsearch.
![运行成功](https://upload-images.jianshu.io/upload_images/2848877-b0f239ce1edf6d9f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###3.安装插件
####3.1安装head
在elasticsearch5.0之前安装都是用plugin命令，

在elasticsearch5.0之后的高版本安装head需要先安装node和grunt
node下载地址
https://nodejs.org/en/download/
选择对应的系统及位版本msi下载，完成后双击安装。
安装完成用cmd进入安装目录执行 node -v可查看版本号。
![](https://upload-images.jianshu.io/upload_images/2848877-d46dc789dbd91237.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
执行 npm install -g grunt-cli 安装grunt ，安装完成后执行grunt -version查看是否安装成功，会显示安装的版本号
![](https://upload-images.jianshu.io/upload_images/2848877-b319ee881d7a7d0b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
现在安装head
在https://github.com/mobz/elasticsearch-head中下载head插件，选择下载zip,可以通过git clone 下来
zip解压得到文件夹elasticsearch-head-master，编辑Gruntfile.js加上hostname:'*'
![](https://upload-images.jianshu.io/upload_images/2848877-ab12e778f6c75b59.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
在cmd 窗口中定位到自己elasticsear head master路径
![](https://upload-images.jianshu.io/upload_images/2848877-49d9807322b1a044.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
下执行npm install 安装完成后执行grunt server 或者npm run start 运行head插件，如果不成功重新安装grunt。
![](https://upload-images.jianshu.io/upload_images/2848877-bb11fffb04f3328d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
运行grunt server 
![](https://upload-images.jianshu.io/upload_images/2848877-c85e3a4f2016d616.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
以上完成 了head插件的安装了。
通过浏览器访问http://localhost:9100或http://本机ip地址:9100 如下图
![](https://upload-images.jianshu.io/upload_images/2848877-3a867507f36474b3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
我们可以看到 http://localhost:9200 未连接，是因为在此之前 elasticsearch 的配置文件 elasticsearch.yml 中 network.host配置了准确的ip地址。所以 要把 http://localhost:9200改为 http://192.168.1.102:9200
就可以了。
![](https://upload-images.jianshu.io/upload_images/2848877-5b9accd1d0cc205f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
在这个页面中可以进行相应的操作啦。
####3.2安装ik分词器
ik分词器的下载地址https://github.com/medcl/elasticsearch-analysis-ik
通过git clone 或者直接zip 下载下来后解压
在我们的elasticserarch安装目录F:\letgo\elasticsearch-6.4.3\elasticsearch-6.4.3\plugins下新建 ik 文件夹
把elasticsearch-analysis-ik文件内容copy放入 ik 文件夹下
![](https://upload-images.jianshu.io/upload_images/2848877-17c744cf2881bc8b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
重启es，可以看到多了loaded plugin analysis-ik,这就要以啦！
![](https://upload-images.jianshu.io/upload_images/2848877-f3aefc079fe6547b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###4.遇到的错误
可以查看D:\es2\logs下面的日志
####4.1 elasticsearch 运行控件窗口出现中国乱码
解决办法：编辑/config/jvm.option文件，把utf-8改成GBK即可。 
####4.2 elasticsearch 启动错误闪退1Caused by: java.net.BindException: Cannot assign requested address: bind
解决办法：编辑/config/elasticsearch.yml  network.host:本地ip地址，
本地ip地址可通过cmd下ipconfig查看 ，一定要是正确的本地ip地址。

##linux  centos6.5 环境下 ElasticSearch搭建
####1、准备1台linux centos64位机器

####2、安装包解压
准备好elasticsearch6.4.3安装包并解压、然后执行命令，
tar -zxvf elasticsearch6.4.3.tar.gz

![](https://upload-images.jianshu.io/upload_images/2848877-a0df0fb4a86da306.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####3、修改配置文件
vim config/elasticsearch.yml
![](https://upload-images.jianshu.io/upload_images/2848877-78d166523b5af4eb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
编辑vim config/jvm.options
![](https://upload-images.jianshu.io/upload_images/2848877-6f3750a13217f4c1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####4、 运行elasticsearch
cd bin
./elasticsearch
./elasticsearch -d
![](https://upload-images.jianshu.io/upload_images/2848877-5d486b0cb750227a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
运行中没有报错，但在浏览器上就是无法访问可能是防火墙导致的。关闭一下或都添加对面开放端口9200 ，9300 即可。 


#####报错1，用户不能用root。
添加新用户wenjinzhu并设置密码
![](https://upload-images.jianshu.io/upload_images/2848877-61294074b3ca8c06.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#####切换用户，再次运行elasticsearch
![](https://upload-images.jianshu.io/upload_images/2848877-9d99dbbea6a8919a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
报错2，权限不够。
切换到root 
su root
输入密码。
定位 cd /home下
chmod -R 777 zwj
su wenjinzhu
cd /home/ela/elasticsearch6.4.3/bin
./elasticsearch
./elasticsearch -d    
![](https://upload-images.jianshu.io/upload_images/2848877-0f6fbc8078d9fc4b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
dna
成功运行。在浏览器地址栏输入 http://172.16.53.27:9200访问如下
![](https://upload-images.jianshu.io/upload_images/2848877-9f568be0f49c6d58.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

ps -ef|grep ela 查看当前进程中是否运行着elasticsearch

elasticsearch 后台运行命令
./elasticsearch -d
####5、elasticsearch 关闭
![](https://upload-images.jianshu.io/upload_images/2848877-6de62a836b69af97.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
前台运行:可以通过”CTRL+C”组合键来停止运行 
后台运行，可以通过”kill -9 进程号”停止.
####6、elasticsearch运行时报的其它错误
```doc
vim /etc/security/limits.conf

* soft nofile 262144                                                                                                                                              
* hard nofile 262144                                                                                                                                              
es soft memlock unlimited                                                                                                                                         
es hard memlock unlimited 

```
#####6.1 max file descriptor 
[1]: max file descriptors [4096] for elasticsearch process is too low, increase to at least [65536] 
[2]: max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144] 
解决方法，请用root权限修改，修改完记得重启elasticsearch和使用配置参数在环境里面生效（重新登录用户）：(如果还有错误请把数字再设置大一点)
vi /etc/security/limits.conf 
```xml
*  soft  nofile  65536

*  hard  nofile  131072

*  soft  nproc  2048

*  hard  nproc 4096
```
#####6.2 max number of threads 
max number of threads [1024] for user [user] is too low, increase to at least [2048] 
解决方法： (* 表示对所有用户有效)
vi /etc/security/limits.d/90-nproc.conf 
```xml
*  soft  nproc  1024
```
#####6.3 max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144] 
 ```xml
临时设置：sudo sysctl -w vm.max_map_count=262144 
永久修改： 
修改/etc/sysctl.conf 文件，添加 “vm.max_map_count”设置 
并执行：sysctl -p
```
![](https://upload-images.jianshu.io/upload_images/2848877-c92f793b7641ee68.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#####6.4 出现 java.net.BindException: 地址已在使用
解决办法：首先查看 本机ip地址与networkhost的地址是否一样，然后在看默认端口9200，9300是否 被占用
kill -9 进程号
#####6.5但凡出现java.nio.file.AccessDeniedException这样的错误
一定是新生成 的文件被拒绝访问了没有root权限。
 su root   
chmod -R 777  该文件
####7、安装head插件
#####elasticsearch5.0之前安装head方法
```doc
直接安装
./bin/plugin install mobz/elasticsearch-head

zip包安装
1. https://github.com/mobz/elasticsearch-head下载zip 解压
2. 建立elasticsearch-2.4.0\plugins\head文件
3. 将解压后的elasticsearch-head-master文件夹下的文件copy到head
4. 运行es
验证  浏览器输入 http://172.16.53.25:9200/_plugin/head

```

#####elasticsearch5.0之后高版本的安装head方法
下载 elasticsearch-head 或者 git clone 到随便一个文件夹
https://github.com/mobz/elasticsearch-head
安装nodejs
下载Node.js
[https://nodejs.org/en/download/](https://nodejs.org/en/download/)
选择 linux 64位
下载后得到node-v10.13.0-linux-x64.tar.xz
解压
tar -zxvf node-v8.9.4-linux-x64.tar.xz
有问题用下面的
tar -xvf node-v8.9.4-linux-x64.tar.xz
修改 /etc/profile变为全局可用
 vim /etc/profile 
```xml
export NODEJS_HOME=/home/nodejs/node-v10.13.0-linux-x64
export PATH=$PATH:$NODEJS_HOME/bin
```
![](https://upload-images.jianshu.io/upload_images/2848877-03952c988f98e7bc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

source /etc/profile 执行
测试
node -v 
![](https://upload-images.jianshu.io/upload_images/2848877-3c0a9efc44dc6b80.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


在[https://github.com/mobz/elasticsearch-head](https://github.com/mobz/elasticsearch-head)中下载head插件，选择下载zip,可以通过git clone 下来

cd /home/es/elasticsearch-head-master 
修改Gruntfile.js
配置添加  hostname:'*'
![](https://upload-images.jianshu.io/upload_images/2848877-dd22d8a2e2e61a77.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

npm install -g grunt-cli 
可能会遇到权限问题报错
![](https://upload-images.jianshu.io/upload_images/2848877-ae9d8ebb012afa0d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

su root
npm install -g grunt-cli 再次执行
npm install 
grunt server  
或 在后台运行 grunt server &  
http://172.16.16.27:9100成功

####8、安装ik分词器
下载地址，寻找与当前安装es版本一致的ik
https://github.com/medcl/elasticsearch-analysis-ik/releases
解压elasticsearch-analysis-ik-6.4.3.zip
把它里面的内容copy到 elasticsearch6.4.3解压目录 的/plugin/ik下面。
重启es即可。

#二、基于Java开发的API
elasticsearch maven 下面所依赖的jar.
```xml
 <!-- ela start -->
<dependency>
				<groupId>com.fasterxml.jackson.core</groupId>
				<artifactId>jackson-core</artifactId>
				<version>2.9.7</version>
			</dependency>
			<dependency>
				<groupId>com.fasterxml.jackson.core</groupId>
				<artifactId>jackson-databind</artifactId>
				<version>2.9.7</version>
			</dependency>
			<dependency>
				<groupId>com.fasterxml.jackson.core</groupId>
				<artifactId>jackson-annotations</artifactId>
				<version>2.9.7</version>
			</dependency>
			<dependency>
				<groupId>net.sf.json-lib</groupId>
				<artifactId>json-lib</artifactId>
				<version>2.4</version>
				<classifier>jdk15</classifier>
			</dependency>
			<!-- <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-elasticsearch</artifactId>
		</dependency> -->
		
		<dependency>
			<groupId>org.elasticsearch.client</groupId>
			<artifactId>transport</artifactId>
			<version>5.6.3</version>
		</dependency>
		<!-- ela end -->
```


##1、创建TransprostClient单例获取instance 对象
```java
package com.wenjin.elasticsearch;

import java.net.InetAddress;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * 
 * 
 * @Title: TransprostClientManager.java
 * @Package com.wenjin.elasticsearch
 * @Description:
 * @author: wenjin.zhu
 * @date: 2018年11月21日 下午1:30:42
 * @version V1.0
 */
public class TransprostClientManager {
	private static volatile TransportClient instance;

	private TransprostClientManager() {
	}

	@SuppressWarnings("resource")
	public static TransportClient getInstance(){
		Settings settings = Settings.builder().put("cluster.name", "zwj-cluster").put("client.transport.sniff", true).build();
		if (instance == null) {
			synchronized (TransportClient.class) {
				if (instance == null) {
					try {
						instance = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.16.52.1"), 9300));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return instance;
	}
}

```  
##2、ES CRUD增删改查
```java
package com.wenjin.zhu.elasticsearch;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * 
 * @Title: EsJavaApi.java
 * @Package com.wenjin.elasticsearch
 * @Description: elastic api
 * @author: wenjin.zhu
 * @date: 2018年11月14日 下午2:18:24
 * @version V1.0
 */
public class EsJavaApi {

	private static TransportClient client = TransprostClientManager.getInstance();
	public static String index = "yuqing";
	public static String type = "targetData";

	public static void main(String[] args) {

		TargetData targetData = new TargetData();
		targetData.setContentText("contentText");
		targetData.setTitle("title");
		// 创建索引文档
		save(targetData);

		// 搜索文档
		get("00ca365ae9024553b216ce9a25a86246");

		// 更新文档
		update(targetData);

		// 删除文档
		delete("00ca365ae9024553b216ce9a25a86246");
		client.close();
	}

	@SuppressWarnings("deprecation")
	public static void save(TargetData target) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			byte[] bytes = mapper.writeValueAsBytes(target);
			IndexResponse response = client.prepareIndex(index, type, target.getId()).setSource(bytes).get();

			System.out.println(response.getResult()); // CREATED
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}

	public static void get(String id) {
		GetResponse response = client.prepareGet(index, type, id).setOperationThreaded(false) // 默认为true,在不同的线程执行
				.get();
		System.out.println(response.getSourceAsString());
	}

	public static String show(String id) {
		GetResponse response = client.prepareGet(index, type, id).setOperationThreaded(false) // 默认为true,在不同的线程执行
				.get();
		return response.getSourceAsString();
	}

	@SuppressWarnings("deprecation")
	public static void update(TargetData target) {
		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index(index);
		updateRequest.type(type);
		updateRequest.id("2");

		ObjectMapper mapper = new ObjectMapper();

		try {
			updateRequest.doc(mapper.writeValueAsBytes(target));
			client.update(updateRequest).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean delete(String id) {
		try {
			DeleteResponse response = client.prepareDelete(index, type, id).get();
			System.out.println(response);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void close() {
		client.close();
	}

}


```

##3、ES 排序 及分页

```java
package com.wenjin.zhu.elasticsearch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

/**
 * 
 * 
 * @Title: EsJavaApi.java
 * @Package com.wenjin.elasticsearch
 * @Description: elastic api
 * @author: wenjin.zhu
 * @date: 2018年11月14日 下午2:18:24
 * @version V1.0
 */
public class EsJavaApi {

	private static TransportClient client = TransprostClientManager.getInstance();
	public static String index = "yuqing";
	public static String type = "targetData";

	public static void main(String[] args) {
		
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("title", "*泄露个人隐*");
		map2.put("is_sen", "1");
		map2.put("over_sea", "1");
		// 查询满足条件后第一页的数据
		List<TargetData> targetlist = getDataByMuchillegible(type, map2, 1,"0","1");
		// 查询满足条件后的数据总记录数
		int totalCount = getDataByMuchillegibleCount(type, map2, "0", "1");
		// 总页数
		int pageCount = (totalCount + 10 - 1) / 10;
		
	}

	

	/**
	 * 
	 * @Title: getDataByMuchillegible   
	 * @Description: 多条件 模糊查询
	 * @param: @param type
	 * @param: @param map
	 * @param: @param page
	 * @param: @return      
	 * @return: List<Map<String,Object>>      
	 * @throws
	 */
	public static List<TargetData> getDataByMuchillegible(String type, Map<String, String> map, int page,
			String sortType, String timeType) {
		int from = 0;
		if (page == 0) {
			from = 0;
		} else if (page == 1) {
			from = 0;
		} else {
			from = page * 10 - 10;
		}
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		if (("2").equals(map.get("over_sea"))) {
			// 全部 国内+国外
			map.remove("over_sea");
		}
		if (("2").equals(map.get("is_sen"))) {
			// 全部 敏感+非敏感
			map.remove("is_sen");
		}
		for (String in : map.keySet()) {
			// map.keySet()返回的是所有key的值
			String str = map.get(in);// 得到每个key多对用value的值
			boolQueryBuilder.must(QueryBuilders.matchQuery(in, str));
		}
		SearchResponse response = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currentTime = dateFormat.format(new java.util.Date()) + " 00:00";
		long time = 0, endtime = 0;
		try {
			time = String2Date.stringToDate2(currentTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SortBuilder<?> sortBuilder = SortBuilders.fieldSort("releaseDate").order(SortOrder.DESC);
		if ("0".equals(sortType)) {
			// 时间降序
			sortBuilder = SortBuilders.fieldSort("releaseDate").order(SortOrder.DESC);
		} else if ("1".equals(sortType)) {
			// 时间升序
			sortBuilder = SortBuilders.fieldSort("releaseDate").order(SortOrder.ASC);
		} else {
			// 入库时间
			sortBuilder = SortBuilders.fieldSort("createDate").order(SortOrder.DESC);
		}

		if ("0".equals(timeType)) {
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder).setFrom(from)
					.addSort(sortBuilder).setSize(10).setExplain(true).execute().actionGet();
		} else if ("1".equals(timeType)) {
			// 今天
			endtime = time + 86400000;
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder)
					.setPostFilter(QueryBuilders.rangeQuery("releaseDate").from(time).to(endtime)).setFrom(from)
					.addSort(sortBuilder).setSize(10).setExplain(true).execute().actionGet();
		} else if ("2".equals(timeType)) {
			// 2天
			endtime = time + 86400000;
			time = time - 86400000;
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder)
					.setPostFilter(QueryBuilders.rangeQuery("releaseDate").from(time).to(endtime)).setFrom(from)
					.addSort(sortBuilder).setSize(10).setExplain(true).execute().actionGet();
		} else if ("3".equals(timeType)) {
			// 3天
			endtime = time + 86400000;
			time = time - 86400000 * 2;
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder)
					.setPostFilter(QueryBuilders.rangeQuery("releaseDate").from(time).to(endtime)).setFrom(from)
					.addSort(sortBuilder).setSize(10).setExplain(true).execute().actionGet();
		} else if ("7".equals(timeType)) {
			// 7天
			endtime = time + 86400000;
			time = time - 86400000 * 6;
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder)
					.setPostFilter(QueryBuilders.rangeQuery("releaseDate").from(time).to(endtime)).setFrom(from)
					.addSort(sortBuilder).setSize(10).setExplain(true).execute().actionGet();
		} else if ("10".equals(timeType)) {
			// 10天
			endtime = time + 86400000;
			time = time - 86400000 * 9;
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder)
					.setPostFilter(QueryBuilders.rangeQuery("releaseDate").from(time).to(endtime)).setFrom(from)
					.addSort(sortBuilder).setSize(10).setExplain(true).execute().actionGet();
		}

		return responseToList(client, response);
	}

	/**
	 * 
	 * @Title: getDataByMuchillegibleCount   
	 * @Description:  多条件 模糊查询 总数  
	 * @param: @param type
	 * @param: @param map
	 * @param: @return      
	 * @return: int      
	 * @throws
	 */
	public static int getDataByMuchillegibleCount(String type, Map<String, String> map, String sortType,
			String timeType) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		if (("2").equals(map.get("over_sea"))) {
			// 全部 国内+国外
			map.remove("over_sea");
		}
		if (("2").equals(map.get("is_sen"))) {
			// 全部 敏感+非敏感
			map.remove("is_sen");
		}
		for (String in : map.keySet()) {
			// map.keySet()返回的是所有key的值
			String str = map.get(in);// 得到每个key多对用value的值
			boolQueryBuilder.must(QueryBuilders.matchQuery(in, str));
		}
		SearchResponse response = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currentTime = dateFormat.format(new java.util.Date()) + " 00:00";
		long time = 0, endtime = 0;
		try {
			time = String2Date.stringToDate2(currentTime).getTime();
			endtime = time + 86400000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if ("0".equals(timeType)) {
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder).setFrom(0).setSize(2000)
					.setExplain(true).execute().actionGet();
		} else if ("1".equals(timeType)) {
			// 今天
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder)
					.setPostFilter(QueryBuilders.rangeQuery("releaseDate").from(time).to(endtime)).setFrom(0)
					.setSize(2000).setExplain(true).execute().actionGet();
		} else if ("2".equals(timeType)) {
			// 2天
			endtime = time + 86400000;
			time = time - 86400000 * 1;
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder)
					.setPostFilter(QueryBuilders.rangeQuery("releaseDate").from(time).to(endtime)).setFrom(0)
					.setSize(2000).setExplain(true).execute().actionGet();
		} else if ("3".equals(timeType)) {
			// 3天
			endtime = time + 86400000;
			time = time - 86400000 * 2;
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder)
					.setPostFilter(QueryBuilders.rangeQuery("releaseDate").from(time).to(endtime)).setFrom(0)
					.setSize(2000).setExplain(true).execute().actionGet();
		} else if ("7".equals(timeType)) {
			// 7天
			endtime = time + 86400000;
			time = time - 86400000 * 6;
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder)
					.setPostFilter(QueryBuilders.rangeQuery("releaseDate").from(time).to(endtime)).setFrom(0)
					.setSize(2000).setExplain(true).execute().actionGet();
		} else if ("10".equals(timeType)) {
			// 10天
			endtime = time + 86400000;
			time = time - 86400000 * 9;
			response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder)
					.setPostFilter(QueryBuilders.rangeQuery("releaseDate").from(time).to(endtime)).setFrom(0)
					.setSize(2000).setExplain(true).execute().actionGet();
		}

		return responseToList(client, response).size();
	}

	/**
	 * 将查询后获得的response转成list
	 * 
	 * @param client
	 * @param response
	 * @return
	 */
	public static List<TargetData> responseToList(TransportClient client, SearchResponse response) {
		/*took - Elasticsearch执行此次搜索所用的时间(单位：毫秒)
		timed_out - 告诉我们此次搜索是否超时
		_shards - 告诉我们搜索了多少分片，还有搜索成功和搜索失败的分片数量
		hits - 搜索结果
		hits.total - 符合搜索条件的文档数量
		hits.hits - 实际返回的搜索结果对象数组(默认只返回前10条)
		hits.sort - 返回结果的排序字段值(如果是按score进行排序，则没有)
		hits._score 和 max_score - 目前先忽略这两个字段*/
		//System.err.println("====="+response.toString());
		SearchHits hits = response.getHits();
		List<TargetData> list = new ArrayList<TargetData>();

		for (int i = 0; i < hits.getHits().length; i++) {
			Map<String, Object> map = hits.getAt(i).getSource();
			// System.err.println("i:"+i+"----"+map.toString());
			TargetData target = new TargetData();
			try {
				target.setTitle(map.get("title").toString());
			} catch (NullPointerException e) {
				System.err.println("title is null.");
			}
			try {
				target.setContentText(map.get("contentText").toString());
			} catch (NullPointerException e) {
				System.err.println("contentText is null.");
			}
			try {
				target.setKeyWord(map.get("keyWord").toString());
			} catch (NullPointerException e) {
				System.err.println("keyWord is null.");
			}
			try {
				target.setFromTo(map.get("fromTo").toString());
			} catch (NullPointerException e) {
				System.err.println("fromTo is null.");
			}
			try {
				target.setCreatTime(map.get("creatTime").toString());
			} catch (NullPointerException e) {
				System.err.println("creatTime is null.");
			}
			try {
				target.setReleaseTime(map.get("releaseTime").toString());
			} catch (NullPointerException e) {
				System.err.println("releaseTime is null.");
			}
			try {
				target.setId(map.get("id").toString());
			} catch (NullPointerException e) {
				System.err.println("id is null.");
			}
			try {
				target.setCommentCount(map.get("commentCount").toString());
			} catch (NullPointerException e) {
				System.err.println("commentCount is null.");
			}
			try {
				target.setType(map.get("type").toString());
			} catch (NullPointerException e) {
				System.err.println("type is null.");
			}
			try {
				target.setTask_id(map.get("task_id").toString());
			} catch (NullPointerException e) {
				System.err.println("task_id is null.");
			}
			try {
				target.setState(map.get("state").toString());
			} catch (NullPointerException e) {
				System.err.println("state is null.");
			}
			try {
				target.setOver_sea(map.get("over_sea").toString());
			} catch (NullPointerException e) {
				System.err.println("over_sea is null.");
			}
			try {
				target.setIs_sen(map.get("is_sen").toString());
			} catch (NullPointerException e) {
				System.err.println("is_sen is null.");
			}

			list.add(target);
		}

		return list;
	}

}


```
##4、聚合查询must,not must,should <=> and,not,or
```java
QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(queryBuilder).mustNot(queryBuilder).should(queryBuilder);

QueryBuilder queryBuilder = QueryBuilders.boolQuery().should(boolQueryBuilder1).must(boolQueryBuilder2).mustNot(boolQueryBuilder3);
		response = client.prepareSearch(index).setTypes(type).setQuery(queryBuilder).setFrom(from)
				.addSort(sortBuilder).setSize(10).setExplain(true).execute().actionGet();



```
API demo 地址
https://download.csdn.net/download/u012898121/10802483
#三、名词简介
```doc
index: es里的index相当于一个数据库。 
type: 相当于数据库里的一个表。 
id： 唯一，相当于主键。 
node:节点是es实例，一台机器可以运行多个实例，但是同一台机器上的实例在配置文件中要确保http和tcp端口不同（下面有讲）。 
cluster:代表一个集群，集群中有多个节点，其中有一个会被选为主节点，这个主节点是可以通过选举产生的，主从节点是对于集群内部来说的。 
shards：代表索引分片，es可以把一个完整的索引分成多个分片，这样的好处是可以把一个大的索引拆分成多个，分布到不同的节点上，构成分布式搜索。分片的数量只能在索引创建前指定，并且索引创建后不能更改。 
replicas:代表索引副本，es可以设置多个索引的副本，副本的作用一是提高系统的容错性，当个某个节点某个分片损坏或丢失时可以从副本中恢复。二是提高es的查询效率，es会自动对搜索请求进行负载均衡。

      