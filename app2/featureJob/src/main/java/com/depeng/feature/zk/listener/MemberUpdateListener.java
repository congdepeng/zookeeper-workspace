package com.depeng.feature.zk.listener;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

public interface MemberUpdateListener {
	void handle(PathChildrenCacheEvent event);
}
