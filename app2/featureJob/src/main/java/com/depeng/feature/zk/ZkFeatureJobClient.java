package com.depeng.feature.zk;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.depeng.feature.zk.common.*;
import com.google.common.net.HostAndPort;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.listen.ListenerContainer;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.depeng.feature.zk.config.ConfigService;
import com.depeng.feature.zk.listener.MemberUpdateListener;
import com.depeng.feature.zk.listener.NotifyEventListener;


/**
 * This class represent as a ZooKeeper Client of a Feature Job Client
 * <p/>
 * extends LeaderSelectorListenerAdapter to vote a Leader
 * implements PathChildrenCacheListener to handle children change event
 * implements Closeable to close client connection
 */
public class ZkFeatureJobClient extends LeaderSelectorListenerAdapter implements PathChildrenCacheListener, Closeable {

    private static Logger log = LoggerFactory.getLogger(ZkFeatureJobClient.class);

    //use curator
    private final CuratorFramework zkc;
    //
    private final Multimap<Path, EventNodeCache> nodeCacheMap = ArrayListMultimap.create();
    // client default not Leader(master)
    private boolean isLeader = false;
    private LeaderSelector election = null;

    private Set<PathChildrenCache> pathChildrenCaches = new HashSet<PathChildrenCache>();
    private ListenerContainer<MemberUpdateListener> memberUpdateListenerListenerContainer = new ListenerContainer<>();

    private Set<ZkFeatureJobMember> members = new HashSet<ZkFeatureJobMember>();

    //java.util.concurrent.ExecutorService
    private final ExecutorService executor;

    //Client register host port into ZK
    private final HostAndPort hostAndPort;


    public ZkFeatureJobClient(HostAndPort hostAndPort, String zkConnectionString) {
        this(true, hostAndPort, zkConnectionString);
    }


