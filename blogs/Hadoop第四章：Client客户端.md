# ϵ������Ŀ¼
[Hadoop��һ�£������](https://blog.csdn.net/weixin_50835854/article/details/124135328)
[Hadoop�ڶ��£���Ⱥ����ϣ�](https://blog.csdn.net/weixin_50835854/article/details/124152234?spm=1001.2014.3001.5501)
[Hadoop�ڶ��£���Ⱥ����У�](https://blog.csdn.net/weixin_50835854/article/details/124194723)
[Hadoop�ڶ��£���Ⱥ����£�](https://blog.csdn.net/weixin_50835854/article/details/124211120)
[Hadoop�����£�Shell����](https://blog.csdn.net/weixin_50835854/article/details/124456642)
Hadoop�����£�Client�ͻ���

---

@[TOC](����Ŀ¼)

---

# ǰ��
���ڿ�ʼ����ʹ��java����дһ��Hadoop��Clinet��ΪʲôҪ��ô���أ���˵��һ�£�֮ǰ����ʹ��Hadoop���в���������Ҫ���ӵ�ĳһ���ڵ��ϣ������ɲ����ԱȽϵͣ���������Ҫ����һ���ͻ��ˡ�

---


# һ�����û���
���ڿͻ��˴󲿷ֶ���windows�������������������������ӣ�����windows������Ϊ��Ⱥ��һ���ڵ㣬���Բ���Ҫ����������Hadoop��
## 1. ���׼��

 1. [Windows����](https://gitee.com/fulsun/winutils-1/tree/master)
��Ϊ�ҵļ�Ⱥ�õ���3.2����������Ҳ��3.2���ɣ�С�汾��������
 2. [idea](https://www.jetbrains.com/idea/download/#section=windows)
����ѧ���Ľ��������õ���רҵ�棬������Ҳ���ԡ�
 3. [maven](https://maven.apache.org/download.cgi)
 ��Ҫ�İ�װ����
 
 4. jdk8
 ������ŵ�ַ�ˣ�����Լ��������ҵġ�
## 2.���û�������
����������Ͱ���Ҫ�Ļ���һ�ζ�����
hadoop
![���������ͼƬ����](https://img-blog.csdnimg.cn/dec13ae05972460fb7978f991f915181.png)
jdk8
![���������ͼƬ����](https://img-blog.csdnimg.cn/8aa747e529fb460d9fafa136086f9e8b.png)
maven
![���������ͼƬ����](https://img-blog.csdnimg.cn/91d571e086904574a8198a2d6487c92e.png)
![���������ͼƬ����](https://img-blog.csdnimg.cn/7a32745831984b80911b51f389da72ab.png)
�������
jdk
![���������ͼƬ����](https://img-blog.csdnimg.cn/202d575bdfbc4873a57cd0a74a8dadd3.png)
maven
![���������ͼƬ����](https://img-blog.csdnimg.cn/404c1f563d6746898d8942223049a95d.png)
hadoop
![���������ͼƬ����](https://img-blog.csdnimg.cn/aeaf94f8cc21452da60a2f0b93fac83b.png)
˫������ļ���һ���ڿ��һ������������ɹ���

## 3.maven��Դ
ʹ��mavenʱ������һЩ����������û�Դһ�£��������ػ�ȽϿ죬����ʹ�ð���Դ��
![���������ͼƬ����](https://img-blog.csdnimg.cn/eb55d0ed25494294bfd2f47a7a3bff36.png)
���±���
![���������ͼƬ����](https://img-blog.csdnimg.cn/411e296cc41943328644d4d230c305ce.png)
��mirrors����Ѱ���Դ�����üӽ�ȥ��

�޸�Ĭ�ϲֿ⡣
��סҪ�ȴ�������ļ��С�
![���������ͼƬ����](https://img-blog.csdnimg.cn/242074391b754550a39eb603e68f6f53.png)







# ����������Ŀ
## 1.����maven
![���������ͼƬ����](https://img-blog.csdnimg.cn/facbf81540fa40b593cddcf2f392244d.png)![���������ͼƬ����](https://img-blog.csdnimg.cn/e1419d0600a2466f9ced2822159077b1.png)
ѡ����ʵĴ洢λ�ã���Ϊ���Ѿ��������ˣ����һ���ҾͲ�ִ���ˡ�

## 2.�޸�ideaĬ��Դ
![���������ͼƬ����](https://img-blog.csdnimg.cn/2021101a9ff943ae9581b745bddcc7de.png)
���⼸���ط����ĳ����Ǳ����Զ���ġ�
![���������ͼƬ����](https://img-blog.csdnimg.cn/87f023e484f04684a39ef7444ab3adb3.png)
�޸�pom.xml
���ʱ��һ��ʹ�ã����Ͻǻ���һ��СȦȦ�������������������pom������Ӧ��jar������Ϊ���ǻ���Դ�ˣ����ػ��Ǻܿ�ġ�
![���������ͼƬ����](https://img-blog.csdnimg.cn/6d11306964b34f6da3e8fe306305a8b3.png)
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
## 3.�����־�ļ�����
����Ŀ��src/main/resourcesĿ¼�£��½�һ���ļ�������Ϊ��log4j.properties�����������������
![���������ͼƬ����](https://img-blog.csdnimg.cn/8c25cffdd1bf45389eb1d28d71b0d851.png)

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
## 4.����������
�ⶼ����ͳ����ȥ��ѧJavaȥ��
![���������ͼƬ����](https://img-blog.csdnimg.cn/76d4483d888f4c5380446c00348dc3c1.png)

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
## 5.������Ⱥ���в���
![���������ͼƬ����](https://img-blog.csdnimg.cn/b2a39f3649ab4ce1bf0fd29b071729c7.png)
![���������ͼƬ����](https://img-blog.csdnimg.cn/9981179691e545c0b5b6196dfda47a4c.png)
�����������г���
![���������ͼƬ����](https://img-blog.csdnimg.cn/587a91dea3be401b90e82772abdbf5ac.png)
����ͨ��
![���������ͼƬ����](https://img-blog.csdnimg.cn/ca130eef8c574f6b97813fc7e45ae7dc.png)
���Կ�����Ⱥ�����һ��Ŀ¼



---

# �ܽ�
����ƪ����ϵ����ξͼ�¼������������˻����Ŀ�ܴ��ʣ�µĴ����´μ�����д��