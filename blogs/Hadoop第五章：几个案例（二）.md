# 系列文章目录
[Hadoop第一章：环境搭建](https://blog.csdn.net/weixin_50835854/article/details/124135328)

[Hadoop第二章：集群搭建（上）](https://blog.csdn.net/weixin_50835854/article/details/124152234?spm=1001.2014.3001.5501)

[Hadoop第二章：集群搭建（中）](https://blog.csdn.net/weixin_50835854/article/details/124194723)

[Hadoop第二章：集群搭建（下）](https://blog.csdn.net/weixin_50835854/article/details/124211120)

[Hadoop第三章：Shell命令](https://blog.csdn.net/weixin_50835854/article/details/124456642)

[Hadoop第四章：Client客户端](https://blog.csdn.net/weixin_50835854/article/details/124535515) 

[Hadoop第四章：Client客户端2.0](https://blog.csdn.net/weixin_50835854/article/details/124654823)

[Hadoop第五章：词频统计](https://blog.csdn.net/weixin_50835854/article/details/125576503)

[Hadoop第五章：序列化](https://blog.csdn.net/weixin_50835854/article/details/125605565)

[Hadoop第五章：几个案例](https://blog.csdn.net/weixin_50835854/article/details/125674771)

Hadoop第五章：几个案例（二）

---

@[TOC](文章目录)

---

# 前言
这次依旧忽略理论部分继续带来一些案例。

---


# 一、 Combiner合并
这个说一下这个Combiner，他是mapper的最后一步，可以把一部分reduce的压力分散到mapper的各个节点，进而减少需要网络传输的数据。
简单说一个例子。
现在又十万条数据，10mapper个节点，1个reducer节点，一条数据可以算出一个结果，每个节点有一万的任务量，如果仅仅由reducer进行求和，此节点需要接受十万个数据，对网络压力比较大，如果使用Combiner，数据可以在各自的mapper节点先求和，这样mapeer就需要接受10个数据，大大减小了网络压力。
## 1.需求分析
![在这里插入图片描述](https://img-blog.csdnimg.cn/2883bfb436bc4318b12adce5ca8578d7.png)
因为咱们要使用Combiner所以只展示方案一。
## 2.代码编写
新创建一个包，并且从之前写好的词频统计中把之前的代码拷贝过来，并新创建一个类
![在这里插入图片描述](https://img-blog.csdnimg.cn/4cd2b2efca5a41c99804c4fca570d12d.png)
WordCountCombiner.java
```java
package com.atguigu.mapreduce.combiner;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class WordCountCombiner extends Reducer<Text, IntWritable,Text, IntWritable> {
    private IntWritable outV=new IntWritable();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
        int sum=0;
        for (IntWritable value : values) {
            sum+=value.get();
        }
        outV.set(sum);
        context.write(key,outV);
    }
}
```
然后修改一下driver，将其加入进去。

```java
package com.atguigu.mapreduce.combiner;

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
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(WordCountDriver.class);

        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

//        job.setCombinerClass(WordCountCombiner.class);

        FileInputFormat.setInputPaths(job,new Path("D:\\learn\\hadoop\\wordcount\\input"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\learn\\hadoop\\wordcount\\output"));

        boolean result = job.waitForCompletion(true);

        System.exit(result?0:1);
    }
}
```
咱们先不加入运行一次，查看一下数据。

![在这里插入图片描述](https://img-blog.csdnimg.cn/46c622219e5a484ca1828905e2090585.png)
现在把注释去掉在运行一次。
![在这里插入图片描述](https://img-blog.csdnimg.cn/106b8b7c45f146db9c25562c52bf2fa2.png)
可以明显看到map阶段输出的数据从121减少到了88，这样就可以减轻从map到reducer传输数据的传输压力。
再用词频统计为例，因为咱们的Combiner做的事情和reducer是一摸一样的，所以可以直接使用reducer.class代替Combiner.class这种情况适用于Combiner阶段和reducer阶段逻辑代码相同。且不会改变最终结果。
# 二、自定义OutputFormat案例
## 1.需求分析
![在这里插入图片描述](https://img-blog.csdnimg.cn/a3b99bedf8ba4a0fbfafe52fbd3cd2c1.png)

## 2.代码编写
![在这里插入图片描述](https://img-blog.csdnimg.cn/5cb2412b953847fba113279f6fb8208b.png)
新创建一个包，并且创建3个基本文件，以及两个新需要的类。
mapper不需要要和修改

```java
package com.atguigu.mapreduce.outputformat;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class LogMapper extends Mapper<LongWritable, Text,Text, NullWritable>{
    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, NullWritable>.Context context) throws IOException, InterruptedException {
        context.write(value,NullWritable.get());
    }
}

```
reducer
由于在mppper阶段我们用value作为reducer的key进行传输，当key相同时我们可能丢数据，所以要进行一个简单的遍历。
```java
package com.atguigu.mapreduce.outputformat;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class LogReducer extends Reducer<Text, NullWritable,Text,NullWritable> {
    @Override
    protected void reduce(Text key, Iterable<NullWritable> values, Reducer<Text, NullWritable, Text, NullWritable>.Context context) throws IOException, InterruptedException {
        for (NullWritable value : values) {
            context.write(key,NullWritable.get());
        }
    }
}
```
LogOutputFormat.class
在这里我们需要实现一个RecordWriter方法，可以用idea自动补充，RecordWriter需要返回一个RecordWriter对象，所以我们还需要创建一个类来编写。
```java
package com.atguigu.mapreduce.outputformat;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class LogOutputFormat extends FileOutputFormat<Text, NullWritable> {
    @Override
    public RecordWriter<Text, NullWritable> getRecordWriter(TaskAttemptContext job) throws IOException, InterruptedException {
        LogRecordWriter lrw = new LogRecordWriter(job);
        return lrw;
    }
}
```
LogRecordWriter.class

```java
package com.atguigu.mapreduce.outputformat;


import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;

public class LogRecordWriter extends RecordWriter<Text,NullWritable> {
	//创建两个流
    private FSDataOutputStream atguiguOut;
    private FSDataOutputStream otherOut;
	//启动流
    public LogRecordWriter(TaskAttemptContext job) {
        try {
            FileSystem fs = FileSystem.get(job.getConfiguration());

            atguiguOut = fs.create(new Path("D:\\learn\\hadoop\\Log\\output\\atguigu.log"));

            otherOut = fs.create(new Path("D:\\learn\\hadoop\\Log\\output\\other.log"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	//数据处理后使用不同流，写入对应的文件
    @Override
    public void write(Text key, NullWritable nullWritable) throws IOException, InterruptedException {
        String log = key.toString();

        if (log.contains("atguigu")){
            atguiguOut.writeBytes(log+"\n");
        }else {
            otherOut.writeBytes(log+"\n");
        }
    }
	//关闭流
    @Override
    public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        IOUtils.closeStream(atguiguOut);
        IOUtils.closeStream(otherOut);
    }
}
```
LogDriver.class

```java
package com.atguigu.mapreduce.outputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class LogDriver {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(LogDriver.class);
        job.setMapperClass(LogMapper.class);
        job.setReducerClass(LogReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        //设置自定义的outputformat
        job.setOutputFormatClass(LogOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path("D:\\learn\\hadoop\\Log\\input"));
        //虽然我们自定义了outputformat，但是因为我们的outputformat继承自fileoutputformat
        //而fileoutputformat要输出一个_SUCCESS文件，所以在这还得指定一个输出目录
        FileOutputFormat.setOutputPath(job, new Path("D:\\learn\\hadoop\\Log\\logoutput"));

        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }
}
```
运行结果
![在这里插入图片描述](https://img-blog.csdnimg.cn/4d6e458b91a4432083d9e31a2ff06db7.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/851377c9c01842638bd34124859678c5.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/c7426db1d8654f4bb2a7917f5a507379.png)

# 总结
这次写不完了，先就到这里吧。
