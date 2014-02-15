package com.depeng.app2.client;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class App2Client {


    private static String connStr = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        CuratorFramework zkc = CuratorFrameworkFactory.newClient(connStr, retryPolicy);
        zkc.start();

        try {
            zkc.create().withMode(CreateMode.EPHEMERAL).forPath("/javaCreate", new byte[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        zkc.getData().watched().forPath("/javaCreate");


        CuratorListener curatorListener = new CuratorListener() {
            @Override
            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILDREN:
                        System.out.println("Children");
                        break;
                    case CREATE:
                        System.out.println("create");
                        break;
                }
            }
        };

        zkc.getCuratorListenable().addListener(curatorListener);

    }






}
