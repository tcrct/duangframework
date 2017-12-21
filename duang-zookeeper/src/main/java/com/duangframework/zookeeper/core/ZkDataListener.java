package com.duangframework.zookeeper.core;

import java.util.List;

public abstract class ZkDataListener implements IZkListener {

	@Override
	public abstract void handleDataChange(String dataPath, Object data) throws Exception;
	
	@Override
	public abstract void handleDataDeleted(String dataPath) throws Exception;
	
	@Override
	public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {}
	
	

}
