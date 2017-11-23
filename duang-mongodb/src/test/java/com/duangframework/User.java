package com.duangframework;

import com.duangframework.core.annotation.db.Entity;
import com.duangframework.core.common.IdEntity;

import java.util.List;
import java.util.Map;

/**
 * @author Created by laotang
 * @date on 2017/11/22.
 */
@Entity(name = "testUser")
public class User extends IdEntity {

    private Map<String, String> addressMap ;
    private List<User> typeList;
    private int age;
    private String name;


    public Map<String, String> getAddressMap() {
        return addressMap;
    }

    public void setAddressMap(Map<String, String> addressMap) {
        this.addressMap = addressMap;
    }

    public List<User> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<User> typeList) {
        this.typeList = typeList;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
