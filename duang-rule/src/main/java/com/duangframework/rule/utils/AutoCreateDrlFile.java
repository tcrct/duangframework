package com.duangframework.rule.utils;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.rule.core.IRuleFuction;
import com.duangframework.rule.entity.ParamItem;
import com.duangframework.rule.entity.generate.DrlModel;
import com.duangframework.rule.entity.generate.RuleInfoModel;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Created by laotang
 * @date createed in 2018/1/25.
 */
public class AutoCreateDrlFile {

    private static final String TAB_FIELD = "    ";
    private static final String ENTER_FIELD = "\n";

    public static String builder(DrlModel drlModel) throws Exception {
        StringBuilder drlString = new StringBuilder();
        drlString.append("package ").append(createDrlPackageName(drlModel.getPackageName())).append(ENTER_FIELD).append(ENTER_FIELD);
        drlString.append(createImportPackage(drlModel.getImportPackageSet())).append(ENTER_FIELD);
        drlString.append(createRuleInfo(drlModel.getRuleInfoModelList())).append(ENTER_FIELD);
        return drlString.toString();
    }

    private static String createDrlPackageName(String packageName) {
        String packagePathString = ToolsKit.isEmpty(packageName) ? AutoCreateDrlFile.class.getPackage().getName().replace("utils", "drl;") : packageName;
        return packagePathString.toLowerCase().replace("package", "");
    }

    private static String createImportPackage(Set<String> importPackageSet) {
        StringBuilder importPackageString = new StringBuilder();
        if(ToolsKit.isNotEmpty(importPackageSet)){
            for (String importPackage : importPackageSet) {
                if(ToolsKit.isNotEmpty(importPackage)) {
                    importPackage = importPackage.startsWith("import") ? importPackage : "import " + importPackage;
                    importPackage = importPackage.endsWith(";") ? importPackage : importPackage+";";
                    importPackageString.append(importPackage).append(ENTER_FIELD);
                }
            }
        }
//        String ruleUtilePath = RuleUtils.class.getPackage().getName() +"." +RuleUtils.class.getSimpleName();
//        if(!importPackageSet.contains(ruleUtilePath)) {
//            importPackageString.append("import ").append(ruleUtilePath).append(";").append(ENTER_FIELD);
//        }
        String ruleFunctionPath = IRuleFuction.class.getPackage().getName() +"." +IRuleFuction.class.getSimpleName();
        if(!importPackageSet.contains(ruleFunctionPath)) {
            importPackageString.append("import ").append(ruleFunctionPath).append(";").append(ENTER_FIELD);
        }
        return importPackageString.toString();
    }

    private static String createRuleInfo(List<RuleInfoModel> ruleInfoModelList) {
        if(ToolsKit.isEmpty(ruleInfoModelList)) {
            throw new EmptyNullException("ruleInfoModelList is null");
        }
        StringBuilder ruleInfoString = new StringBuilder();
        for (Iterator<RuleInfoModel> it = ruleInfoModelList.iterator(); it.hasNext();) {
            RuleInfoModel ruleInfoModel = it.next();
            ruleInfoString.append("// ").append(ruleInfoModel.getRuleDesc()).append(ENTER_FIELD);
            ruleInfoString.append("rule \"").append(ruleInfoModel.getRuleName()).append("\"").append(ENTER_FIELD)
                    .append(TAB_FIELD).append("no-loop ").append(ruleInfoModel.isNoLoop()).append(ENTER_FIELD)
                    .append(TAB_FIELD).append("salience ").append(ruleInfoModel.getSalience()).append(ENTER_FIELD)
                    .append(TAB_FIELD).append("when").append(ENTER_FIELD)
                    .append(TAB_FIELD).append(TAB_FIELD).append(createRuleWhen(ruleInfoModel.getWhenList())).append(ENTER_FIELD)
                    .append(TAB_FIELD).append("then").append(ENTER_FIELD)
                    .append(TAB_FIELD).append(TAB_FIELD).append(createRuleThen(ruleInfoModel)).append(ENTER_FIELD)
                    .append("end").append(ENTER_FIELD).append(ENTER_FIELD);
        }
        return ruleInfoString.toString();
    }

    private static String createRuleWhen(List<ParamItem> paramItemList) {
        if(ToolsKit.isEmpty(paramItemList)) {
            throw new EmptyNullException("paramItemList is null");
        }
        StringBuilder paramItemString = new StringBuilder();
        int size = paramItemList.size();
        for (int i=0; i<size; i++) {
            ParamItem paramItem = paramItemList.get(i);
            paramItemString.append("$ruleMap : Map([\"").append(paramItem.getKey()).append("\"]");
            paramItemString.append(" "+ paramItem.getOperatorEnum().getValue()+" ");
            Object valueObj = paramItem.getValue();
            if(valueObj instanceof  String) {
                paramItemString.append("\"").append(valueObj).append("\"");
            } else {
                paramItemString.append(valueObj);
            }
            paramItemString.append(");");
            if(i < (size-1)) {
                paramItemString.append(ENTER_FIELD).append(TAB_FIELD).append(TAB_FIELD);
            }
        }
        return paramItemString.toString();
    }

    private static String createRuleThen(RuleInfoModel ruleInfoModel) {
        return "eval(exceute(\""+ruleInfoModel.getRuleName()+"\"));";
    }
}
