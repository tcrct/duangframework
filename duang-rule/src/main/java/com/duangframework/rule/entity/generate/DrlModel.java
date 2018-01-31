package com.duangframework.rule.entity.generate;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/1/25.
 */
public class DrlModel {
    /**
     * 规则包名
     */
    private String packageName;
    /**
     * 导入的包集合,全路径
     */
    private List<String> importPackageList;
    /**
     * 规则名称
     */
    private List<RuleInfoModel> ruleInfoModelList;

    public DrlModel() {
    }

    public DrlModel(String packageName, List<String> importPackageList, List<RuleInfoModel> ruleInfoModelList) {
        this.packageName = packageName;
        this.importPackageList = importPackageList;
        this.ruleInfoModelList = ruleInfoModelList;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getImportPackageList() {
        return importPackageList;
    }

    public void setImportPackageList(List<String> importPackageList) {
        this.importPackageList = importPackageList;
    }

    public List<RuleInfoModel> getRuleInfoModelList() {
        return ruleInfoModelList;
    }

    public void setRuleInfoModelList(List<RuleInfoModel> ruleInfoModelList) {
        this.ruleInfoModelList = ruleInfoModelList;
    }
}
