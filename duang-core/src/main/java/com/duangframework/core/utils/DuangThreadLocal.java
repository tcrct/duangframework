package com.duangframework.core.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DuangThreadLocal<T> {

	private Map<Thread, T> duangThreadLocalMap = Collections.synchronizedMap(new HashMap<Thread, T>());
	private final static int MAX_THREAD_NUMBER = 10;
	
	public void set(T value) {
		Thread thread = Thread.currentThread();
		duangThreadLocalMap.put(thread, value);
		reset(thread, value);
	}

	public T get() {
		Thread thread = Thread.currentThread();
		T value = duangThreadLocalMap.get(thread);
		if (null == value && !duangThreadLocalMap.containsKey(thread)) {
			value = initialValue();
			duangThreadLocalMap.put(thread, value);
		}
//		remove();
		return value;
	}

	private void remove() {
		duangThreadLocalMap.remove(Thread.currentThread());
	}
	
	private void reset(Thread thread, T value) {
		if(duangThreadLocalMap.size() > MAX_THREAD_NUMBER) {
			duangThreadLocalMap.clear();
			duangThreadLocalMap.put(thread, value);
		}
	}

	protected T initialValue() {
		return null;
	}

}
