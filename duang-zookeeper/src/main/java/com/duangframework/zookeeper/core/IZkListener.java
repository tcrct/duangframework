package com.duangframework.zookeeper.core;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;

public interface IZkListener extends IZkDataListener, IZkChildListener {

}
