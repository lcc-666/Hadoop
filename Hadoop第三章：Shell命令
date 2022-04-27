# 系列文章目录
[Hadoop第一章：环境搭建](https://blog.csdn.net/weixin_50835854/article/details/124135328)
[Hadoop第二章：集群搭建（上）](https://blog.csdn.net/weixin_50835854/article/details/124152234?spm=1001.2014.3001.5501)
[Hadoop第二章：集群搭建（中）](https://blog.csdn.net/weixin_50835854/article/details/124194723)
[Hadoop第二章：集群搭建（下）](https://blog.csdn.net/weixin_50835854/article/details/124211120)
Hadoop第三章：Shell命令

---

@[TOC](文章目录)

---

# 前言
这次博客主要记录一些在Hadoop集群使用中的一些常用的Shell命令。实验之前先把集群启动一下。你可以使用脚本也可以手动启动一下，操作不演不演示了。
![在这里插入图片描述](https://img-blog.csdnimg.cn/78c0bee4c43e41e1a2faa809b8796d7a.png)


# 一、上传
创建一个测试文件夹，后边会用到。
```bash
hadoop fs -mkdir /sanguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/66f8d49f371047c6ae8b94f1f6fa294c.png)

## 1.-moveFromLocal
本地剪切到HDFS

本地写一个测试文件

```bash
 echo shuguo > shuguo.txt
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/637d01f35157460bbd2ad7596760e79f.png)

```bash
hadoop fs -moveFromLocal ./shuguo.txt /sanguo
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/9c7b8420cf4c4e2a9ffd17bc78ef956a.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/dea86dc2fd0c44439676871ef66ff412.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/fcbba3275c0845ada526ad752bec9e2e.png)
并且当前文件夹的文本文件已经消失了。

## 2.-copyFromLocal
本地拷贝到HDFS


```bash
echo weiguo > weiguo.txt
hadoop fs -copyFromLocal ./weiguo.txt /sanguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/a79cd45d51b14bbeac8839d5c0c9736b.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/26c84a34712848f9b8a86aac30d466a4.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/0a8368a9d39d40ba8462eac864cde8f8.png)
## 3.-put
等同于copyFromLocal，但是put更常用。

```bash
echo wuguo > wuguo.txt
hadoop fs -put ./wuguo.txt /sanguo
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/6c3abdc8993b42169d1b629d040ca3ba.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/5ffbc6f769754425b24df2aac66c5057.png)

## 3.-appendToFile
追加到某个文件后面

```bash
echo liubei > liubei.txt
hadoop fs -appendToFile ./liubei.txt /sanguo/shuguo.txt
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/69122c2fb8d14be08ba403dde711b2d3.png)


# 二、下载

## 1.-copyToLocal

从HDFS拷贝到本地

```bash
hadoop fs -copyToLocal /sanguo/shuguo.txt ./
cat shuguo.txt
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/7a06271b4fc74a119d2d6438e8a82ef0.png)
## 2.-put
和-copyToLocal等价，但是更常用。

```bash
hadoop fs -get /sanguo/shuguo.txt ./shuguo2.txt
cat shuguo2.txt
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/f21b36d6f02e47c5869d7b4a41d5fb06.png)

# 一、HDFS直接操作
这个和Linux的命令大同小异，所以就简单演示几个。
## 1.展示文件夹

```bash
hadoop fs -ls /sanguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/7cc326a6573840a5838c9a33aae761f9.png)

## 2.显示文件内容

```bash
hadoop fs -cat /sanguo/shuguo.txt
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/efdf704aafe04fc8a7b89e92017ded24.png)
## 3.删除文件

```bash
hadoop fs -rm -r /sanguo
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/5d231c4a14004b8997627cebceab2d99.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/db4e354064674e42902010ce3c903392.png)

---

# 总结
今次博客算是一次过渡性质的博客，所以没有什么太多太难的内容，学校刚刚宣布，取消五一假期了，悲催啊。
