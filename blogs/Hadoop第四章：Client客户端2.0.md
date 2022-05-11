# 系列文章目录
# 系列文章目录
[Hadoop第一章：环境搭建](https://blog.csdn.net/weixin_50835854/article/details/124135328)

[Hadoop第二章：集群搭建（上）](https://blog.csdn.net/weixin_50835854/article/details/124152234?spm=1001.2014.3001.5501)

[Hadoop第二章：集群搭建（中）](https://blog.csdn.net/weixin_50835854/article/details/124194723)

[Hadoop第二章：集群搭建（下）](https://blog.csdn.net/weixin_50835854/article/details/124211120)

[Hadoop第三章：Shell命令](https://blog.csdn.net/weixin_50835854/article/details/124456642)

[Hadoop第四章：Client客户端](https://blog.csdn.net/weixin_50835854/article/details/124535515) 

Hadoop第四章：Client客户端2.0

---

@[TOC](文章目录)

---

# 前言
在上一次的博客中我们完成了了Client的基础模板，这次我们将其完善。

---

`提示：以下是本篇文章正文内容，下面案例可供参考`

# 一、简单封装
核心代码

```java
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
可以看到我们将整个过程中的连接集群，执行命令，以及最后的连接断开，都写到了一个函数中，这很明显不利用代码的我们应该将其分别封装在三个函数中。

```java
public class HdfsClient {

    private FileSystem fs;

    @Before
    public void init() throws URISyntaxException, IOException, InterruptedException {
        //连接地址
        URI uri=new URI("hdfs://hadoop102:8020");
        //配置文件
        Configuration configuration=new Configuration();
        //用户
        String user="atguigu";
        //获取客户端对象
        fs = FileSystem.get(uri,configuration,user);
    }

    @After
    public void close() throws IOException {
        //关闭资源
        fs.close();
    }

    @Test
    public void testmkdir() throws  IOException {
        //创建一个文件夹
        fs.mkdirs(new Path("/xiyou/huaguoshan1"));
    }
}
```
现在我们在创建一个文件。

![在这里插入图片描述](https://img-blog.csdnimg.cn/c1abbefaadea4de296fedaaa8afacddc.png)

现在我们只需要在命令部分进行修改即可。
# 二、上传文件
核心代码

![在这里插入图片描述](https://img-blog.csdnimg.cn/2d2b5fafdd55454696d6a6dcfd8a8c69.png)

只测试某一个test
```java
    @Test
    public void testPut() throws IOException {
        //参数 1.是否删除源文件 2.是否强制覆盖 3.本地文件路径 4.hadoop文件路径
        fs.copyFromLocalFile(false,false,new Path("D:\\learn\\test.txt"),new Path("/"));
    }
```

## 副本数量优先级

![在这里插入图片描述](https://img-blog.csdnimg.cn/a2c71cab0180485b85698ae6aad0e81d.png)

正常情况下，这里会出现三个副本，因为咱们集群有三个节点，但是这个数量可以设置吗，当然是可以的，我们可以在项目的配置文件文件里设置（hdfs-site.xml），但这样灵活性会下降，所以就不介绍了，咱们之说在独立项目中如何设置。

在resources目录下创建一个hdfs-site.xml文件，如果要修改配置文件，就直接在hadoop的hdfs-site.xml文件中设置。

![在这里插入图片描述](https://img-blog.csdnimg.cn/6d793a19e54b4d38ac833d19a6fc65ce.png)

```java
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>
	<property>
		<name>dfs.replication</name>
         <value>1</value>
	</property>
</configuration>

```
![在这里插入图片描述](https://img-blog.csdnimg.cn/08c0150ff6f84f04b9aab01938c17030.png)

这里我们吧副本数量设置为1，现在再次上传。这次注意要允许覆盖。

![在这里插入图片描述](https://img-blog.csdnimg.cn/073e4f09322a46b6827e22bbe4651130.png)

现在我这个项目里上传的文件的副本数统一更改了，但是还是不够灵活我希望每一次上传的副本数都有我控制，所以我们呀修改连接配置。
核心代码
```java
    @Before
    public void init() throws URISyntaxException, IOException, InterruptedException {
        //连接地址
        URI uri=new URI("hdfs://hadoop102:8020");
        //配置文件
        Configuration configuration=new Configuration();
        //配置副本数
        configuration.set("dfs.replication", "2");
        //用户
        String user="atguigu";
        //获取客户端对象
        fs = FileSystem.get(uri,configuration,user);
    }
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/316c2f4aa5dc43dd998808f36e5c35e5.png)

所以这里记录优先级
配置文件<项目配置<代码配置。

# 三、下载文件
核心代码

```java
@Test
    //文件下载
    public void testGet() throws IOException {
        //参数 1.是否删除源文件 2.hadoop路径 3.本地路径 4.是否校验
        fs.copyToLocalFile(false,new Path("/test.txt"),new Path("C:\\Users\\admin\\Desktop"),false);
    }
```
我的下载路径选择的是桌面。

![在这里插入图片描述](https://img-blog.csdnimg.cn/04cc4e3667bc4b89b6af518c13681bd5.png)

这里说一下这个校验，就hadoop的问价哈希一下，然后本地文件也哈希一下，如果一样，就说明没有产生数据丢失。

# 四、删除文件
核心代码

```java
    @Test
    //文件删除
    public void tessRm() throws IOException {
        //参数 1.删除目录 2.是否递归
        fs.delete(new Path("/test.txt"),false);

        fs.delete(new Path("/wcinput"),true);

    }
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/68cff1386d2045aaaa49d6da43943566.png)

注意空目录和文件可以直接删除，非空目录需要递归。
# 五、更名和移动
核心代码
```java
    @Test
    //文件移动和更名
    public void testmv() throws IOException {
    	//参数 1.源路径 2.目标路径
        fs.rename(new Path("/xiyou/huaguoshan"),new Path("/xiyou/shuiliandong"));
    }
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/2b4cc480907a44258e2d846b585a177a.png)

更名和移动是类似的只要修改路径即可。
## 六、是否为文件
信息代码

```java
    @Test
    //判断是文件夹还是文件
    public void testFile() throws IOException {
        FileStatus[] listStatus =fs.listStatus(new Path("/"));
        for (FileStatus status : listStatus) {
            if (status.isFile()) {
                System.out.println("文件："+status.getPath().getName());
            }else {
                System.out.println("目录："+status.getPath().getName());
            }
        }
    }
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/f5ecfb009d3a406d984196454c97f55f.png)
## 七、查看文件详情
核心代码

```java
@Test
    //获取文件信息
    public void fileDetail() throws IOException {
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);

        while (listFiles.hasNext()) {
            LocatedFileStatus fileStatus = listFiles.next();

            System.out.println("========" + fileStatus.getPath() + "=========");
            System.out.println(fileStatus.getPermission());
            System.out.println(fileStatus.getOwner());
            System.out.println(fileStatus.getGroup());
            System.out.println(fileStatus.getLen());
            System.out.println(fileStatus.getModificationTime());
            System.out.println(fileStatus.getReplication());
            System.out.println(fileStatus.getBlockSize());
            System.out.println(fileStatus.getPath().getName());

            // 获取块信息
            BlockLocation[] blockLocations = fileStatus.getBlockLocations();
            System.out.println(Arrays.toString(blockLocations));
        }
    }
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/dd1204c01e5f417f832eecd798fab201.png)

由于集群是从新搭建了一次，里边没啥东西，随便上传个文件测试一下就行。

---

# 总结
用Java进行客户端的搭建到这里就告一段落了。
