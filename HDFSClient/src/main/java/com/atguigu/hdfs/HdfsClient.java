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
