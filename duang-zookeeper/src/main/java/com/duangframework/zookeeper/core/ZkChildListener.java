package com.duangframework.zookeeper.core;

import java.util.List;

public abstract class ZkChildListener implements IZkListener {

	@Override
	public void handleDataChange(String dataPath, Object data) throws Exception {
		
	}

	@Override
	public void handleDataDeleted(String dataPath) throws Exception {
		
	}
	@Override
	public abstract void handleChildChange(String parentPath, List<String> currentChilds) throws Exception;
	
	

}
