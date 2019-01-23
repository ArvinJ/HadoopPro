

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



