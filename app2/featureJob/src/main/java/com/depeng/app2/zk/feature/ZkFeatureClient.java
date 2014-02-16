package com.depeng.app2.zk.feature;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by depeng on 15/2/14.
 */
public class ZkFeatureClient  extends LeaderSelectorListenerAdapter implements PathChildrenCacheListener,Closeable {


    private final CuratorFramework cclient;
    /**
     * whether is master, default is false
     */
    private boolean isMaster = false;

    private LeaderSelector election = null;
    private Set<PathChildrenCache> memberpathcaches = new HashSet<PathChildrenCache>();

    public ZkFeatureClient() {
    }


    @Override
    public void close() throws IOException {
        if(election!=null){
            election.close();
            synchronized(election){
                election.notify();
            }
        }
        for(PathChildrenCache cache:memberpathcaches){
            cache.close();
        }
        for(EventNodeCache node:nodeCacheMap.values()){
            node.close();
        }
        this.cclient.close();
    }

    @Override
    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        log.info("this service been elected as master");
        System.out.println(context.getHostName() + ":" + context.getPort() + " is now the leader.");
        isMaster = true;
        synchronized(election)
        {
            election.wait();
        }

    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
        switch ( event.getType() )
        {
            case CHILD_ADDED:
            {
                String path = event.getData().getPath();
                if(members.add(new ServiceInstance(path))){
                    applyMemberChangeListener(event);
                }
                break;
            }

            case CHILD_UPDATED:
            {
                break;
            }

            case CHILD_REMOVED:
            {
                String path = event.getData().getPath();
                if(members.remove(new ServiceInstance(path))){
                    applyMemberChangeListener(event);
                }
                break;
            }
        }
    }
}