    // construction of Zk FJ client
    ZkFeatureJobClient(boolean leaderElection, HostAndPort hostAndPort, String zkConnectionString) {
        this.hostAndPort = hostAndPort;
        executor = Executors.newCachedThreadPool();
        ConfigService configService = ConfigService.load();
        if (zkConnectionString == null) {
            zkConnectionString = configService.get("zookeeper.quorum");
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkc = CuratorFrameworkFactory.newClient(zkConnectionString, retryPolicy);
        zkc.start();
        if (leaderElection) {
            // when every client create, start to elect leader
            this.startLeaderElection();
        }

        // create a EPHEMERAL mapping to this client
        this.registerAsMember();
		this.startMemberChangeListener();
    }

    /**
     * Register my self
     */
    private void registerAsMember() {
        try {
            Path member = this.getMemberPath();
            String hostName = hostAndPort.getHostText();
            int port = hostAndPort.getPort();
            String nodeName = hostName + ":" + port;
            Path path = member.joinPath(nodeName);

            String parentPath = path.getParent().toString();
            Stat state = zkc.checkExists().forPath(parentPath);
            if (state == null) {
                zkc.create().forPath(parentPath);
            }
            state = zkc.checkExists().forPath(path.toString());
            if (state == null) {
                zkc.create().withMode(CreateMode.EPHEMERAL).forPath(path.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void registerAsLeader() {
        try {
            Path member = this.getMemberPath();
            String hostName = hostAndPort.getHostText();
            int port = hostAndPort.getPort();
            String nodeName = hostName + ":" + port;
            Path path = member.joinPath(nodeName);

            String parentPath = path.getParent().toString();
            Stat state = zkc.checkExists().forPath(parentPath);
            if (state == null) {
                zkc.create().forPath(parentPath);
            }
            state = zkc.checkExists().forPath(path.toString());
            if (state == null) {
                zkc.create().withMode(CreateMode.EPHEMERAL).forPath(path.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startLeaderElection() {
        election = new LeaderSelector(zkc, getLeaderPath().toString(), executor, this);
        election.autoRequeue();
        election.start();
    }


    /**
     * Member change listener
     *
     * 1. get All Child List
     *
     *
     */
    private void startMemberChangeListener() {
        try {
            Path memberPath = getMemberPath();
            String memberPathStr = memberPath.toString();
            List<String> childList = zkc.getChildren().forPath(memberPathStr);
            if (childList != null) {
                for (String child : childList) {
                    ZkFeatureJobMember instance = new ZkFeatureJobMember(child);
                    members.add(instance);
                }
            }
            PathChildrenCache memberPathChildrenCache = new PathChildrenCache(zkc, memberPathStr, true);
            memberPathChildrenCache.start();
            memberPathChildrenCache.getListenable().addListener(this);
            pathChildrenCaches.add(memberPathChildrenCache);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<ZkFeatureJobMember> getMembers() {
        return members;
    }
    public ZkFeatureJobMember getLeader() {


        // TODO: get leader
        return null;
    }




    public void addMemberUpdateListener(MemberUpdateListener listener) {
        if (listener != null) memberUpdateListenerListenerContainer.addListener(listener, executor);
    }


    public void publish(NotifyEventType notifyEventType, String eventData) {
        try {
            String path = getNotifyEventPath(notifyEventType).toString();
            Stat state = zkc.checkExists().forPath(path);
            if (state == null) {
                zkc.create().creatingParentsIfNeeded().forPath(path);
            } else {
                zkc.setData().forPath(path, eventData.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addNotifyEventListener(final NotifyEventType notifyEventType, final NotifyEventListener listener) {
        try {
            final Path path = getNotifyEventPath(notifyEventType);
            EventNodeCache node = new EventNodeCache(zkc, path.toString());
            node.getListenable().addListener(new NodeCacheListener() {
                public void nodeChanged() throws Exception {
                    String eventData = new String(zkc.getData().forPath(path.toString()));
                    listener.handle(notifyEventType, eventData);
                }
            });
            nodeCacheMap.put(path, node);
            node.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Path getNotifyEventPath(NotifyEventType notifyEventType) {
        return this.getFeatureJobPath().joinPath(Constant.PATH_EVENT).joinPath(notifyEventType.getName());
    }

    private Path getLeaderPath() {
        return this.getFeatureJobPath().joinPath(Constant.PATH_LEADER);
    }

    private Path getMemberPath() {
        return this.getFeatureJobPath().joinPath(Constant.PATH_MEMBER);
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void takeLeadership(CuratorFramework client) throws Exception {
        log.info("this service been elected as master");
        System.out.println(hostAndPort.getHostText() + ":" + hostAndPort.getPort() + " is now the leader.");
        isLeader = true;
        synchronized (election) {
            election.wait();
        }
    }

    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        switch (event.getType()) {
            case CHILD_ADDED: {
                String path = event.getData().getPath();
                if (members.add(new ZkFeatureJobMember(path))) {
                    applyMemberChangeListener(event);
                }
                break;
            }

            case CHILD_UPDATED: {
                break;
            }

            case CHILD_REMOVED: {
                String path = event.getData().getPath();
                if (members.remove(new ZkFeatureJobMember(path))) {
                    applyMemberChangeListener(event);
                }
                break;
            }
        }
    }

    private void applyMemberChangeListener(final PathChildrenCacheEvent event) {
        this.memberUpdateListenerListenerContainer.forEach(
                new Function<MemberUpdateListener, Void>() {
                    public Void apply(MemberUpdateListener listener) {
                        try {
                            listener.handle(event);
                        } catch (Exception e) {
                            log.error("Calling listener", e);
                        }
                        return null;
                    }
                }
        );
    }

    public void close() throws IOException {
        if (election != null) {
            election.close();
            synchronized (election) {
                election.notify();
            }
        }
        for (PathChildrenCache cache : pathChildrenCaches) {
            cache.close();
        }
        for (EventNodeCache node : nodeCacheMap.values()) {
            node.close();

        }
        this.zkc.close();
    }

    public Path getFeatureJobPath() {

        return new Path(Constant.PATH_FEATURE_JOB);

    }
}
