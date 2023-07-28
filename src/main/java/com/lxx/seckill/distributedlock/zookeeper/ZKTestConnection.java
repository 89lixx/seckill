package com.lxx.seckill.distributedlock.zookeeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/21 14:02
 * @Version: 1.0
 */


public class ZKTestConnection {

    public static void main(String[] args) {
        String connectionString = "localhost:2181"; // ZooKeeper连接地址和端口
        int connectionTimeoutMs = 5000; // 连接超时时间

        // 创建CuratorFramework客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectionString,
                connectionTimeoutMs, connectionTimeoutMs, new ExponentialBackoffRetry(1000, 3));

        try {
            // 启动客户端
            client.start();

            // 判断是否成功连接
            if (client.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                System.out.println("Connected to ZooKeeper successfully!");
            } else {
                System.out.println("Failed to connect to ZooKeeper.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭客户端
            client.close();
        }
    }
}
