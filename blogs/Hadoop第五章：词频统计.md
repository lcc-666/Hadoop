# 系列文章目录
[Hadoop第一章：环境搭建](https://blog.csdn.net/weixin_50835854/article/details/124135328)

[Hadoop第二章：集群搭建（上）](https://blog.csdn.net/weixin_50835854/article/details/124152234?spm=1001.2014.3001.5501)

[Hadoop第二章：集群搭建（中）](https://blog.csdn.net/weixin_50835854/article/details/124194723)

[Hadoop第二章：集群搭建（下）](https://blog.csdn.net/weixin_50835854/article/details/124211120)

[Hadoop第三章：Shell命令](https://blog.csdn.net/weixin_50835854/article/details/124456642)

[Hadoop第四章：Client客户端](https://blog.csdn.net/weixin_50835854/article/details/124535515) 

[Hadoop第四章：Client客户端2.0](https://blog.csdn.net/weixin_50835854/article/details/124654823)

Hadoop第五章：词频统计

---


@[TOC](文章目录)

---

# 前言
之前由于学校学习压力，已经断更快两个月，从现在起开始恢复，最低一周一篇。
之前我们使用了hadoop自带的hadoop词频统计jar包进行了词频统计，现在我们手写一个，更加深刻的理解一下hadoop。

---

# 一、创建项目
博主用的是最新版的idea，项目创建和之前有一些所以重新演示一下。
![在这里插入图片描述](https://img-blog.csdnimg.cn/50ecff55675e4a1cae30b41f1986de5e.png)
# 二、基本环境搭建
## 1.添加依赖
![在这里插入图片描述](https://img-blog.csdnimg.cn/25677197732742d9a0e49f118e18dd5a.png)

```bash
<dependencies>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>3.3.2</version>
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
![在这里插入图片描述](https://img-blog.csdnimg.cn/cfb113ccdbc9482980be9cc161c51cdf.png)
## 2.创建日志
创建log4j.properties文件
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
![在这里插入图片描述](https://img-blog.csdnimg.cn/b29641bb54a94c52bea1024da2609d69.png)
## 3.创建包和类
![在这里插入图片描述](https://img-blog.csdnimg.cn/5a8e7f72ad3b4b15bc3d500fdab1c992.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/519c31f59da241d9b885725e4defc682.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/f603f6895ba14376ae9547e8d82a1c81.png)
# 三、编写函数
## 1.WordCountMapper
```java
package com.atguigu.mapreduce.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class WordCountMapper extends Mapper<LongWritable,Text,Text, IntWritable> {
	//为了节省空间，将k-v设置到函数外
    private Text outK=new Text();
    private IntWritable outV=new IntWritable(1);


    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
    	//获取一行输入数据
        String line = value.toString();
		//将数据切分
        String[] words = line.split(" ");
		//循环每个单词进行k-v输出
        for (String word : words) {
            outK.set(word);
			//将参数传递到reduce
            context.write(outK,outV);
        }
    }
}
```
## 2.WordCountReducer

```java
package com.atguigu.mapreduce.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class WordCountReducer extends Reducer<Text, IntWritable,Text,IntWritable> {
	//全局变量输出类型
    private IntWritable outV = new IntWritable();
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {		//设立一个计数器
        int sum=0;
        //统计单词出现个数
        for (IntWritable value : values) {
            sum+=value.get();
        }
		//转换结果类型
        outV.set(sum);
		//输出结果
        context.write(key,outV);
    }
}

```
## 3.WordCountDriver

```java
package com.atguigu.mapreduce.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class WordCountDriver {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
    	//1.获取job
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

		//2.设置jar包路径
        job.setJarByClass(WordCountDriver.class);

		//3.关联mapper和reducer
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

		//4.设置map输出kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
		//5.设置最终输出kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
		//6.设置输入路径和输出路径
        FileInputFormat.setInputPaths(job,new Path("D:\\learn\\hadoop\\wordcount\\input"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\learn\\hadoop\\wordcount\\output"));
		//7.提交job
        boolean result = job.waitForCompletion(true);

        System.exit(result?0:1);
    }
}
```
# 四、本地运行
之前已经配置过本地的hadoop环境了，就不再重复了。
![在这里插入图片描述](https://img-blog.csdnimg.cn/9abdec425b064316abeca8fcd330c75b.png)

这代表运行成功

![在这里插入图片描述](https://img-blog.csdnimg.cn/3f91e7b523cc48a49e793b5a638f4f83.png)

到自己设置的输出目录进行查看。

![在这里插入图片描述](https://img-blog.csdnimg.cn/26426b9624f948c380e1bfe758f29bea.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/749fca304eb8432ab5f68ae95f05a9e1.png)

# 五、集群运行
## 1.添加依赖并打包
为了方便讲个文件上传到集群，我们需要打包，所以要先添加打包的依赖。

在pom.xml进行依赖添加

![在这里插入图片描述](https://img-blog.csdnimg.cn/3a80f8a9a64344d086459c6640b49372.png)


```java
<build>
        <plugins>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.6.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```
刚刚的代码我们需要人工设置输入输出路径，在集群中，显然是很不方便的，所以咱们再写一个包，将之前的三个代码一次性复制过来简单修改一下。

![在这里插入图片描述](https://img-blog.csdnimg.cn/27d41888f9a34cff806656679dc6cd2b.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/2d9661a3d4b74e9282a4d41982e27bea.png)

现在，我们进行打包
![在这里插入图片描述](https://img-blog.csdnimg.cn/14009539d0fa4739a5019f186811ad8c.png)

完成后，可在右边查看
![在这里插入图片描述](https://img-blog.csdnimg.cn/7e2f38715b59484e817ae242df32e2f1.png)

可以看到，这里有两个jar包，大小差别很大，说一下有什么区别，第一个jar包里只包含代码需要将需要的hadoop运行环境提前配置好，第二个jar含有需要的依赖，可以直接调用。
![在这里插入图片描述](https://img-blog.csdnimg.cn/b5c2784d51f9484f97d4c9b3b2b22ecc.png)
## 2.启动集群并上传jar包

![在这里插入图片描述](https://img-blog.csdnimg.cn/ea0e2c8cad7f4f0192fa6ab281fedcaa.png)

可以简单修改一下文件名。

![在这里插入图片描述](https://img-blog.csdnimg.cn/99bc1cd90d534cc99bf6d846268e32d7.png)

在集群上传测试数据。
![在这里插入图片描述](https://img-blog.csdnimg.cn/9aa507c16e774f73ab1fa93b16767c52.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/ae2e1418990e48cab82e13166ffa2622.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/92b482e0f5244174b22965c22e835969.png)

集群运行。
hadoop jar 包命 包类 输出路径 输出路径
包类可以从idea中获取
com.atguigu.mapreduce.wordcount2.WordCountDriver

![在这里插入图片描述](https://img-blog.csdnimg.cn/1ba9cd48afe64337a3e1ea88c6a07a95.png)
```java
hadoop jar wc.jar com.atguigu.mapreduce.wordcount2.WordCountDriver /wcinput /wcoutput
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/cf16a8fd05724ba8828e3779201889b6.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/8c5b9a23950b423e94d8722450d938db.png)
查看结果

![在这里插入图片描述](https://img-blog.csdnimg.cn/ec2b60ae5548436696b6344acfc816b1.png)

---

# 总结
到此词频统计的项目基本就结束了，开始把之前欠的 博客补一补。
