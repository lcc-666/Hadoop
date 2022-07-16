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

[Hadoop第五章：几个案例（二）](https://blog.csdn.net/weixin_50835854/article/details/125801623)

Hadoop第五章：Join/ETL

---

@[TOC](文章目录)

---

# 前言

今天还是继续带来一些案例。

---

# 一、Reduce Join案例
## 1.需求分析
通过将关联条件作为Map输出的key，将两表满足Join条件的数据并携带数据所来源的文件信息，发往同一个ReduceTask，在Reduce中进行数据的串联。

![在这里插入图片描述](https://img-blog.csdnimg.cn/00824d1c212c4cdea26a67ada6ab58fa.png)
## 2.代码编写
新建一个包，并创建需要的类。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2138870b316b4e6fa70ffdbeb8f3a195.png)

TableBean.class
```java
package com.atguigu.mapreduce.reducejoin;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TableBean implements Writable {

    private String id;//订单id
    private String pid;//产品id
    private int amount;//产品数量
    private String pname;//产品名称
    private String flag;//判断是order表还是pd表的标志字段

    public TableBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(pid);
        out.writeInt(amount);
        out.writeUTF(pname);
        out.writeUTF(flag);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readUTF();
        this.pid = in.readUTF();
        this.amount = in.readInt();
        this.pname = in.readUTF();
        this.flag = in.readUTF();
    }

    @Override
    public String toString() {
        return id + "\t" + pname + "\t" + amount;
    }
}

```
TableMapper.class
```java
package com.atguigu.mapreduce.reducejoin;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class TableMapper extends Mapper<LongWritable, Text, Text, TableBean> {

    private String filename;
    private Text outK = new Text();
    private TableBean outV = new TableBean();

    @Override
    protected void setup(Mapper<LongWritable, Text, Text, TableBean>.Context context) throws IOException, InterruptedException {
    	//获取对应文件名称
        FileSplit split = (FileSplit) context.getInputSplit();

        filename = split.getPath().getName();
    }

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, TableBean>.Context context) throws IOException, InterruptedException {
    	//获取文件
        String line = value.toString();
		//判断来源，分别封装
        if (filename.contains("order")) {
            String[] split = line.split("\t");

            outK.set(split[1]);
            outV.setId(split[0]);
            outV.setPid(split[1]);
            outV.setAmount(Integer.parseInt(split[2]));
            outV.setPname("");
            outV.setFlag("order");
        } else {
            String[] split = line.split("\t");

            outK.set(split[0]);
            outV.setId("");
            outV.setPid(split[0]);
            outV.setAmount(0);
            outV.setPname(split[1]);
            outV.setFlag("pd");
        }
        context.write(outK, outV);
    }
}
```
TableReducer.class

```java
package com.atguigu.mapreduce.reducejoin;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class TableReducer extends Reducer<Text,TableBean,TableBean, NullWritable> {
    @Override
    protected void reduce(Text key, Iterable<TableBean> values, Reducer<Text, TableBean, TableBean, NullWritable>.Context context) throws IOException, InterruptedException {
    	//初始化集合
        ArrayList<TableBean> orderBeans = new ArrayList<>();
        TableBean pdBean = new TableBean();
		//逻辑代码
        for (TableBean value : values) {
            if ("order".equals(value.getFlag())){
                TableBean tmptableBean = new TableBean();
                try {
                    BeanUtils.copyProperties(tmptableBean,value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

                orderBeans.add(tmptableBean);
            }else {
                try {
                    BeanUtils.copyProperties(pdBean,value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        for (TableBean orderBean : orderBeans) {
            orderBean.setPname(pdBean.getPname());
            context.write(orderBean,NullWritable.get());
        }
    }
}
```
TableDriver.class

```java
package com.atguigu.mapreduce.reducejoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class TableDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(TableDriver.class);
        job.setMapperClass(TableMapper.class);
        job.setReducerClass(TableReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(TableBean.class);

        job.setOutputKeyClass(TableBean.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.setInputPaths(job, new Path("D:\\learn\\hadoop\\Table\\input"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\learn\\hadoop\\Table\\output"));

        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }
}
```
运行查看结果。

![在这里插入图片描述](https://img-blog.csdnimg.cn/db452d53b16f456b94868044ed593a0d.png)


# 二、Map Join案例
1）使用场景
Map Join适用于一张表十分小、一张表很大的场景。

2）优点
思考：在Reduce端处理过多的表，非常容易产生数据倾斜。怎么办？
在Map端缓存多张表，提前处理业务逻辑，这样增加Map端业务，减少Reduce端数据的压力，尽可能的减少数据倾斜。
## 1.需求分析
![在这里插入图片描述](https://img-blog.csdnimg.cn/e19adc9cf57845eaba3daf24f25b96fb.png)

## 2.代码编写
![在这里插入图片描述](https://img-blog.csdnimg.cn/32c647e216b64b44a622acb7af4485ef.png)

创建一个新的包，创建需要的类。
MapJoinMapper.class

```java
package com.atguigu.mapreduce.mapjoin;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;

public class MapJoinMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
    private HashMap<String, String> pdMap = new HashMap<>();
    private Text outK = new Text();

    @Override
    protected void setup(Mapper<LongWritable, Text, Text, NullWritable>.Context context) throws IOException, InterruptedException {
    	//获取缓存文件并封装
        URI[] cacheFiles = context.getCacheFiles();

        FileSystem fs = FileSystem.get(context.getConfiguration());
        FSDataInputStream fis = fs.open(new Path(cacheFiles[0]));
		//从流中读取数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

        String line;
        while (StringUtils.isNoneEmpty(line = reader.readLine())) {
        	//切割
            String[] fields = line.split("\t");
            //赋值
            pdMap.put(fields[0], fields[1]);
        }
        //关流
        IOUtils.closeStream(reader);
    }

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, NullWritable>.Context context) throws IOException, InterruptedException {
    	//处理order.txt
        String line = value.toString();
        String[] fields = line.split("\t");
		//获取pid
        String pname = pdMap.get(fields[1]);
        //获取订单id和订单数量
        //封装
        outK.set(fields[0] + "\t" + pname + "\t" + fields[2]);
        context.write(outK, NullWritable.get());
    }
}

```
MapJoinDriver.class
```java
package com.atguigu.mapreduce.mapjoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MapJoinDriver {
    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException, IOException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        
        job.setJarByClass(MapJoinDriver.class);
        
        job.setMapperClass(MapJoinMapper.class);
       
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        // 加载缓存数据
        job.addCacheFile(new URI("file:///D:/learn/hadoop/Table/inputpd/pd.txt"));
        // Map端Join的逻辑不需要Reduce阶段，设置reduceTask数量为0
        job.setNumReduceTasks(0);

        
        FileInputFormat.setInputPaths(job, new Path("D:\\learn\\hadoop\\Table\\inputorder"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\learn\\hadoop\\Table\\outputmap"));
        
        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }
}
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/87ff3aed2461486888c2023ce740ca61.png)
# 三、数据清洗（ETL）
## 1.需求分析
![在这里插入图片描述](https://img-blog.csdnimg.cn/59a71b4fcbba406ea4f0be29df1ad41b.png)

web.log

![在这里插入图片描述](https://img-blog.csdnimg.cn/30f001abcdcf4d45adcf69cfd2d96967.png)
## 2.代码分析
WebLogMapper.class
```java
package com.atguigu.mapreduce.etl;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class WebLogMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, NullWritable>.Context context) throws IOException, InterruptedException {
        //获取一行
        String line = value.toString();
		//ETL清洗
        boolean result = parseLog(line, context);

        if (!result) {
            return;
        }
        //写出
        context.write(value, NullWritable.get());
    }

    private boolean parseLog(String line, Mapper<LongWritable, Text, Text, NullWritable>.Context context) {
    	//切割
        String[] fields = line.split(" ");
		//判断长度
        if (fields.length > 11) {
            return true;
        } else {
            return false;
        }
    }
}
```
WebLogDriver.class

```java
package com.atguigu.mapreduce.etl;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WebLogDriver {
    public static void main(String[] args) throws Exception {

// 输入输出路径需要根据自己电脑上实际的输入输出路径设置
        args = new String[] { "D:\\learn\\hadoop\\Log\\inputweb", "D:\\learn\\hadoop\\Log\\outputweb" };

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(WebLogDriver.class);

        job.setMapperClass(WebLogMapper.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        // 设置reducetask个数为0
        job.setNumReduceTasks(0);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }
}
```
运行查看结果
我们先查看一下源文件。
我使用了VScode查看文件行数。

![在这里插入图片描述](https://img-blog.csdnimg.cn/67e95eb2cb64441a9cf0f79b780d815c.png)

然后查看运行后的文件。

![在这里插入图片描述](https://img-blog.csdnimg.cn/7f331e3c85d741169dab9e297064b065.png)

很明显咱们的数据少了几千条。

# 总结
第五章的内容基本就到这里了。
