package com.duangframework.rule.entity.generate;

import java.util.List;
import java.util.Set;

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
    private Set<String> importPackageSet;
    /**
     * 规则名称
     */
    private List<RuleInfoModel> ruleInfoModelList;

    public DrlModel() {
    }

    public DrlModel(String packageName, Set<String> importPackageSet, List<RuleInfoModel> ruleInfoModelList) {
        this.packageName = packageName;
        this.importPackageSet = importPackageSet;
        this.ruleInfoModelList = ruleInfoModelList;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<String> getImportPackageSet() {
        return importPackageSet;
    }

    public void setImportPackageSet(Set<String> importPackageSet) {
        this.importPackageSet = importPackageSet;
    }

    public List<RuleInfoModel> getRuleInfoModelList() {
        return ruleInfoModelList;
    }

    public void setRuleInfoModelList(List<RuleInfoModel> ruleInfoModelList) {
        this.ruleInfoModelList = ruleInfoModelList;
    }
}
