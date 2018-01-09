package com.duangframework.core.common.enums;

public interface IEnums {

	public final int IENUMS_FAIL_CODE = 111000;
	public final String IENUMS_FAIL_MESSAGE = "发生未知异常";
	
	public final int IENUMS_SUCCESS_CODE = 0; //211000;
	public final String IENUMS_SUCCESS_MESSAGE = "操作成功";
	
	public int getCode();
	
	public String getMessage();
}
