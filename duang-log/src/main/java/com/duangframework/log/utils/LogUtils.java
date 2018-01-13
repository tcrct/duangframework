package com.duangframework.log.utils;

import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.IpUtils;
import com.duangframework.core.utils.MD5;
import com.duangframework.log.core.LogEnum;
import com.duangframework.log.sdk.aliyun.enums.SLSAccessEnum;

/**
 * @author Created by laotang
 * @date createed in 2018/1/13.
 */
public class LogUtils {

    /**
     * MD5(ip)
     * @return
     */
    public static String getLogShardHash() {
        return MD5.MD5Encode(IpUtils.getLocalHostIP());
    }

    private static LogEnum logEnum;
    private static SLSAccessEnum slsAccessEnum;

    public static void setLogEnum(LogEnum logEnum) {
        LogUtils.logEnum = logEnum;
    }

    public static LogEnum getLogEnum() {
        return logEnum;
    }

    public static SLSAccessEnum getSLSAccessEnum() {
        if(ToolsKit.isEmpty(slsAccessEnum)) {
            String productCode = PropertiesKit.duang().key("product.code").asString();
            for(SLSAccessEnum accessEnum : SLSAccessEnum.values()) {
                if(productCode.equalsIgnoreCase(accessEnum.getProductCode())) {
                    slsAccessEnum = accessEnum;
                    break;
                }
            }
        }
        return slsAccessEnum;
    }
}
