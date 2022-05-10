package com.atguigu.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class HdfsClient {

    private FileSystem fs;

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

    @After
    public void close() throws IOException {
        //关闭资源
        fs.close();
    }

    //创建一个文件夹
    @Test
    public void testmkdir() throws  IOException {

        fs.mkdirs(new Path("/xiyou/huaguoshan1"));
    }
    //上传文件
    @Test
    public void testPut() throws IOException {
        //参数 1.是否删除源文件 2.是否强制覆盖 3.本地文件路径 4.hadoop文件路径
        fs.copyFromLocalFile(false,true,new Path("D:\\learn\\test.txt"),new Path("/"));
    }
    @Test
    //文件下载
    public void testGet() throws IOException {
        //参数 1.是否删除源文件 2.hadoop路径 3.本地路径 4.是否校验
        fs.copyToLocalFile(false,new Path("/test.txt"),new Path("C:\\Users\\admin\\Desktop"),false);
    }
    @Test
    //文件删除
    public void tessRm() throws IOException {
        //参数 1.删除目录 2.是否递归
        fs.delete(new Path("/test.txt"),false);

        fs.delete(new Path("/wcinput"),true);

    }
    @Test
    //文件移动和更名
    public void testmv() throws IOException {
        //参数 1.源路径 2.目标路径
        fs.rename(new Path("/xiyou/huaguoshan"),new Path("/xiyou/shuiliandong"));
    }
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
}
