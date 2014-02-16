package com.depeng.feature.zk.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Properties;

public class ConfigService {
	
	private static final String DEFAULT_CONFIG_LOCATION="/conf/config.properties";

	public Long getLong(String key){
		String value = this.get(key);
		if(value==null){
			return null;
		}
		return Long.valueOf(value);
	}
	
	public boolean getBoolan(String key,boolean defValue){
		String value = this.get(key);
		if(value==null){
			return defValue;
		}
		return Boolean.valueOf(value);
	}
	
	public Integer getInt(String key,int defaultValue){
		String value = this.get(key);
		if(value==null){
			return defaultValue;
		}
		return Integer.valueOf(value);
	}
	
	public BigDecimal getBigDecimal(String key){
		String value = this.get(key);
		if(value==null){
			return null;
		}
		return new BigDecimal(value);
	}
	
	public String get(String key,String defaultVal){
		String val = this.get(key);
		if(val==null){
			return defaultVal;
		}
		return val;
	}
	
	public String get(String key){
		return pros.getProperty(key);
	}
	
	private final Properties pros;
	
	private ConfigService(Properties properties) {
		pros = properties;
	}
	
	private static Object sync = new Object();
	private static ConfigService configServiceInstance = null;
	public static ConfigService load(){
		if (configServiceInstance == null) {
			synchronized(sync){
				if (configServiceInstance == null) {
					configServiceInstance = ConfigService.load(DEFAULT_CONFIG_LOCATION);
				}
			}
		}
		return configServiceInstance;
	}
	
	private static ConfigService load(String... locations){
		try {
			Properties props = new Properties();
			for (String location : locations) {
				InputStream is = null;
				try {
					is = ConfigService.class.getResourceAsStream(location);
					props.load(new InputStreamReader(is, "utf-8"));
				} finally {
					if (is != null) {
						is.close();
					}
				}
			}
			return new ConfigService(props);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
}
