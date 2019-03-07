

### 什么是MapReduce

MapReduce是hadoop的核心组件之一，hadoop要分布式包括两部分，一是分布式文件系统hdfs,一部是**分布式计算框**，就是mapreduce,缺一不可，也就是说，可以通过mapreduce很容易在hadoop平台上进行分布式的计算编程。

主要用于搜索领域，解决海量数据的计算问题。
MR有两个阶段组成：Map和Reduce，用户只需实现map()和reduce()两个函数，即可实现分布式计算。

MapReduce的主要思想：**自动将一个大的计算（程序）拆分成Map（映射）和Reduce（化简）的方式。**数据被分割后通过Map函数将数据映射成不同的区块，分配给计算集群进行处理，以达到分布运算的效果，再通过Reduce函数将结果进行汇整，从而输出开发者所需要的结果。





map映射reduce规约 









#### mapreduce的怎么个流程步骤

分成input，split，map，shuffle，reduce五个步骤



input：也就是数据存储位置，这里当然是类似于hdfs这样的分布式存储。

split：因为map task只读split，而split基本上和hdfs的基本存储块block同样大小，一个split对应一个map，你可以把它当做map的单位块来理解。

Shuffler就是reduce从map拉取数据的过程。(说的形象一点就是：将数据重新打乱汇聚到不同节点的过程，当然相同key需要汇聚在同一个节点)









### mapreduce示例编写及编码规范



查看了 WordCount 这个 MapReduce 程序的源码编写，可以得出几点结论：
（1） 该程序有一个 main 方法，来启动任务的运行，其中 job 对象就存储了该程序运行的必要 信息，比如指定 Mapper 类和 Reducer 类
job.setMapperClass(TokenizerMapper.class);
job.setReducerClass(IntSumReducer.class);
（2） 该程序中的 TokenizerMapper 类继承了 Mapper 类
（3） 该程序中的 IntSumReducer 类继承了 Reducer 类



总结： MapReduce 程序的业务编码分为两个大部分，一部分配置程序的运行信息，一部分 编写该 MapReduce 程序的业务逻辑，并且业务逻辑的 map 阶段和 reduce 阶段的代码分别继 承 Mapper 类和 Reducer 类





（1） 用户编写的程序分成三个部分： Mapper， Reducer， Driver(提交运行 MR 程序的客户端)
（2） Mapper 的输入数据是 KV 对的形式（ KV 的类型可自定义）
（3） Mapper 的输出数据是 KV 对的形式（ KV 的类型可自定义）
（4） Mapper 中的业务逻辑写在 map()方法中
（5） map()方法（ maptask 进程）对每一个<K,V>调用一次
（6） Reducer 的输入数据类型对应 Mapper 的输出数据类型，也是 KV
（7） Reducer 的业务逻辑写在 reduce()方法中
（8） Reducetask 进程对每一组相同 k 的<k,v>组调用一次 reduce()方法
（9） 用户自定义的 Mapper 和 Reducer 都要继承各自的父类
（10） 整个程序需要一个 Drvier 来进行提交，提交的是一个描述了各种必要信息的 job 对象



















WordCount 统计每一个单词在整个数据集中出现的总次数。

hadoop jar /root/mrinput/wordcount.jar com.zwj.WordCountJobSubmitter