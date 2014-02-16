package com.depeng.feature.zk.testcase;

import com.depeng.feature.job.FeatureJob;
import com.depeng.feature.zk.common.ZkFeatureJobMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ZkFeatureJobClientEventNotifyTest {
	

	public static void main(String[] args) throws Exception {

        FeatureJob inst1 = new FeatureJob("feature_job_ip-1",9090);
		Thread.sleep(100);
		
		FeatureJob inst2 = new FeatureJob("feature_job_ip-2",9090);
		Thread.sleep(100);

		FeatureJob inst3 = new FeatureJob("feature_job_ip-3",9090);
		Thread.sleep(100);

		FeatureJob inst4 = new FeatureJob("feature_job_ip-4",9090);

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
