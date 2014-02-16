package com.depeng.feature.zk.testcase;


import com.depeng.feature.job.FeatureJob;

import java.util.ArrayList;
import java.util.List;

public class ZkFeatureJobClientMemberChangeTest {
	

	public static void main(String[] args) throws Exception {
		
		FeatureJob inst1 = new FeatureJob("feature_job_ip-1",9090);
		inst1.addMemberUpdateListener();
		Thread.sleep(100);
		
		FeatureJob inst2 = new FeatureJob("feature_job_ip-2",9090);
		Thread.sleep(100);

		FeatureJob inst3 = new FeatureJob("feature_job_ip-3",9090);
		Thread.sleep(100);

		FeatureJob inst4 = new FeatureJob("feature_job_ip-4",9090);

//		inst3.close();
		
		Thread.sleep(1000);
		
//		inst2.close();
		
		Thread.sleep(1000);

		while (true)   {}
//		System.exit(0);
	}
	
}
