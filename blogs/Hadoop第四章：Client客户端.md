# 系列文章目录
[Hadoop第一章：环境搭建](https://blog.csdn.net/weixin_50835854/article/details/124135328)
[Hadoop第二章：集群搭建（上）](https://blog.csdn.net/weixin_50835854/article/details/124152234?spm=1001.2014.3001.5501)
[Hadoop第二章：集群搭建（中）](https://blog.csdn.net/weixin_50835854/article/details/124194723)
[Hadoop第二章：集群搭建（下）](https://blog.csdn.net/weixin_50835854/article/details/124211120)
[Hadoop第三章：Shell命令](https://blog.csdn.net/weixin_50835854/article/details/124456642)
Hadoop第四章：Client客户端

---

@[TOC](文章目录)

---

# 前言
现在开始我们使用java来编写一个Hadoop的Clinet，为什么要这么做呢，简单说明一下，之前我们使用Hadoop进行操作，都需要连接到某一个节点上，这样可操作性比较低，所以咱们要配置一个客户端。

---


# 一、配置环境
由于客户端大部分都在windows，所以咱们配置依赖进行连接，但是windows本身不作为集群的一个节点，所以不需要配置完整的Hadoop。
## 1. 软件准备

 1. [Windows依赖](https://gitee.com/fulsun/winutils-1/tree/master)
因为我的集群用的是3.2，所以依赖也用3.2即可，小版本不用在意
 2. [idea](https://www.jetbrains.com/idea/download/#section=windows)
我有学生的教育申请用的是专业版，社区版也可以。
 3. [maven](https://maven.apache.org/download.cgi)
 必要的安装依赖
 
 4. jdk8
 这个不放地址了，大家自己很容易找的。
## 2.配置环境变量
咱们在这里就把需要的环境一次都配了
hadoop
![在这里插入图片描述](https://img-blog.csdnimg.cn/dec13ae05972460fb7978f991f915181.png)
jdk8
![在这里插入图片描述](https://img-blog.csdnimg.cn/8aa747e529fb460d9fafa136086f9e8b.png)
maven
![在这里插入图片描述](https://img-blog.csdnimg.cn/91d571e086904574a8198a2d6487c92e.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/7a32745831984b80911b51f389da72ab.png)
检查配置
jdk
![在这里插入图片描述](https://img-blog.csdnimg.cn/202d575bdfbc4873a57cd0a74a8dadd3.png)
maven
![在这里插入图片描述](https://img-blog.csdnimg.cn/404c1f563d6746898d8942223049a95d.png)
hadoop
![在这里插入图片描述](https://img-blog.csdnimg.cn/aeaf94f8cc21452da60a2f0b93fac83b.png)
双击这个文件有一个黑框框一闪而过，代表成功。

## 3.maven换源
使用maven时会下载一些依赖包，最好换源一下，这样下载会比较快，咱们使用阿里源。
![在这里插入图片描述](https://img-blog.csdnimg.cn/eb55d0ed25494294bfd2f47a7a3bff36.png)
记事本打开
![在这里插入图片描述](https://img-blog.csdnimg.cn/411e296cc41943328644d4d230c305ce.png)
在mirrors里面把阿里源的配置加进去。

修改默认仓库。
记住要先创建这个文件夹。
![在这里插入图片描述](https://img-blog.csdnimg.cn/242074391b754550a39eb603e68f6f53.png)







# 二、创建项目
## 1.创建maven
![在这里插入图片描述](https://img-blog.csdnimg.cn/facbf81540fa40b593cddcf2f392244d.png)![在这里插入图片描述](https://img-blog.csdnimg.cn/e1419d0600a2466f9ced2822159077b1.png)
选择合适的存储位置，因为我已经创建过了，最后一步我就不执行了。

## 2.修改idea默认源
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021101a9ff943ae9581b745bddcc7de.png)
把这几个地方都改成咱们本地自定义的。
![在这里插入图片描述](https://img-blog.csdnimg.cn/87f023e484f04684a39ef7444ab3adb3.png)
修改pom.xml
如果时第一次使用，右上角会有一个小圈圈，点击它，他会根据你的pom下载相应的jar包，因为咱们换过源了，下载还是很快的。
![在这里插入图片描述](https://img-blog.csdnimg.cn/6d11306964b34f6da3e8fe306305a8b3.png)
```bash
<dependencies>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>3.1.2</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.36</version>
        </dependency>
    </dependencies>
```
## 3.添加日志文件配置
在项目的src/main/resources目录下，新建一个文件，命名为“log4j.properties“，并添加以下内容
![在这里插入图片描述](https://img-blog.csdnimg.cn/8c25cffdd1bf45389eb1d28d71b0d851.png)

```bash
log4j.rootLogger=INFO, stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d %p [%c] - %m%n
log4j.appender.logfile=org.apache.log4j.FileAppender
log4j.appender.logfile.File=target/spring.log
log4j.appender.logfile.layout=org.apache.log4j.PatternLayout
log4j.appender.logfile.layout.ConversionPattern=%d %p [%c] - %m%n
```
## 4.创建包和类
这都不会就趁早回去重学Java去。
![在这里插入图片描述](https://img-blog.csdnimg.cn/76d4483d888f4c5380446c00348dc3c1.png)

```bash
package com.atguigu.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HdfsClient {
    //连接集群的nn地址

    @Test
    public void testmkdir() throws URISyntaxException, IOException, InterruptedException {
        //连接地址
        URI uri=new URI("hdfs://hadoop102:8020");
        //配置文件
        Configuration configuration=new Configuration();
        //用户
        String user="atguigu";
        //获取客户端对象
        FileSystem fs=FileSystem.get(uri,configuration,user);
        //创建一个文件夹
        fs.mkdirs(new Path("/xiyou/huaguoshan"));
        //关闭资源
        fs.close();
    }
}
```
## 5.启动集群进行测试
![在这里插入图片描述](https://img-blog.csdnimg.cn/b2a39f3649ab4ce1bf0fd29b071729c7.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/9981179691e545c0b5b6196dfda47a4c.png)
现在我们运行程序。
![在这里插入图片描述](https://img-blog.csdnimg.cn/587a91dea3be401b90e82772abdbf5ac.png)
运行通过
![在这里插入图片描述](https://img-blog.csdnimg.cn/ca130eef8c574f6b97813fc7e45ae7dc.png)
可以看到集群里多了一个目录



---

# 总结
由于篇幅关系，这次就记录到这里，这次完成了基本的框架搭建，剩下的代码下次继续编写。