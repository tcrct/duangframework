package com.duangframework.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.duangframework.core.annotation.db.Vo;
import com.duangframework.core.common.IdEntity;

import java.util.List;
import java.util.Map;

/**
 * @author Created by laotang
 * @date on 2017/11/22.
 */
@Vo
public class UserVo extends IdEntity {

    private Map<String, String> addressMap ;
    private List<UserVo> typeList;
    @JSONField(name = "user_age")
    private int age;
    private String name;
    private long white;

    public long getWhite() {
        return white;
    }

    public void setWhite(long white) {
        this.white = white;
    }

    public double getHight() {
        return hight;
    }

    public void setHight(double hight) {
        this.hight = hight;
    }

    private double hight;

    public Map<String, String> getAddressMap() {
        return addressMap;
    }

    public void setAddressMap(Map<String, String> addressMap) {
        this.addressMap = addressMap;
    }

    public List<UserVo> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<UserVo> typeList) {
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
