# 系列文章目录
[Hadoop第一章：环境搭建](https://blog.csdn.net/weixin_50835854/article/details/124135328)
Hadoop第二章：集群搭建（上）

---

@[TOC](文章目录)

---

# 前言
上一次完成了hadoop的基本环境准备，这一次将进行集群的搭建，不知道一次能不能写完。
开始前先把三台102-104三台虚拟机都启动。并用Xshell都连接好。

---

# 一、SSH免密登陆
为了方便节点的之间的通信和信息同步，咱们要为节点之间设置免密登陆。
以hadoop102为例开始配置。用atguigu用户登陆并到达如图位置。
![在这里插入图片描述](https://img-blog.csdnimg.cn/87f0f12c7c84425896c743811c6ac1eb.png)

正常情况下，这里是空的，但我已经配置过了，所以我先把文件清空。
![在这里插入图片描述](https://img-blog.csdnimg.cn/9a49e24d80744082ae10b4c42cf667db.png)
其他两台虚拟机也做同样的操作。
现在我们用Hadoop102直接连接103会发下需要密码。
![在这里插入图片描述](https://img-blog.csdnimg.cn/ffb1255956794c349cc671ef7c48c319.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
第一次需要输入yes和密码（不显示），之后用ctrl+d断开。
我们希望以后连接不需要密码，所以需要使用密钥。
密钥的原理我就不说了
## 生成密钥

```bash
ssh-keygen -t rsa
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/aeaf357b11464cb29fa5abb9551c5d94.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
之后会出现这三个文件。
现在将密钥分发包括自己的三台机器上。

```bash
ssh-copy-id hadoop102
ssh-copy-id hadoop103
ssh-copy-id hadoop104
```
第一次连接是需要密码的。
![在这里插入图片描述](https://img-blog.csdnimg.cn/7d9b6a151310461dbc85a2ae386faaad.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

然后在其他两台机器也做同样的操作
等三台虚拟机都相互发送了密钥后，咱们再用ssh连接一下。
![在这里插入图片描述](https://img-blog.csdnimg.cn/98d3614e90694b17ad5cad24c8a8cd19.png)
可以看到这次不需要密码就可以直接连接。
最后建议把hadoop102的root用户的密码也分发一下，就分发一台即可，操作类似就不说了。

# 二、分发脚本
等节点之间的免密登陆完成后，我们需要编写一个分发脚本来快速转发文件，还需要将他配置到atguigu用户的环境变量中。

```bash
cd /home/atguigu
mkdir bin
cd bin
vim xsync
```
然后将如下内容拷贝进去

```bash
#!/bin/bash

#1. 判断参数个数
if [ $# -lt 1 ]
then
    echo Not Enough Arguement!
    exit;
fi

#2. 遍历集群所有机器
for host in hadoop102 hadoop103 hadoop104
do
    echo ====================  $host  ====================
    #3. 遍历所有目录，挨个发送

    for file in $@
    do
        #4. 判断文件是否存在
        if [ -e $file ]
            then
                #5. 获取父目录
                pdir=$(cd -P $(dirname $file); pwd)

                #6. 获取当前文件的名称
                fname=$(basename $file)
                ssh $host "mkdir -p $pdir"
                rsync -av $pdir/$fname $host:$pdir
            else
                echo $file does not exists!
        fi
    done
done

```
然后为其添加执权限。

```bash
chmod +x xsync
```
测试一下

```bash
xsync /home/atguigu/bin
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/fee5b0a13eb1479f8e497f14a16d2e79.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
他会把测试目录里面的文件进行分发。
![在这里插入图片描述](https://img-blog.csdnimg.cn/405a5f73478a47cd9fe919d3bcdb497f.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/b2e35ae7ab8b4586a6bc79ff5ebe1346.png)
另外两台机器也有了。
最后要配置全局环境变量。

```bash
sudo cp xsync /bin/
```

## 三、安装jdk和hadoop
用xftp连接hadoop102，将jdk和hadoop传输到我们之前建立的位置。
![在这里插入图片描述](https://img-blog.csdnimg.cn/0c84f9c5c4a74fc4861ba4efcd7230a7.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
直接拖动就行，这里我使用的是jdk8和hadoop3.2.3，hadoop3基本上都可以了。jdk最好用8，因为官网宣布只能支持到11，所以我就用了8.
![在这里插入图片描述](https://img-blog.csdnimg.cn/801100bdffc840d689571f8e3e103b30.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
到达上传目录然后解压缩
![在这里插入图片描述](https://img-blog.csdnimg.cn/5ea2b5d80cdd4afe8ac51e79416c38f2.png)

```bash
tar -zxvf OpenJDK8U-jdk_x64_linux_openj9_linuxXL_8u282b08_openj9-0.24.0.tar.gz -C /opt/module/
tar -zxvf hadoop-3.2.3.tar.gz -C /opt/module/
```
两个文件有几百兆，大该需要点时间。
![在这里插入图片描述](https://img-blog.csdnimg.cn/8786d26294374350a0c2b269f3c65272.png)
解压目录加就出现的两个文件。
接下来配置环境变量。
一般情况下是在/etc/profile目录进行换进变量配置，但这是一个总配置文件，看过我学rhce的文章都知道，配置文件不建议直接修改总配置文件，但咱们可以看一下。
![在这里插入图片描述](https://img-blog.csdnimg.cn/aac0216e2e50497789660d5f246e94b9.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
大意是，不建议直接修改这个文件除非你知道你在干什么，最好的方法是创建一个.sh的脚本在/etc/profile.d/目录下，来进行自定义的环境修改，这样可以防止你的需求在未来更新中被合并。
现在咱们就在这个文件里创建脚本。
脚本名称随意。
```bash
sudo vim /etc/profile.d/my_env.sh
#JAVA_HOME
export JAVA_HOME=/opt/module/jdk8u282-b08
export PATH=$PATH:$JAVA_HOME/bin

#HADOOP_HOME
export HADOOP_HOME=/opt/module/hadoop-3.2.3
export PATH=$PATH:$HADOOP_HOME/bin
export PATH=$PATH:$HADOOP_HOME/sbin
~                                     
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/f75f53e710f44a9d82e33e14d00f288b.png)
然后更新环境变量测试一下。

```bash
source /etc/profile
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/7cda8490c83d4a138f7ef0189af5b54b.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
最后用分发脚本分发一下。
也可以用xftp上传。
```bash
xsync /etc/profile.d/my_env.sh
xsync /opt/module/
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/6b18f6b005404f2186949ddff180232a.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
在两外两台机器上更新环境变量后测试一下。



---

# 总结
一篇博客估计是写不完了，下次接着写把。