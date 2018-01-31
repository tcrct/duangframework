package com.duangframework.rule.utils;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.rule.entity.ParamItem;
import com.duangframework.rule.entity.generate.DrlModel;
import com.duangframework.rule.entity.generate.RuleInfoModel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Created by laotang
 * @date createed in 2018/1/25.
 */
public class AutoCreateDrlFile {

    private static final String TAB_FIELD = "    ";
    private static final String ENTER_FIELD = "\n";
    private static final String EXCEUTE_FUNCTION_FIELD = "_duangRuleExceute";

    public static String builder(DrlModel drlModel) throws Exception {
        StringBuilder drlString = new StringBuilder();
        drlString.append("package ").append(createDrlPackageName(drlModel.getPackageName())).append(ENTER_FIELD).append(ENTER_FIELD);
        List<String> packageList = drlModel.getImportPackageList();
        drlString.append(createImportPackage(packageList)).append(ENTER_FIELD);
        drlString.append(createFunctionString(packageList)).append(ENTER_FIELD).append(ENTER_FIELD);
        drlString.append(createRuleInfo(drlModel.getRuleInfoModelList())).append(ENTER_FIELD);
        System.out.println(drlString);
        return drlString.toString();
    }

    private static String createDrlPackageName(String packageName) {
        String packagePathString = ToolsKit.isEmpty(packageName) ? AutoCreateDrlFile.class.getPackage().getName().replace("utils", "drl;") : packageName;
        return packagePathString.toLowerCase().replace("package", "");
    }

    private static String createFunctionString(List<String> packageList) {
        String importPackageString = packageList.get(0);
        int startIndex = importPackageString.lastIndexOf(".")+1;
        int endIndex = importPackageString.endsWith(";") ? importPackageString.length()-1 : importPackageString.length();
        String ruleActionName = importPackageString.substring(startIndex, endIndex);
        StringBuilder functionString = new StringBuilder();
        functionString.append("function void ").append(EXCEUTE_FUNCTION_FIELD).append("(String ruleName) {").append(ENTER_FIELD)
                .append(TAB_FIELD).append(ruleActionName).append("(ruleName);").append(ENTER_FIELD)
                .append("}");
        return functionString.toString();
    }

    private static String createImportPackage(List<String> importPackageSet) {
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
        String mapPath = Map.class.getName();
        if(!importPackageSet.contains(mapPath)) {
            importPackageString.append("import ").append(mapPath).append(";").append(ENTER_FIELD);
        }
//        String ruleFunctionPath = IRuleFuction.class.getPackage().getName() +"." +IRuleFuction.class.getSimpleName();
//        if(!importPackageSet.contains(ruleFunctionPath)) {
//            importPackageString.append("import ").append(ruleFunctionPath).append(";").append(ENTER_FIELD);
//        }
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
            paramItemString.append("$ruleMap_").append(paramItem.getKey()).append(" : Map(this[\"").append(paramItem.getKey()).append("\"]");
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
        return EXCEUTE_FUNCTION_FIELD + "(\""+ruleInfoModel.getRuleName()+"\");";
    }
}
