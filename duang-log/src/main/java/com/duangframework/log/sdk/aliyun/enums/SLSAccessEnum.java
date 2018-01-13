package com.duangframework.log.sdk.aliyun.enums;


/**
 * SLS访问日志枚举
 * 项目，日志库，说明
 */
public enum SLSAccessEnum {

	QINGPLUS("syt-accesslog","qingplus","qingplus","轻加接口"),
	QM("syt-accesslog", "qingmall", "qingmall", "悦瘦营"),
	MALL("syt-accesslog", "mall", "syt-mall","商城"),
	AD("syt-accesslog", "sytad", "ad-center","广告"),
	GAM("syt-accesslog", "gam", "gam-center", "社区"),
	RECOMM("syt-accesslog", "recomm", "recomm-center", "推荐"),
	PAY("syt-accesslog", "pay", "pay-center","支付"),
	PLAN("syt-accesslog", "plan", "syt-task", "方案"),
	WX("syt-accesslog", "weixin", "syt-weixin", "微信"),
	PHONE("syt-accesslog", "phone", "phone","手机系统事件"),
	OPENAPI("syt-accesslog", "openapi", "openapi-center","第三方openapi"),
	EVENT("syt-accesslog", "event", "event", "自定义事件"),
	D28("syt-accesslog", "d28", "d28", "自定义事件"),
	QC("syt-accesslog", "qingcharity", "syt-qingcharity", "轻公益"),
	QS("syt-accesslog", "qingsecretary", "syt-qingsecretary", "轻蜜"),
	GENERALIZE("syt-accesslog", "generalize", "syt-generalize", "推广投放"),
	ANALYTICS("syt-accesslog", "analytics", "syt-analytics", "统计分析"),
	PUSH("syt-accesslog", "push", "push-center", "推送"),
	DIET("syt-accesslog", "diet", "diet-center", "饮食"),
	SYTTEST("syt-accesslog","test","test","日志测试");

    private final String project;		// 项目名称
    private final String store;			// 日志库名称(微服务名称)
	private final String productCode; //对应的项目名称
    private final String desc;			// 说明

    public String getProject() {
        return project;
    }
    public String getStore() {
        return store;
    }
    public String getDesc() { return desc; }
	public String getProductCode() {
		return productCode;
	}

    private SLSAccessEnum( String project, String store, String productCode, String desc) {
		this.project = project;
		this.store = store;
		this.productCode = productCode;
		this.desc = desc;
	}
}





