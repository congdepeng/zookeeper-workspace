package com.depeng.feature.zk.testcase;

import com.depeng.feature.zk.common.ZkFeatureJobInstanceMock;

import java.util.ArrayList;
import java.util.List;

public class ZkFeatureJobClientTest {
	
	//private static CountDownLatch latch = new CountDownLatch(3);

	private static List<ZkFeatureJobInstanceMock> instances = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		
		ZkFeatureJobInstanceMock inst1 = new ZkFeatureJobInstanceMock("feature_job_ip-1",9090);
		instances.add(inst1);
		Thread.sleep(100);
		
		ZkFeatureJobInstanceMock inst2 = new ZkFeatureJobInstanceMock("feature_job_ip-2",9090);
		instances.add(inst2);
		Thread.sleep(100);

		ZkFeatureJobInstanceMock inst3 = new ZkFeatureJobInstanceMock("feature_job_ip-3",9090);
		instances.add(inst3);
		Thread.sleep(100);

		ZkFeatureJobInstanceMock inst4 = new ZkFeatureJobInstanceMock("feature_job_ip-4",9090);
		instances.add(inst4);
		
		Thread.sleep(2000);
		System.out.println("feature_job_ip-1 is leader:"+inst1.isMaster());
		System.out.println("feature_job_ip-2 is leader:"+inst2.isMaster());
		System.out.println("feature_job_ip-3 is leader:"+inst3.isMaster());
		System.out.println("feature_job_ip-4 is leader:"+inst4.isMaster());
        Thread.sleep(2000);
        Thread.sleep(2000);
        Thread.sleep(2000);
		inst1.close();
        Thread.sleep(2000);
        Thread.sleep(2000);
        Thread.sleep(2000);
		inst3.close();
		System.exit(0);
	}
	
}
