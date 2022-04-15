# 系列文章目录
[Hadoop第一章：环境搭建](https://blog.csdn.net/weixin_50835854/article/details/124135328)
[Hadoop第二章：集群搭建（上）](https://blog.csdn.net/weixin_50835854/article/details/124152234?spm=1001.2014.3001.5501)
Hadoop第二章：集群搭建（中）

---

@[TOC](文章目录)

---

# 前言
这次博客我们来继续进行上一次的集群搭建。
这里先说一下集群的目标配置。
  --     | hadoop102 | hadoop103 | hadoop104
-------- | ----- |-------- | ------------- | -----
HDFS | NameNode，DataNode |DataNode |SecondaryNameNode，DataNode
  YARN|NodeManager  | ResourceManager，NodeManager | NodeManager
需要修改的自定义文件。

core-site.xml、hdfs-site.xml、yarn-site.xml、mapred-site.xml四个配置文件存放在$HADOOP_HOME/etc/hadoop这个路径上，用户可以根据项目需求重新进行修改配置。

以下修改均在Hadoop102进行，完成后分发即可。

---

# 一、自定义文件配置

## 1.core-site.xml

```bash
vim /opt/module/hadoop-3.2.3/etc/hadoop/core-site.xml

#添加以下内容
<configuration>
    <!-- 指定NameNode的地址 -->
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://hadoop102:8020</value>
    </property>

    <!-- 指定hadoop数据的存储目录 -->
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/opt/module/hadoop-3.2.3/data</value>
    </property>

    <!-- 配置HDFS网页登录使用的静态用户为atguigu -->
    <property>
        <name>hadoop.http.staticuser.user</name>
        <value>atguigu</value>
    </property>
</configuration>

```




## 2.hdfs-site.xml

```bash
vim /opt/module/hadoop-3.2.3/etc/hadoop/hdfs-site.xml

<configuration>
	<!-- nn web端访问地址-->
	<property>
        <name>dfs.namenode.http-address</name>
        <value>hadoop102:9870</value>
    </property>
	<!-- 2nn web端访问地址-->
    <property>
        <name>dfs.namenode.secondary.http-address</name>
        <value>hadoop104:9868</value>
    </property>
</configuration>

```

## 3.hdfs-site.xml

```bash
vim /opt/module/hadoop-3.2.3/etc/hadoop/yarn-site.xml

<configuration>
    <!-- 指定MR走shuffle -->
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>

    <!-- 指定ResourceManager的地址-->
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>hadoop103</value>
    </property>

    <!-- 环境变量的继承 -->
    <property>
        <name>yarn.nodemanager.env-whitelist</name>
        <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRED_HOME</value>
    </property>
</configuration>

```

## 4.mapred-site.xml

```bash
vim /opt/module/hadoop-3.2.3/etc/hadoop/mapred-site.xml

<configuration>
        <!-- 指定MapReduce程序运行在Yarn上 -->
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>

```
# 二、分发配置文件
```bash
xsync /opt/module/hadoop-3.2.3/etc/hadoop/
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/cb9a0ab54bd44097b5ab73b3d39ba15b.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
去其他机器看以下，这里就不演示了。
# 三、启动集群

## 1.配置works

```bash
vim /opt/module/hadoop-3.2.3/etc/hadoop/workers
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/56a82edb7d35440a9cfb7db782c98146.png)


再次分发

```bash
xsync /opt/module/hadoop-3.2.3/etc/hadoop/
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/1daf4c372ef54416ae6531796326875a.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
## 2.启动HDFS
注意第一次启动需要格式化节点。
```bash
 hdfs namenode -format
```
基本上没有报错就是成功了。

然后会在根目录里生成一个data文件
![在这里插入图片描述](https://img-blog.csdnimg.cn/d051cb7bd91e46878b0ebadf92842ae3.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
启动hdfs，注意所在目录
```bash
sbin/start-dfs.sh
```
不报错基本就没有问题
![在这里插入图片描述](https://img-blog.csdnimg.cn/17fcccd1e61046b28f601c525fdb600a.png)


## 3.启动YARN
注意：
**在配置了ResourceManager的节点（hadoop103）启动YARN**

```bash
sbin/start-yarn.sh
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/ca49914485cf448cbd371bd5805c19e7.png)
现在 咱们和预期配置对比一下。
![在这里插入图片描述](https://img-blog.csdnimg.cn/572d29960538428f9db2877b59293a82.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/7958653de73240fbb7ca01b38b31864c.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/5c192b2c61e748fcb16959435dbcfd7b.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/be828f0324a84f419e655f6ed4c98b84.png)
没有问题，有不同就要检查自己那里错误了。



## 4.web测试
分别访问以下两个页面，来进行测试。
http://hadoop102:9870
http://hadoop103:8088
![在这里插入图片描述](https://img-blog.csdnimg.cn/5da716531164474796a43df1697b5203.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/a12246ba54ce4621902bb33a37e967bd.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
集群启动成功。

# 四、集群测试

## 1.上传

随便上传个文件。
这里有两个目录第一个是本地要上传的目录，第二个是集群接受的目录。
```bash
hadoop fs -put /opt/software/OpenJDK8U-jdk_x64_linux_openj9_linuxXL_8u282b08_openj9-0.24.0.tar.gz  /
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/e5b77c5f93ed49feb1133132e5b8ab2f.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/bf0c691628794fdf8228e146b528650e.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
其他操作，比如下载删除，大家自行学习。

## 2.分布式计算

现在做一下分布式计算。
先在本地写点数据。

```bash
mkdir wcinput
vim wcinput/word.txt
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/0afcbe1f8d0e4e7f84fbb038845b1373.png)
这是一个词频统计，所以随便写点单词就行。
![在这里插入图片描述](https://img-blog.csdnimg.cn/c7d16220544348db831ee783c5a04c92.png)
然后上传。

```bash
hadoop fs -mkdir /wcinput
hadoop fs -put /opt/module/hadoop-3.2.3/wcinput/word.txt /wcinput
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/9903042b960745eb8cba709fea96aa0e.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/69bca7aa31244052abf2d0c352bc3c55.png)
然后做一个词频统计，这个测试函数Hadoop已经封装好了，直接用就行。

```bash
hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.2.3.jar wordcount /wcinput /wcoutput
```
这个时候YARN会检测到你的任务。
![在这里插入图片描述](https://img-blog.csdnimg.cn/92489050b1a046a492b72c389f274e57.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/a8135da359f740fd8eb453d0c75f3346.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/1ce16042f4ed469784f27234f1c241b0.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/64a75e3a1bbd4542add62e5ffe72b7a8.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
分布式计算完成。

---

# 五、集群关闭
到这一步集群的基本框架就搭建好了，但是还有一些配置的细节，下次再说吧，我们先来来将集群关闭。
开启的时候先HDFS后YARN。
关闭的时候反过来就行。
![在这里插入图片描述](https://img-blog.csdnimg.cn/c91e2822575d4b21abee357a8636a436.png)![在这里插入图片描述](https://img-blog.csdnimg.cn/4818e6d2459e40a488137a5aa629187c.png)
注意出现这种进程关不掉用kill -9 杀死
![在这里插入图片描述](https://img-blog.csdnimg.cn/73015f0ecb67406a9cc0f7e578cbc9c1.png)



# 总结
这次博客就到这里吧，再有一次集群的搭建应该就能完成了。
