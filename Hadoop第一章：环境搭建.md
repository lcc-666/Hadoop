# 系列文章目录
Hadoop第一章：环境准备

---

@[TOC](文章目录)
---

# 前言
从现在开始记录自己的hadoop学习情况，计划博客一周一次

---



# 一、软件准备

 1. Vmare虚拟机
 2. centos7镜像
 3. Xshell
 4. Xftp
# 二、安装模板机
看我之前的一次博客
[redhat安装](https://blog.csdn.net/weixin_50835854/article/details/119541986?spm=1001.2014.3001.5501)
根据自己的硬件简单修改一下就行。
现在咱们假设安装完成。开启自己的虚拟机配置一下。
 ## 1.修改主机名

```bash
hostnamectl set-hostname hadoop100
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/d08a0a66cb8348b78453f9b0eae912fd.png)
## 2.修改IP

```bash
vim /etc/sysconfig/network-scripts/ifcfg-ens33 
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/80107e0c64654383818a0e834713ec1e.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

进行如下修改

![在这里插入图片描述](https://img-blog.csdnimg.cn/49334e5d26d3426b8e0f3fee8669250f.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
## 3.添加普通用户

```bash
useradd atguigu
passwd atguigu
```
为其添加root权限

```bash
vim /etc/sudoers
```
直接加一行

![在这里插入图片描述](https://img-blog.csdnimg.cn/e5aa2838a3574691a903cd8d812af317.png)
## 4.创建文件目录

```bash
mkdir /opt/module
mkdir /opt/software
chown atguigu:atguigu /opt/module
chown atguigu:atguigu /opt/software
```
以后这个目录用来存放咱们的dadoop文件
## 5.卸载自带的jdk
```bash
rpm -qa | grep -i java | xargs -n1 rpm -e
```
由于博主这里已经安装好了，就不演示卸载了。
## 6.配置网络
![在这里插入图片描述](https://img-blog.csdnimg.cn/f2c0ed54e51649e986ab24f6149419b7.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

由于咱们使用的是静态网址DHCP可以不用设置。

![在这里插入图片描述](https://img-blog.csdnimg.cn/c20b2d0095394786a3283587f713134e.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/1cfe96966a87477aa5525bf01718d9ff.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)


然后修改windows网卡。

![在这里插入图片描述](https://img-blog.csdnimg.cn/3e77c83c2e034452acd792ff8d22de1e.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
注意是Vmare8，别选错了。

![在这里插入图片描述](https://img-blog.csdnimg.cn/c359e3df10514a688da0c87c0fecf9c8.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/7826a25a4c7240afb31e2028fba2f822.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

![在这里插入图片描述](https://img-blog.csdnimg.cn/29aaf538d043444ebc580f6032ae0b90.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

然后重启网卡ping一下百度。

![在这里插入图片描述](https://img-blog.csdnimg.cn/8c790c2dd61547c3a7e6b3628c0ea121.png)
## 7.安装epel-release
```bash
yum install -y epel-release
```
## 8.关闭防火墙
```bash
systemctl stop firewalld
systemctl disable firewalld.service
```
## 9.配置hosts
因为大数据需要多台虚拟机，咱们也不能一直用IP来访问。

```bash
vim /etc/hosts
```
多写几个

![在这里插入图片描述](https://img-blog.csdnimg.cn/cd03428c62fb4c178789692e87d614f4.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

```bash
192.168.10.100 hadoop100
192.168.10.101 hadoop101
192.168.10.102 hadoop102
192.168.10.103 hadoop103
192.168.10.104 hadoop104
192.168.10.105 hadoop105
192.168.10.106 hadoop106
192.168.10.107 hadoop107
192.168.10.108 hadoop108
```
到这里模板虚拟机基本就修改完成了。
reboot重启即可。

# 三、复制虚拟机
注意首先在虚拟机关机的情况下进行复制。
选中虚拟机-右键-管理-克隆
![在这里插入图片描述](https://img-blog.csdnimg.cn/0d590776d16849d7afaf1c886e4b6b19.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/44b3c1904c3d429386410c9bcf88b4f3.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/793e83d699f0423190ecf6ed1e49f1d1.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/7218b57f303f4114846444834ea03dc1.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

自行修改名字和位置

![在这里插入图片描述](https://img-blog.csdnimg.cn/cd449b4aa41a49539efd53daa5fb9074.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

我已经克隆过来，就不再做了
将克隆机的IP和主机名做相应修改就可以了。
我又克隆了hadoop102 103 104三台服务器。暂时是够了，不够再加。

# 四、Xshell链接
修改windows的hosts

![在这里插入图片描述](https://img-blog.csdnimg.cn/2e3c3f57b64249d6b7fd822a8112b882.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/d2aa593f051a4817b06f490d5c53643a.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

在后边追加，然后用物理机ping一下虚拟机。这里以102为例。
![在这里插入图片描述](https://img-blog.csdnimg.cn/58e9365aa8aa4260a7665ac20208b470.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

可以看到用IP和域名都是可以的。确定可以联通后用Xshell链接。

![在这里插入图片描述](https://img-blog.csdnimg.cn/269ab18ed87b47b7b90b61368366829e.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/c72e1909d44c43638211a6e19c982f3d.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)

然后连接。

![在这里插入图片描述](https://img-blog.csdnimg.cn/ff002f854762496e84ab1a8de2d6d7e0.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/460d9dacb78b4db1a30eebcac7f7ee21.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6LaF5ZOlLS0=,size_20,color_FFFFFF,t_70,g_se,x_16)
![在这里插入图片描述](https://img-blog.csdnimg.cn/227aeacca3e74b8f9e1a16a12af27c98.png)

由于咱们以后的实验都是用Xshell连接的，也为了节省硬件资源，咱们把可视化关掉。

```bash
systemctl set-default runlevel3.target 
```

到此基本的环境准备就结束了。

---

# 总结
大数据和hadoop是离不开的，要慢慢学习。
