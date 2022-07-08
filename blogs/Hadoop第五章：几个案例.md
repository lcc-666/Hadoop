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
Hadoop第五章：几个案例


---


@[TOC](文章目录)

---

# 前言

这次博客记录一些hadoop中的简单案例。

---

# 一、Partition分区案例
其实前面还有一大段的理论知识，但是博主能力有限，只能记录一下这些实操案例。
## 1.需求分析
![在这里插入图片描述](https://img-blog.csdnimg.cn/732883dc03754560a203b539eea620cb.png)
## 2.代码编写
简单说一下，这个partitioner分区的实现是在整个流程中的map之后reducer之前。
新建一个包partitioner，并将之前writable的文件整体复制过来。然后新建一个provincepartitioner。
![在这里插入图片描述](https://img-blog.csdnimg.cn/877e4800b7a24c0395e437f649620acd.png)

```java
package com.atguigu.mapreduce.partitioner;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class ProvincePartitioner extends Partitioner<Text, FlowBean> {
    @Override
    public int getPartition(Text text, FlowBean flowBean, int i) {
        String phone = text.toString();

        String prePhone = phone.substring(0, 3);

        int partition;

        if ("136".equals(prePhone)) {
            partition = 0;
        } else if ("137".equals(prePhone)) {
            partition = 1;
        } else if ("138".equals(prePhone)) {
            partition = 2;
        } else if ("139".equals(prePhone)) {
            partition = 3;
        } else {
            partition = 4;
        }
        return partition;
    }
}
```
现在我们通过driver将这个分区加入到hadoop流程中
![在这里插入图片描述](https://img-blog.csdnimg.cn/3f51249d411d44deacba35348fab7f19.png)
在这个位置添加两行代码。

```java
//指定自定义分区器
job.setPartitionerClass(ProvincePartitioner.class);
//同时指定相应数量的ReduceTask
job.setNumReduceTasks(5);
```
然后简单修改一下输出路径就可以了。
## 3.执行结果
![在这里插入图片描述](https://img-blog.csdnimg.cn/2b947cc9a054433d978393e4797b0bba.png)
现在执行结果已经被分成了五个文件。随便看一下。
![在这里插入图片描述](https://img-blog.csdnimg.cn/bfc825be03e04636abde1ea4d04d4b73.png)
0号码文件都是136开头的。
![在这里插入图片描述](https://img-blog.csdnimg.cn/ee712fcdcc7544ce876a95d21c1f33de.png)
1号文件是137的。
剩下的不看了。


# 二、writableComparable排序（全排序）
## 1.需求分析
![在这里插入图片描述](https://img-blog.csdnimg.cn/374de9c1be224aacbd3d621aaf62999c.png)

## 2.代码编写
创建一个writableComparable，并将之前的代码复制过来。
![在这里插入图片描述](https://img-blog.csdnimg.cn/d1ade99eac964d47ba76b2738e5826f6.png)
FlowBean编写
```java
package com.atguigu.mapreduce.writable;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FlowBean implements WritableComparable<FlowBean> {
    private long upFlow;
    private long downFlow;
    private long sumFlow;


    public FlowBean() {
    }

    public long getUpFlow() {
        return upFlow;
    }

    public void setUpFlow(long upFlow) {
        this.upFlow = upFlow;
    }

    public long getDownFlow() {
        return downFlow;
    }

    public void setDownFlow(long downFlow) {
        this.downFlow = downFlow;
    }

    public long getSumFlow() {
        return sumFlow;
    }

    public void setSumFlow(long sumFlow) {
        this.sumFlow = sumFlow;
    }

    public void setSumFlow() {
        this.sumFlow = this.upFlow + this.downFlow;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(upFlow);
        dataOutput.writeLong(downFlow);
        dataOutput.writeLong(sumFlow);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.upFlow = dataInput.readLong();
        this.downFlow = dataInput.readLong();
        this.sumFlow = dataInput.readLong();
    }

    @Override
    public String toString() {
        return upFlow + "\t" + downFlow + "\t" + sumFlow;
    }
	//排序
    @Override
    public int compareTo(FlowBean o) {
        if (this.sumFlow > o.sumFlow) {
            return -1;
        } else if (this.sumFlow < o.sumFlow) {
            return 1;
        } else {
            if (this.upFlow > o.upFlow) {
                return 1;
            } else if (this.upFlow < o.upFlow) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
```
然后简单修改一下driver
![在这里插入图片描述](https://img-blog.csdnimg.cn/ae834988c79d4cf9ba0ae425e83992ae.png)
注意，现在的输入数据，是之前的输出数据。
![在这里插入图片描述](https://img-blog.csdnimg.cn/cdc87b4ed96c475fbeb23f916a9bdca1.png)
可以发现已经按照总流量进行降序排列了。
但是观察结果可以发现最后三行的总流量是相同的，所以现在增加需求，总流量相同时，安装下载流量排序。
继续修改FlowBean

```java
    @Override
    public int compareTo(FlowBean o) {
        if (this.sumFlow > o.sumFlow) {
            return -1;
        } else if (this.sumFlow < o.sumFlow) {
            return 1;
        } else {
            if (this.upFlow > o.upFlow) {
                return 1;
            } else if (this.upFlow < o.upFlow) {
                return -1;
            } else {
                return 0;
            }
        }
    }
```
然后修改输入输出再次运行查看。
![在这里插入图片描述](https://img-blog.csdnimg.cn/322ef148a374475dbe5e5099ce787172.png)
# 三、writableComparable排序（区内排序）
##  1.需求分析
基于前一个需求，增加自定义分区类，分区按照省份手机号设置。
![在这里插入图片描述](https://img-blog.csdnimg.cn/231cfaf366a841dd98e28005d45cdf2b.png)
## 2.代码编写
创建一个新的包，将上一个包中需要的class考过来。然后再新建一个分区器。
![在这里插入图片描述](https://img-blog.csdnimg.cn/169b2c61d2304605a7169d5a613b0d97.png)
编写ProvincePartitioner2
```java
package com.atguigu.mapreduce.partitionerandwritableComparable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class ProvincePartitioner2 extends Partitioner<FlowBean, Text> {
    @Override
    public int getPartition(FlowBean flowBean, Text text, int i) {
        String phone = text.toString();

        String prePhone = phone.substring(0, 3);

        int partition;

        if ("136".equals(prePhone)) {
            partition = 0;
        } else if ("137".equals(prePhone)) {
            partition = 1;
        } else if ("138".equals(prePhone)) {
            partition = 2;
        } else if ("139".equals(prePhone)) {
            partition = 3;
        } else {
            partition = 4;
        }
        return partition;
    }
}
```
在简单修改一下driver的输出，就可以运行查看结果了。
![在这里插入图片描述](https://img-blog.csdnimg.cn/8ad890ae4d7e4f8d9c23d3793b33a685.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/6b788724cb2243c28e435a97a30a0c59.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/18de54c7729c4ef598172de6c8a10687.png)

---

# 总结
这次简单的记录了几个小小的案例，暂时就到这里吧。
