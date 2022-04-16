# 系列文章目录
[Hadoop第一章：环境搭建](https://blog.csdn.net/weixin_50835854/article/details/124135328)

[Hadoop第二章：集群搭建（上）](https://blog.csdn.net/weixin_50835854/article/details/124152234?spm=1001.2014.3001.5501)

[Hadoop第二章：集群搭建（中）](https://blog.csdn.net/weixin_50835854/article/details/124194723)

Hadoop第二章：集群搭建（下）


---

@[TOC](文章目录)

---

# 前言
这次应该就是Hadoop集群搭建的最后一次了。最后完成一些群集搭建的后续任务。

---


# 一、配置历史服务器
上一次我们完成了一个简单的分布式计算，现在我们想查看历史信息。

![在这里插入图片描述](https://img-blog.csdnimg.cn/c2c620b3bb0e49beb817e01077e597f5.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

这时就会报错，就是因为我们没有配置历史服务器。

![在这里插入图片描述](https://img-blog.csdnimg.cn/69f1d66ec296471983e971090a3f7630.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

## 1.配置文件

```bash
vim /opt/module/hadoop-3.2.3/etc/hadoop/mapred-site.xml 

<!-- 开启日志聚集功能 -->
<property>
    <name>yarn.log-aggregation-enable</name>
    <value>true</value>
</property>
<!-- 设置日志聚集服务器地址 -->
<property>  
    <name>yarn.log.server.url</name>  
    <value>http://hadoop102:19888/jobhistory/logs</value>
</property>
<!-- 设置日志保留时间为7天 -->
<property>
    <name>yarn.log-aggregation.retain-seconds</name>
    <value>604800</value>
</property>
```
继续追加

![在这里插入图片描述](https://img-blog.csdnimg.cn/2c13328c44c94a6bbfb23172d9b833be.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
## 2.分发脚本

```bash
xsync /opt/module/hadoop-3.2.3/etc/hadoop/
```
然后重启集群服务，这个不演示了。
## 3.启动历史服务器
历史服务器是配置在Hadoop102上，所以102启动历史服务。
```bash
mapred --daemon start historyserver
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/65a379d6c03f418f89ac73906f105164.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

从新进行云计算
计算之前要把之前的结果删掉，不然会报错。

```bash
hadoop fs -rm -r /wcoutput
hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.2.3.jar wordcount /wcinput /wcoutput
```
然后web访问历史记录。
http://hadoop102:19888/jobhistory/

![在这里插入图片描述](https://img-blog.csdnimg.cn/4e960afe09f640f6b45fafb2b3f8bdac.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

因为我进行了多次实验，可能记录比较多。


# 二、常用脚本
## 1.快速启动/关闭
当服务器很多的我们无法一个一个机器的启动，所以要使用shell脚本。

```bash
cd /home/atguigu/bin/
vim myhadoop.sh

#!/bin/bash

if [ $# -lt 1 ]
then
    echo "No Args Input..."
    exit ;
fi

case $1 in
"start")
        echo " =================== 启动 hadoop集群 ==================="

        echo " --------------- 启动 hdfs ---------------"
        ssh hadoop102 "/opt/module/hadoop-3.2.3/sbin/start-dfs.sh"
        echo " --------------- 启动 yarn ---------------"
        ssh hadoop103 "/opt/module/hadoop-3.2.3/sbin/start-yarn.sh"
        echo " --------------- 启动 historyserver ---------------"
        ssh hadoop102 "/opt/module/hadoop-3.2.3/bin/mapred --daemon start historyserver"
;;
"stop")
        echo " =================== 关闭 hadoop集群 ==================="

        echo " --------------- 关闭 historyserver ---------------"
        ssh hadoop102 "/opt/module/hadoop-3.2.3/bin/mapred --daemon stop historyserver"
        echo " --------------- 关闭 yarn ---------------"
        ssh hadoop103 "/opt/module/hadoop-3.2.3/sbin/stop-yarn.sh"
        echo " --------------- 关闭 hdfs ---------------"
        ssh hadoop102 "/opt/module/hadoop-3.2.3/sbin/stop-dfs.sh"
;;
*)
    echo "Input Args Error..."
;;
esac

#最后给权限
chmod +x myhadoop.sh
```

## 2.查看状态

```bash
vim jpsall

#!/bin/bash

for host in hadoop102 hadoop103 hadoop104
do
        echo =============== $host ===============
        ssh $host jps
done

```
## 3.分发使用

```bash
myhadoop.sh stop
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/fc2c931272c740e3b85273ec9b518359.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

```bash
jpsall
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/3c200bcf9e11427482b90a6330cd2de0.png)

咱们在启动一下。

```bash
myhadoop.sh start
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/b8a9a83163ea4fc5815adc6f96cc23b3.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/30cbed7d39914d53b5fe20519703f3c2.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_18,color_FFFFFF,t_70,g_se,x_16)

# 总结
Hadoop的集群搭建，算是结束了，hadoop学习才仅仅是刚刚开始。任重而道远啊。
