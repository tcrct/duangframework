package com.duangframework.log.core;

import java.util.Map;

/**
 * @author Created by laotang
 * @date createed in 2018/1/13.
 */
public class LogDto  {

    private String project;
    private String store;
    private String topic;
    protected Map<String ,String> logItemMap;

    public LogDto() {    }

    public LogDto(String project, String store, String topic, Map<String, String> logItemMap) {
        this.project = project;
        this.store = store;
        this.topic = topic;
        this.logItemMap = logItemMap;
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
    public Map<String, String> getLogItemMap() {
        return logItemMap;
    }

    public void setLogItemMap(Map<String, String> logItemMap) {
        this.logItemMap = logItemMap;
    }
}
