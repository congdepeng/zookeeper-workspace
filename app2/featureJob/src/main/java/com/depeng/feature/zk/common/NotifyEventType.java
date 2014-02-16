package com.depeng.feature.zk.common;
import org.apache.commons.lang.enums.Enum;

public class NotifyEventType extends Enum{
	
	public static NotifyEventType CustomizedEvent = new NotifyEventType("customized_event");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected NotifyEventType(String name) {
		super(name);
	}

}
