package com.duangframework.core.interfaces;

/**
 * @author Created by laotang
 * @date createed in 2018/1/10.
 */
public interface IWatch {

    /**
     * 目录节点监听
     */
    void watchChildNote();

    /**
     * 内容节点监听
     */
    void watchDataNote();

}
