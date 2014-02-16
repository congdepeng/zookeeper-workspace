package com.depeng.feature.zk.common;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.depeng.feature.zk.common.ChildData;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.listen.ListenerContainer;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;



public class EventNodeCache implements Closeable
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final CuratorFramework client;
    private final String path;
    private final boolean dataIsCompressed;
    //private final EnsurePath ensurePath;
    private final AtomicReference<ChildData> data = new AtomicReference<ChildData>(null);
    private final AtomicReference<State> state = new AtomicReference<State>(State.LATENT);
    private final ListenerContainer<NodeCacheListener> listeners = new ListenerContainer<NodeCacheListener>();
    private final AtomicBoolean isConnected = new AtomicBoolean(true);
    private final ConnectionStateListener connectionStateListener = new ConnectionStateListener()
    {
        public void stateChanged(CuratorFramework client, ConnectionState newState)
        {
            if ( (newState == ConnectionState.CONNECTED) || (newState == ConnectionState.RECONNECTED) )
            {
                if ( isConnected.compareAndSet(false, true) )
                {
                    try
                    {
                        reset();
                    }
                    catch ( Exception e )
                    {
                        log.error("Trying to reset after reconnection", e);
                    }
                }
            }
            else
            {
                isConnected.set(false);
            }
        }
    };

    private final CuratorWatcher watcher = new CuratorWatcher()
    {
        public void process(WatchedEvent event) throws Exception
        {
            reset();
        }
    };

    private enum State
    {
        LATENT,
        STARTED,
        CLOSED
    }

    private final BackgroundCallback backgroundCallback = new BackgroundCallback()
    {
        public void processResult(CuratorFramework client, CuratorEvent event) throws Exception
        {
            processBackgroundResult(event);
        }
    };

    /**
     * @param client curztor client
     * @param path the full path to the node to cache
     */
    public EventNodeCache(CuratorFramework client, String path)
    {
        this(client, path, false);
    }

    /**
     * @param client curztor client
     * @param path the full path to the node to cache
     * @param dataIsCompressed if true, data in the path is compressed
     */
    public EventNodeCache(CuratorFramework client, String path, boolean dataIsCompressed)
    {
        this.client = client;
        this.path = path;
        this.dataIsCompressed = dataIsCompressed;
    }

    /**
     * Start the cache. The cache is not started automatically. You must call this method.
     *
     * @throws Exception errors
     */
    public void  start() throws Exception
    {
    	Preconditions.checkState(state.compareAndSet(State.LATENT, State.STARTED), "Cannot be started more than once");

        client.getConnectionStateListenable().addListener(connectionStateListener);

        reset();
    }

    public void close() throws IOException
    {
        if ( state.compareAndSet(State.STARTED, State.CLOSED) )
        {
            listeners.clear();
        }
        client.getConnectionStateListenable().removeListener(connectionStateListener);
    }

    /**
     * Return the cache listenable
     *
     * @return listenable
     */
    public ListenerContainer<NodeCacheListener> getListenable()
    {
        Preconditions.checkState(state.get() != State.CLOSED, "Closed");

        return listeners;
    }

    /**
     * Return the current data. There are no guarantees of accuracy. This is
     * merely the most recent view of the data. If the node does not exist,
     * this returns null
     *
     * @return data or null
     */
    public ChildData getCurrentData()
    {
        return data.get();
    }

    private void reset() throws Exception
    {
        if ( (state.get() == State.STARTED) && isConnected.get() )
        {
            client.checkExists().usingWatcher(watcher).inBackground(backgroundCallback).forPath(path);
        }
    }
    
    /**
     * get the childData of the 
     * @throws Exception
     */
    private void internalRebuild() throws Exception
    {
        try
        {
            Stat    stat = new Stat();
            byte[]  bytes = dataIsCompressed ? client.getData().decompressed().storingStatIn(stat).forPath(path) : client.getData().storingStatIn(stat).forPath(path);
            data.set(new ChildData(path, stat, bytes));
        }
        catch ( KeeperException.NoNodeException e )
        {
            data.set(null);
        }
    }

    private void processBackgroundResult(CuratorEvent event) throws Exception
    {
        switch ( event.getType() )
        {
            case GET_DATA:
            {
                if ( event.getResultCode() == KeeperException.Code.OK.intValue() )
                {
                    ChildData childData = new ChildData(path, event.getStat(), event.getData());
                    setNewData(childData);
                }
                break;
            }

            case EXISTS:
            {
                if ( event.getResultCode() == KeeperException.Code.NONODE.intValue() )
                {
                    setNewData(null);
                }
                else if ( event.getResultCode() == KeeperException.Code.OK.intValue() )
                {
                    if ( dataIsCompressed )
                    {
                        client.getData().decompressed().usingWatcher(watcher).inBackground(backgroundCallback).forPath(path);
                    }
                    else
                    {
                        client.getData().usingWatcher(watcher).inBackground(backgroundCallback).forPath(path);
                    }
                }
                break;
            }
        }
    }

    private void setNewData(ChildData newData) throws InterruptedException
    {
        ChildData  previousData = data.getAndSet(newData);
        if(previousData ==null){
        	return;
        }
        if ( !Objects.equal(previousData, newData) )
        {
            listeners.forEach
            (
                new Function<NodeCacheListener, Void>()
                {
                    public Void apply(NodeCacheListener listener)
                    {
                        try
                        {
                            listener.nodeChanged();
                        }
                        catch ( Exception e )
                        {
                            log.error("Calling listener", e);
                        }
                        return null;
                    }
                }
            );
        }
    }
}