package com.duangframework.mongodb.utils;

/**
 * @author Created by laotang
 * @date createed in 2018/4/20.
 */
public class User implements java.io.Serializable{
    private String name;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
