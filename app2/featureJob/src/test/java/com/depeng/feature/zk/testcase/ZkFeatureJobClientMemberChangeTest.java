package com.depeng.feature.zk.testcase;

import com.depeng.feature.zk.common.ZkFeatureJobInstanceMock;

import java.util.ArrayList;
import java.util.List;

public class ZkFeatureJobClientMemberChangeTest {
	
	private static List<ZkFeatureJobInstanceMock> instances = new ArrayList<ZkFeatureJobInstanceMock>();
	
	public static void main(String[] args) throws Exception {
		
		ZkFeatureJobInstanceMock inst1 = new ZkFeatureJobInstanceMock("host-1",9090);
		instances.add(inst1);
		inst1.addMemberUpdateListener();
		Thread.sleep(100);
		
		ZkFeatureJobInstanceMock inst2 = new ZkFeatureJobInstanceMock("host-2",9090);
		instances.add(inst2);
		Thread.sleep(100);

		ZkFeatureJobInstanceMock inst3 = new ZkFeatureJobInstanceMock("host-3",9090);
		instances.add(inst3);
		Thread.sleep(100);

		ZkFeatureJobInstanceMock inst4 = new ZkFeatureJobInstanceMock("host-4",9090);
		instances.add(inst4);
		
//		inst3.close();
		
		Thread.sleep(1000);
		
//		inst2.close();
		
		Thread.sleep(1000);

		while (true)   {}
//		System.exit(0);
	}
	
}
