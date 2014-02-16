package com.depeng.feature.zk.testcase;

import com.depeng.feature.zk.common.ZkFeatureJobInstanceMock;
import com.depeng.feature.zk.common.ZkFeatureJobMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ZkFeatureJobClientEventNotifyTest {
	
	private static List<ZkFeatureJobInstanceMock> instances = new ArrayList<ZkFeatureJobInstanceMock>();
	
	public static void main(String[] args) throws Exception {
		
		ZkFeatureJobInstanceMock inst1 = new ZkFeatureJobInstanceMock("host-1",9090);
		instances.add(inst1);
		Thread.sleep(100);
		
		ZkFeatureJobInstanceMock inst2 = new ZkFeatureJobInstanceMock("host-2",9090);
		instances.add(inst2);
		Thread.sleep(100);

		ZkFeatureJobInstanceMock inst3 = new ZkFeatureJobInstanceMock("host-3",9090);
		instances.add(inst3);
		Thread.sleep(100);

		ZkFeatureJobInstanceMock inst4 = new ZkFeatureJobInstanceMock("host-4",9090);
		instances.add(inst4);

		inst1.publishCustomizedEvent();
		Thread.sleep(1000);



		Set<ZkFeatureJobMember> members = inst1.getMembers();
		for (ZkFeatureJobMember member : members) {
			System.out.println(member);
		}

		while (true){}
//		System.exit(0);
	}
	
}
