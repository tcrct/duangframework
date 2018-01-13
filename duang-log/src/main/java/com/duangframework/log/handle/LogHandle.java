package com.duangframework.log.handle;

import com.duangframework.core.common.Const;
import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.interfaces.IHandle;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.log.core.LogDto;
import com.duangframework.log.core.LogEnum;
import com.duangframework.log.kit.LogKit;
import com.duangframework.log.sdk.aliyun.enums.SLSAccessEnum;
import com.duangframework.log.utils.LogUtils;
import org.apache.http.HttpHeaders;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by laotang
 * @date createed in 2018/1/13.
 */
public class LogHandle implements IHandle {

    @Override
    public void execute(String target, IRequest request, IResponse response) throws Exception {

        String remoteIp = request.getHeader(Const.FORWARDED_FOR);  // 客户端真实 IP
        if (ToolsKit.isEmpty(remoteIp)) {
            remoteIp = request.getRemoteHost();
        } else {
            remoteIp = remoteIp.split(",")[0];
        }

        String intranetIp = request.getHeader(Const.REAL_IP);  // slb的内网IP

        String host = request.getHeader(HttpHeaders.HOST);
        if (ToolsKit.isEmpty(host)) {
            host = request.getRemoteHost();
        }

        String scheme = request.getHeader(Const.FORWARDED_PROTO);
        if (ToolsKit.isEmpty(scheme)) {
            scheme = request.getScheme();
        }

        String requestId = ToolsKit.getThreadLocalDto().getRequestId();

        Map<String, String> dataMap = builderSLSMap(requestId, remoteIp, intranetIp,response.getHeader(Const.REMOTE_SERVICE_IP), scheme,
                host,target,request.getMethod(),request.getHeader(HttpHeaders.USER_AGENT), request.getContentType(),getAllValue(request));

        LogEnum logEnum = LogUtils.getLogEnum();
        LogKit.duang().channel(logEnum).param(buildLogDto(dataMap)).submit();

    }

    private LogDto buildLogDto(Map<String,String> dataMap) {
        LogDto requestDto = new LogDto();
        requestDto.setLogItemMap(dataMap);
        SLSAccessEnum accessEnum = LogUtils.getSLSAccessEnum();
        requestDto.setProject(accessEnum.getProject());
        requestDto.setStore(accessEnum.getStore());
        requestDto.setTopic(accessEnum.getProductCode());
        return requestDto;
    }

    private String getAllValue(IRequest request) {
        Map<String, Object> paramMap = new HashMap<>();
        Enumeration<String> enumer = request.getParameterNames();
        if (ToolsKit.isNotEmpty(enumer)) {
            while (enumer.hasMoreElements()) {
                String key = enumer.nextElement();
                if(key.length() > 20) {
                    continue;  //对get请求进行了参数加密的key
                }
                String value = request.getParameter(key);
                paramMap.put(key, value);
            }
        }
        enumer = request.getAttributeNames();
        if (ToolsKit.isNotEmpty(enumer)) {
            while (enumer.hasMoreElements()) {
                String key = enumer.nextElement();
                String keyLowerCase = key.toLowerCase();
                if(key.equals(Const.DUANG_INPUTSTREAM_STR_NAME)) {
                    continue;
                }
                // 注意indexOf的值是比较的内容变量值
                if(keyLowerCase.contains("filter") && keyLowerCase.contains("shiro")
                        && keyLowerCase.contains(".")  &&  !Const.DUANG_INPUTSTREAM_STR_NAME.equals(key) ){
                    Object value = request.getAttribute(key);
                    if (ToolsKit.isNotEmpty(value)) {
                        paramMap.put(key, value);
                    }
                }
            }
        }
        return ToolsKit.toJsonString(paramMap);
    }

    public Map<String,String> builderSLSMap(String requestId, String remoteIp, String intranetIp, String servieIp, String scheme, String host, String target,
                                                   String method, String userAgent, String contentType, String params) {
        Map<String, String> slsMap = new HashMap<String, String>();
        slsMap.put("requestId", requestId);
        slsMap.put("remoteIp", checkValue(remoteIp));
        slsMap.put("intranetIp", checkValue(intranetIp));
        slsMap.put("localhostIp", checkValue(servieIp));
        slsMap.put("scheme", checkValue(scheme));
        slsMap.put("host", checkValue(host));
        slsMap.put("uri", checkValue(target));
        slsMap.put("method", checkValue(method));
        slsMap.put("agent", checkValue(userAgent));
        slsMap.put("contentType", checkValue(contentType));
        slsMap.put("params",checkValue(params));
        String timeMillis = System.currentTimeMillis() + "";
        slsMap.put("sendTime", timeMillis);
        slsMap.put("postTime", timeMillis);     // ODPS用来分区
        slsMap.put("exception", " ");
        slsMap.put("result", " ");
        slsMap.put("endTime", timeMillis);
//        if (Config.getEnadleSecurity()) {
//            try {
//                ShiroUser sUser = SecurityHelper.getShiroUser();
//                if (ToolsKit.isNotEmpty(sUser)) {
//                    slsMap.put("createUserName", sUser.getAccount());
//                    slsMap.put("createUserId", sUser.getUserid());
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
        return slsMap;
    }

    private String checkValue(String value) {
        return ToolsKit.isEmpty(value) ? "" : value;
    }
}
