package com.duangframework.mysql.common;

import com.duangframework.mysql.Operator;
import com.duangframework.mysql.utils.MysqlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Created by laotang
 * @date createed in 2018/3/26.
 */
public class CurdSqlModle implements java.io.Serializable {

    private String table;
    private Map<String,Object> paramMap;
    private List<String> paramKeyList;
    private CurdEnum curdEnum;
    private String idFieldName;

    public CurdSqlModle() {
    }

    public CurdSqlModle(CurdEnum curdEnum, String table, Map<String, Object> paramMap, String idFieldName) {
        this.curdEnum = curdEnum;
        this.table = table;
        this.idFieldName = idFieldName;
        setParamMap(paramMap);
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public CurdEnum getCurdEnum() {
        return curdEnum;
    }

    public void setCurdEnum(CurdEnum curdEnum) {
        this.curdEnum = curdEnum;
    }

    public String getIdFieldName() {
        return idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }

    public Object[] getParamValueArray() {
        List<Object> paramArray = null;
        if(null != paramKeyList) {
            int size = paramKeyList.size();
            paramArray = new ArrayList();
            for (int i = 0; i < size; i++) {
                paramArray.add(paramMap.get(paramKeyList.get(i)));
            }
            // 如果不是新增，则将ID字段添加到最后
            if(!CurdEnum.INSERT.name().equals(curdEnum.name())) {
                paramArray.add(paramMap.get(idFieldName));
            }
        }
        return paramArray.toArray();
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
        getParamKeyList();
    }

    private List<String> getParamKeyList() {
        if(null == paramKeyList) {
            paramKeyList = MysqlUtils.orderParamKey(paramMap);
        }
        //不论是增删改查那种情况，都先将ID字段移除，再自行将ID字段添加到最后
        paramKeyList.remove(idFieldName);
        return paramKeyList;
    }

    public String builderInsertSql() {
        if(null == paramKeyList) {
            return "";
        }
        StringBuilder insertFieldSql = new StringBuilder("(");
        StringBuilder insertPlaceholderSql = new StringBuilder("(");
        for(String fieldName : paramKeyList) {
            insertFieldSql.append(fieldName).append(",");
            insertPlaceholderSql.append("?").append(",");
        }
        insertFieldSql.deleteCharAt(insertFieldSql.length()-1);
        insertPlaceholderSql.deleteCharAt(insertPlaceholderSql.length()-1);
        insertFieldSql.append(")");
        insertPlaceholderSql.append(")");
        StringBuilder insertSql = new StringBuilder();
        insertSql.append(Operator.INSERT).append(Operator.EMPTY_SPACE)
                .append(table).append(Operator.EMPTY_SPACE)
                .append(insertFieldSql).append(Operator.EMPTY_SPACE)
                .append("values").append(Operator.EMPTY_SPACE).append(insertPlaceholderSql);
        return insertSql.toString();
    }

    public String builderUpdateSql() {
        StringBuilder updateSql = new StringBuilder();
        updateSql.append(Operator.UPDATE).append(Operator.EMPTY_SPACE).append(table).append(Operator.EMPTY_SPACE).
                append(Operator.SET).append(Operator.EMPTY_SPACE);
        for(String fieldName : paramKeyList) {
            updateSql.append(fieldName).append("=?").append(",");
        }
        updateSql.deleteCharAt(updateSql.length()-1);
        updateSql.append(Operator.EMPTY_SPACE).append(Operator.WHERE).append(Operator.EMPTY_SPACE)
                .append(idFieldName).append("=?");
        return updateSql.toString();
    }
}
