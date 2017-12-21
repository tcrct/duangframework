package com.duangframework.zookeeper.kit;

import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.zookeeper.core.DuangZkSerializer;
import com.duangframework.zookeeper.core.IZkListener;
import com.duangframework.zookeeper.core.ZkChildListener;
import com.duangframework.zookeeper.core.ZkDataListener;
import com.duangframework.zookeeper.exception.ZooKeeperException;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * zookeeper客房端封装工具类
 *
 * @author laotang
 * @date 2017-12-19
 */
public class ZooKit {

	private static final Logger logger = LoggerFactory.getLogger(ZooKit.class);

	private static ReentrantLock zooKitLock = new ReentrantLock();
	private static ZooKit _zooKit;
	private static ZkClient _zkClient;
	private static String _path;
	private static Object _data;
	private static CreateMode _createMode;
	public static final int DEFAULT_SESSION_TIMEOUT = 30000;				//30s
	private static final int DEFAULT_CONNECTION_TIMEOUT = 10000;		//5s

	public static ZooKit duang() {
		if (ToolsKit.isEmpty(_zooKit)) {
			try {
				zooKitLock.lock();
				if (ToolsKit.isEmpty(_zooKit)) {
					_zooKit = new ZooKit();
				}
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			} finally {
				zooKitLock.unlock();
			}
		}
		clear();
		return _zooKit;
	}

	private ZooKit() {
		String zkServers = ConfigKit.duang().key("zk.servers").defaultValue("").asString();
		if(ToolsKit.isEmpty(zkServers)) {
			throw new ZooKeeperException("zkServers is null");
		}
		int sessionTimeOut = ConfigKit.duang().key("zk.session.timeout").defaultValue(DEFAULT_SESSION_TIMEOUT).asInt();
		int connectionTimeout = ConfigKit.duang().key("zk.connection.timeout").defaultValue(DEFAULT_CONNECTION_TIMEOUT).asInt();
		/**
		 * zk服务器地址<ip:port>（有多个以分号分隔）, 会话超时时间，连接超时时间，自定义序列化实例
		 */
		_zkClient = new ZkClient(zkServers, sessionTimeOut, connectionTimeout, new DuangZkSerializer());
	}

	private static void clear() {
		_path = null;
		_data = null;
		// 默认为持久化模式
		_createMode = CreateMode.PERSISTENT;
	}

	/**
	 * zk上的路径
	 * @param path		路径  / note / note / note
	 * @return
	 */
	public ZooKit path(String path) {
		_path = path;
		return _zooKit;
	}

	/**
	 * 要持久化到ZK上的数据
	 * @param data
	 * @return
	 */
	public ZooKit data(Object data) {
		_data = data;
		return _zooKit;
	}

	/**
	 *CreateMode 模式
	 * 		PERSISTENT：客户端断开后，znode不会自动删除。（即持久化）
	 *		PERSISTENT_SEQUENTIAL：znode在客户端断开连接时不会自动删除，并且其名称将附加单调递增的数字。
	 *		EPHEMERAL：客户端断开连接时被删除。
	 *		EPHEMERAL_SEQUENTIAL：znode将在客户端断开连接时被删除，并且其名称将被添加一个单调递增的数字。
	 * @param mode
	 * @return
	 */
	public ZooKit mode(CreateMode mode) {
		_createMode = mode;
		return _zooKit;
	}
	
	/**
	 * 创建节点，如有数据，并保存数据到该节点下
	 */
	private void create() {
		try {
			if (_zkClient.exists(_path)) {
				throw new ZooKeeperException(_path + "is exists! can't repeat create it...");
			}
			String paths[] = _path.split("/");
			String pathItem = "";
			Object objItem = null;
			for (int i = 1; i < paths.length; i++) {
				pathItem = pathItem + "/" + paths[i];
				// 如果目录不存在则创建
				if (!_zkClient.exists(pathItem)) {
					if (pathItem.equals(_path)) {
						objItem = _data;
					}
					// 默认创建持久化节点
					_zkClient.create(pathItem, objItem, _createMode);
				}
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			throw new ZooKeeperException("create node [" + _path + "] is fail: " + e.getMessage(), e);
		} finally {
			clear();
		}
	}

	/**
	 * 读取数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get() {
		try {
			return (T) _zkClient.readData(_path, true);
		} catch(Exception e){
			logger.warn(e.getMessage(), e);
			throw new ZooKeeperException("get node [" + _path + "] is fail: " + e.getMessage(), e);
		} finally {
			clear();
		}
	}
	
	/**
	 * 取得指定路径下的所有节点
	 * @return
	 */
	public List<String> children() {
		try {
			return _zkClient.getChildren(_path);
		} catch(Exception e){
			logger.warn(e.getMessage(), e);
			throw new ZooKeeperException("get node [" + _path + "]  children is fail: " + e.getMessage(), e);
		} finally {
			clear();
		}
	}

	/**
	 * 写数据
	 * 须先设置data()值
	 */
	public boolean set() {
		try {
			if(ToolsKit.isEmpty(_data)) {
				throw new ZooKeeperException("data is empty!");
			}
			// 如果路径不存在面则新增数据
			if (!_zkClient.exists(_path)) {
				create();
			} else {
				// 如果是存在的话，则更新
				_zkClient.writeData(_path, _data);
			}
			return true;
		} catch(Exception e){
			logger.warn(e.getMessage(), e);
			throw new ZooKeeperException("set node [" + _path + "] is fail: " + e.getMessage(), e);
		}finally {
			clear();
		}
	}
	
	/**
	 * 监听器
	 * @param listener
	 */
	public void listener(IZkListener listener) {
		exists();
		if(listener instanceof ZkDataListener) {
			_zkClient.subscribeDataChanges(_path, listener);
		} else if (listener instanceof ZkChildListener) {
			_zkClient.subscribeChildChanges(_path, listener);
		}
	}

	/**
	 * 物理删除zknote节点
	 * @return
	 */
	public boolean del() {
		try {
			if(ToolsKit.isEmpty(_path)) {
				throw new ZooKeeperException(_path + " is empty!");
			}
			// 物理删除
			return _zkClient.delete(_path);
		} catch(Exception e){
			logger.warn(e.getMessage(), e);
			throw new ZooKeeperException("delete node [" + _path + "] is fail: " + e.getMessage(), e);
		}finally {
			clear();
		}
	}

	/**
	 * 验证路径是否存在
	 * @return
	 */
	public boolean exists() {
		if(ToolsKit.isEmpty(_path)) {
			throw new ZooKeeperException(_path + " is empty!");
		}
		return _zkClient.exists(_path);
	}

	/**
	 * 返回ZK客户端对象
	 * @return
	 */
	public ZkClient client() {
		return _zkClient;
	}

}
