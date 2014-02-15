package com.depeng.app2.client;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.io.IOException;

public class Master implements Watcher {

    ZooKeeper zk;
    String hostPort;

    public Master(String hostPort) {
        this.hostPort = hostPort;
    }

    void startZK() throws IOException, KeeperException, InterruptedException {

        zk = new ZooKeeper(hostPort, 1500, this);
        zk.create("/xxjava", "ss".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

    }



    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
        System.out.println(event.getState());
        System.out.println(event.getPath());
        System.out.println(event.getType());
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        Master master = new Master("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183");

        master.startZK();


        Thread.sleep(60000);
    }


}
