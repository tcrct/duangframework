package com.duangframework.mongodb;

import java.util.HashMap;
import java.util.Map;

/**
 * mongoDB operation constant.
 * 
 */
public final class Operator {
    
    //id
    public static final String ID = "_id";
    
    //query condition
    public static final String GT = "$gt";
    public static final String GTE = "$gte";
    public static final String LT = "$lt";
    public static final String LTE = "$lte";
    public static final String NE = "$ne";
    public static final String IN = "$in";
    public static final String NIN = "$nin";
    public static final String MOD = "$mod";
    public static final String ALL = "$all";
    public static final String SLICE = "$slice";
    public static final String SIZE = "$size";
    public static final String EXISTS = "$exists";
    public static final String WHERE = "$where";
    public static final String ELEMMATCH = "$elemMatch";
    
    //like
    public static final String LIKE = "like";
    //between 
    public static final String BETWEEN = "between";
    
    //query logic
    public static final String AND = "$and";
    public static final String OR = "$or";
    
    //2d and geo
    public static final String NEAR = "$near";
    public static final String WITHIN = "$within";
    public static final String CENTER = "$center";
    public static final String BOX = "$box";
    
    //update
    public static final String SET = "$set";
    public static final String UNSET = "$unset";
    public static final String INC = "$inc";
    public static final String MUL = "$mul";
    public static final String PUSH = "$push";
    public static final String PULL = "$pull";
    public static final String EACH = "$each";
    public static final String POP = "$pop";
    public static final String MIN = "$min";
    public static final String MAX = "$max";
    public static final String BIT = "$bit";
    
    //aggregation
    public static final String PROJECT = "$project";
    public static final String MATCH = "$match";
    public static final String LIMIT = "$limit";
    public static final String SKIP = "$skip";
    public static final String UNWIND = "$unwind";
    public static final String GROUP = "$group";
    public static final String SORT = "$sort";
    public static final String SUM = "$sum";

    public static final String HINT = "$hint";


    //dbo查询关键字转为sql
    public static final Map<String, String> SQL_OPERATOR_MAP = new HashMap<String,String>();
    static {
    	SQL_OPERATOR_MAP.put(Operator.GT, " > ");
    	SQL_OPERATOR_MAP.put(Operator.GTE, " >= ");
    	SQL_OPERATOR_MAP.put(Operator.LT, " < ");
    	SQL_OPERATOR_MAP.put(Operator.LTE, " <= ");
    	SQL_OPERATOR_MAP.put(Operator.NE, " != ");
    	SQL_OPERATOR_MAP.put(Operator.OR, " or ");
    	SQL_OPERATOR_MAP.put(Operator.AND, " and ");
    }
    
    
}
