package com.depeng.feature.zk.testcase;


import com.depeng.feature.job.FeatureJob;

import java.util.ArrayList;
import java.util.List;

public class ZkFeatureJobClientTest {
	
	//private static CountDownLatch latch = new CountDownLatch(3);

	private static List<FeatureJob> instances = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		
		FeatureJob inst1 = new FeatureJob("feature_job_ip-1",9099);
		instances.add(inst1);
		Thread.sleep(100);
		
		FeatureJob inst2 = new FeatureJob("feature_job_ip-2",9098);
		instances.add(inst2);
		Thread.sleep(100);

		FeatureJob inst3 = new FeatureJob("feature_job_ip-3",9097);
		instances.add(inst3);
		Thread.sleep(100);

		FeatureJob inst4 = new FeatureJob("feature_job_ip-4",9096);
		instances.add(inst4);
		
		System.out.println("feature_job_ip-1 is leader:"+inst1.isLeader());
		System.out.println("feature_job_ip-2 is leader:"+inst2.isLeader());
		System.out.println("feature_job_ip-3 is leader:"+inst3.isLeader());
		System.out.println("feature_job_ip-4 is leader:"+inst4.isLeader());
		inst1.close();
		inst3.close();
		System.exit(0);
	}
	
}
