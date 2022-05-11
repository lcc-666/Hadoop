# ϵ������Ŀ¼
# ϵ������Ŀ¼
[Hadoop��һ�£������](https://blog.csdn.net/weixin_50835854/article/details/124135328)
[Hadoop�ڶ��£���Ⱥ����ϣ�](https://blog.csdn.net/weixin_50835854/article/details/124152234?spm=1001.2014.3001.5501)
[Hadoop�ڶ��£���Ⱥ����У�](https://blog.csdn.net/weixin_50835854/article/details/124194723)
[Hadoop�ڶ��£���Ⱥ����£�](https://blog.csdn.net/weixin_50835854/article/details/124211120)
[Hadoop�����£�Shell����](https://blog.csdn.net/weixin_50835854/article/details/124456642)
[Hadoop�����£�Client�ͻ���](https://blog.csdn.net/weixin_50835854/article/details/124535515) 
Hadoop�����£�Client�ͻ���2.0

---

@[TOC](����Ŀ¼)

---

# ǰ��
����һ�εĲ����������������Client�Ļ���ģ�壬������ǽ������ơ�

---

`��ʾ�������Ǳ�ƪ�����������ݣ����永���ɹ��ο�`

# һ���򵥷�װ
���Ĵ���

```java
public class HdfsClient {
    //���Ӽ�Ⱥ��nn��ַ

    @Test
    public void testmkdir() throws URISyntaxException, IOException, InterruptedException {
        //���ӵ�ַ
        URI uri=new URI("hdfs://hadoop102:8020");
        //�����ļ�
        Configuration configuration=new Configuration();
        //�û�
        String user="atguigu";
        //��ȡ�ͻ��˶���
        FileSystem fs=FileSystem.get(uri,configuration,user);
        //����һ���ļ���
        fs.mkdirs(new Path("/xiyou/huaguoshan"));
        //�ر���Դ
        fs.close();
    }
}
```
���Կ������ǽ����������е����Ӽ�Ⱥ��ִ������Լ��������ӶϿ�����д����һ�������У�������Բ����ô��������Ӧ�ý���ֱ��װ�����������С�

```java
public class HdfsClient {

    private FileSystem fs;

    @Before
    public void init() throws URISyntaxException, IOException, InterruptedException {
        //���ӵ�ַ
        URI uri=new URI("hdfs://hadoop102:8020");
        //�����ļ�
        Configuration configuration=new Configuration();
        //�û�
        String user="atguigu";
        //��ȡ�ͻ��˶���
        fs = FileSystem.get(uri,configuration,user);
    }

    @After
    public void close() throws IOException {
        //�ر���Դ
        fs.close();
    }

    @Test
    public void testmkdir() throws  IOException {
        //����һ���ļ���
        fs.mkdirs(new Path("/xiyou/huaguoshan1"));
    }
}
```
���������ڴ���һ���ļ���
![���������ͼƬ����](https://img-blog.csdnimg.cn/c1abbefaadea4de296fedaaa8afacddc.png)
��������ֻ��Ҫ������ֽ����޸ļ��ɡ�
# �����ϴ��ļ�
���Ĵ���
![���������ͼƬ����](https://img-blog.csdnimg.cn/2d2b5fafdd55454696d6a6dcfd8a8c69.png)
ֻ����ĳһ��test
```java
    @Test
    public void testPut() throws IOException {
        //���� 1.�Ƿ�ɾ��Դ�ļ� 2.�Ƿ�ǿ�Ƹ��� 3.�����ļ�·�� 4.hadoop�ļ�·��
        fs.copyFromLocalFile(false,false,new Path("D:\\learn\\test.txt"),new Path("/"));
    }
```

## �����������ȼ�

![���������ͼƬ����](https://img-blog.csdnimg.cn/a2c71cab0180485b85698ae6aad0e81d.png)

��������£���������������������Ϊ���Ǽ�Ⱥ�������ڵ㣬��������������������𣬵�Ȼ�ǿ��Եģ����ǿ�������Ŀ�������ļ��ļ������ã�hdfs-site.xml��������������Ի��½������ԾͲ������ˣ�����֮˵�ڶ�����Ŀ��������á�

��resourcesĿ¼�´���һ��hdfs-site.xml�ļ������Ҫ�޸������ļ�����ֱ����hadoop��hdfs-site.xml�ļ������á�
![���������ͼƬ����](https://img-blog.csdnimg.cn/6d793a19e54b4d38ac833d19a6fc65ce.png)

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
![���������ͼƬ����](https://img-blog.csdnimg.cn/08c0150ff6f84f04b9aab01938c17030.png)
�������ǰɸ�����������Ϊ1�������ٴ��ϴ������ע��Ҫ�����ǡ�
![���������ͼƬ����](https://img-blog.csdnimg.cn/073e4f09322a46b6827e22bbe4651130.png)
�����������Ŀ���ϴ����ļ��ĸ�����ͳһ�����ˣ����ǻ��ǲ��������ϣ��ÿһ���ϴ��ĸ����������ҿ��ƣ���������ѽ�޸��������á�
���Ĵ���
```java
    @Before
    public void init() throws URISyntaxException, IOException, InterruptedException {
        //���ӵ�ַ
        URI uri=new URI("hdfs://hadoop102:8020");
        //�����ļ�
        Configuration configuration=new Configuration();
        //���ø�����
        configuration.set("dfs.replication", "2");
        //�û�
        String user="atguigu";
        //��ȡ�ͻ��˶���
        fs = FileSystem.get(uri,configuration,user);
    }
```
![���������ͼƬ����](https://img-blog.csdnimg.cn/316c2f4aa5dc43dd998808f36e5c35e5.png)
���������¼���ȼ�
�����ļ�<��Ŀ����<�������á�

# ���������ļ�
���Ĵ���

```java
@Test
    //�ļ�����
    public void testGet() throws IOException {
        //���� 1.�Ƿ�ɾ��Դ�ļ� 2.hadoop·�� 3.����·�� 4.�Ƿ�У��
        fs.copyToLocalFile(false,new Path("/test.txt"),new Path("C:\\Users\\admin\\Desktop"),false);
    }
```
�ҵ�����·��ѡ��������档
![���������ͼƬ����](https://img-blog.csdnimg.cn/04cc4e3667bc4b89b6af518c13681bd5.png)
����˵һ�����У�飬��hadoop���ʼ۹�ϣһ�£�Ȼ�󱾵��ļ�Ҳ��ϣһ�£����һ������˵��û�в������ݶ�ʧ��

# �ġ�ɾ���ļ�
���Ĵ���

```java
    @Test
    //�ļ�ɾ��
    public void tessRm() throws IOException {
        //���� 1.ɾ��Ŀ¼ 2.�Ƿ�ݹ�
        fs.delete(new Path("/test.txt"),false);

        fs.delete(new Path("/wcinput"),true);

    }
```
![���������ͼƬ����](https://img-blog.csdnimg.cn/68cff1386d2045aaaa49d6da43943566.png)
ע���Ŀ¼���ļ�����ֱ��ɾ�����ǿ�Ŀ¼��Ҫ�ݹ顣
# �塢�������ƶ�
���Ĵ���
```java
    @Test
    //�ļ��ƶ��͸���
    public void testmv() throws IOException {
    	//���� 1.Դ·�� 2.Ŀ��·��
        fs.rename(new Path("/xiyou/huaguoshan"),new Path("/xiyou/shuiliandong"));
    }
```
![���������ͼƬ����](https://img-blog.csdnimg.cn/2b4cc480907a44258e2d846b585a177a.png)
�������ƶ������Ƶ�ֻҪ�޸�·�����ɡ�
## �����Ƿ�Ϊ�ļ�
��Ϣ����

```java
    @Test
    //�ж����ļ��л����ļ�
    public void testFile() throws IOException {
        FileStatus[] listStatus =fs.listStatus(new Path("/"));
        for (FileStatus status : listStatus) {
            if (status.isFile()) {
                System.out.println("�ļ���"+status.getPath().getName());
            }else {
                System.out.println("Ŀ¼��"+status.getPath().getName());
            }
        }
    }
```
![���������ͼƬ����](https://img-blog.csdnimg.cn/f5ecfb009d3a406d984196454c97f55f.png)
## �ߡ��鿴�ļ�����
���Ĵ���

```java
@Test
    //��ȡ�ļ���Ϣ
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

            // ��ȡ����Ϣ
            BlockLocation[] blockLocations = fileStatus.getBlockLocations();
            System.out.println(Arrays.toString(blockLocations));
        }
    }
```
![���������ͼƬ����](https://img-blog.csdnimg.cn/dd1204c01e5f417f832eecd798fab201.png)
���ڼ�Ⱥ�Ǵ��´��һ�Σ����ûɶ����������ϴ����ļ�����һ�¾��С�

---

# �ܽ�
��Java���пͻ��˵Ĵ������͸�һ�����ˡ�
