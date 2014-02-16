package com.depeng.feature.zk.testcase;


import com.depeng.feature.job.FeatureJob;

import java.util.ArrayList;
import java.util.List;

public class ZkFeatureJobClientTest {
	
	//private static CountDownLatch latch = new CountDownLatch(3);

	private static List<FeatureJob> instances = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		
		FeatureJob inst1 = new FeatureJob("feature_job_ip-1",9090);
		instances.add(inst1);
		Thread.sleep(100);
		
		FeatureJob inst2 = new FeatureJob("feature_job_ip-2",9090);
		instances.add(inst2);
		Thread.sleep(100);

		FeatureJob inst3 = new FeatureJob("feature_job_ip-3",9090);
		instances.add(inst3);
		Thread.sleep(100);

		FeatureJob inst4 = new FeatureJob("feature_job_ip-4",9090);
		instances.add(inst4);
		
		Thread.sleep(2000);
		System.out.println("feature_job_ip-1 is leader:"+inst1.isLeader());
		System.out.println("feature_job_ip-2 is leader:"+inst2.isLeader());
		System.out.println("feature_job_ip-3 is leader:"+inst3.isLeader());
		System.out.println("feature_job_ip-4 is leader:"+inst4.isLeader());
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
