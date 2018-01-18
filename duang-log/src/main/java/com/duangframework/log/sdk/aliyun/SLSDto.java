package com.duangframework.log.sdk.aliyun;

import com.duangframework.log.core.ILogRequest;

/**
 * @author Created by laotang
 * @date createed in 2018/1/13.
 */
public class SLSDto extends ILogRequest {

    private String project;
    private String store;
    private String topic;
    public SLSDto() {
    }

    public SLSDto(String project, String store, String topic) {
        this.project = project;
        this.store = store;
        this.topic = topic;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

}
