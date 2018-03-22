package com.duangframework.dubbo.service.impl;

/**
 * @author Created by laotang
 * @date createed in 2018/3/15.
 */
public class UserService implements com.duangframework.dubbo.service.impl.IUserService {

    public String findById(java.lang.String objectId, String userId, String version) {
        return objectId+"_" + userId + "_" + version;
    }

}
