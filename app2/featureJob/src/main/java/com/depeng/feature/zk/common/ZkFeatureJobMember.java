package com.depeng.feature.zk.common;

import com.depeng.feature.zk.common.Constant;

public class ZkFeatureJobMember {


	private String host;
	
	private int port=0;


	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public ZkFeatureJobMember(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public ZkFeatureJobMember(String fullPath) {
		int lastIndexOf = fullPath.lastIndexOf("/");
		String hostAdress = fullPath; 
		if(lastIndexOf>=0){
			hostAdress = fullPath.substring(lastIndexOf+1);
		}
		String[] splits = hostAdress.split(":");
		if(splits.length == 2){
			this.host = splits[0];
			this.port = Integer.valueOf(splits[1]);
		}
	}

	@Override
	public String toString() {
		return Constant.PATH_FEATURE_JOB.concat( Constant.PATH_MEMBER +this.host + ":" + this.port).trim();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	
}
