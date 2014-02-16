package com.depeng.feature.job;

import com.depeng.feature.zk.ZkFeatureJobClient;
import com.depeng.feature.zk.common.NotifyEventType;
import com.depeng.feature.zk.common.ZkFeatureJobMember;
import com.depeng.feature.zk.listener.MemberUpdateListener;
import com.depeng.feature.zk.listener.NotifyEventListener;
import com.google.common.net.HostAndPort;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Set;


/**
 * This class is
 *
 */
public class FeatureJob  implements NotifyEventListener {

    private ZkFeatureJobClient client;
    HostAndPort hostAndPort;

    public FeatureJob(String hostName,int port) {
        HostAndPort hostAndPort = HostAndPort.fromParts(hostName, port);
        this.hostAndPort = hostAndPort;
        client = new ZkFeatureJobClient(hostAndPort, null);
        client.addNotifyEventListener(NotifyEventType.CustomizedEvent, this);
    }

    public void addMemberUpdateListener(){
        client.addMemberUpdateListener(new MemberUpdateListener(){

            @Override
            public void handle(PathChildrenCacheEvent event) {
                PathChildrenCacheEvent.Type type = event.getType();
                String path = event.getData().getPath();
                System.out.println(type.name()+" "+path);
                Set<ZkFeatureJobMember> members = client.getMembers();
                int size = members.size();
                System.out.println("get member update event, current total members size is: "+size);
                for(ZkFeatureJobMember mem:members){
                    System.out.println(mem);
                }
            }

        });
    }

    public void publishCustomizedEvent(){
        System.out.printf("publish event on %s: %d\n", this.hostAndPort.getHostText(), hostAndPort.getPort());
        client.publish(NotifyEventType.CustomizedEvent,"CustomizedEvent");
    }


    public boolean isLeader(){
        return client.isLeader();
    }

    public void close() throws IOException {
        try {
            this.client.close();
        } catch (Exception e) {
            System.out.println("Close error");
        }

    }

    @Override
    public void handle(NotifyEventType notifyEventType, String eventData) {
        System.out.println(eventData);
        System.out.printf("This Server %s:%d get "+notifyEventType.getName()+" event and can process at this handle method\n",this.hostAndPort.getHostText(),hostAndPort.getPort());
    }

    public Set<ZkFeatureJobMember> getMembers() {
        return client.getMembers();
    }
}
