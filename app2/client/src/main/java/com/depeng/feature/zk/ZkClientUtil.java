package com.depeng.feature.zk;

import com.depeng.feature.zk.config.ConfigService;
import com.google.common.net.HostAndPort;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

/**
 * Created by depeng on 16/2/14.
 */
public class ZkClientUtil {


    public static String getMasterInfo() throws Exception {
        HostAndPort hostAndPort = null;

        ConfigService configService = ConfigService.load();
        String zkConnectionString = configService.get("zookeeper.quorum");
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework zkc = CuratorFrameworkFactory.newClient(zkConnectionString, retryPolicy);
        zkc.start();

        List<String> childList = zkc.getChildren().forPath("/app2/feature_job/master");
        if (childList != null) {
            for (String child : childList) {
                return child;
            }
        }
        return null;
    }


}
