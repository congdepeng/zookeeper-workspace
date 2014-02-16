package com.depeng.feature.zk.listener;

import com.depeng.feature.zk.common.NotifyEventType;

/**
 *
 * Implement this interface to handle and process customized event
 *
 */
public interface NotifyEventListener {

    /**
     * Implement this method to handle customized event
     *
     * @param notifyEventType notifyEventType
     * @param eventData additional data of this event, e.g. can be "IP:port" or other data
     */
	void handle(NotifyEventType notifyEventType, String eventData);
}
