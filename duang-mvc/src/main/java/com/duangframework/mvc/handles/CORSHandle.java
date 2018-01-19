package com.duangframework.mvc.handles;

import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.interfaces.IHandle;
import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ToolsKit;

import java.util.*;

/**
 * 请求跨域处理器
 * @author Created by laotang
 * @date createed in 2018/1/19.
 * @since  1.0
 */
public class CORSHandle implements IHandle {

    private Map<String,String> allowHostMap;					// 过滤后的允许跨域的域名
    private final static String PROTOCOL = "http://";
    private final static String PROTOCOLS = "https://";
    private final static String FORWARDED_PROTO = "X-Forwarded-Proto";
    private final static String DUANG_HTTPS  = "duang_https";			//自定义设置，在nginx里添加的设置
    private static String accessControlAllowHeaders = "";

    /**
     * 允许跨域访问的域名集合
     *
     * @param accessHostMap
     */
    public CORSHandle(Map<String, String> accessHostMap) {
        if (ToolsKit.isNotEmpty(accessHostMap)) {
            allowHostMap = new HashMap<>(accessHostMap.size());
            for (Iterator<Map.Entry<String, String>> it = accessHostMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, String> entry = it.next();
                String host = entry.getValue().toLowerCase().replace(PROTOCOL,"").replace(PROTOCOLS,"").replace("*","");
                if(ToolsKit.isNotEmpty(host)) {
                    allowHostMap.put(entry.getKey(), host);
                }
            }
        }
    }

    /**
     * 	执行处理器
     * @param target			请求URI
     * @param request			请求对象
     * @param response		返回对象
     * @throws Exception
     */
    @Override
    public void execute(String target, IRequest request, IResponse response) throws Exception {
        if(ToolsKit.isEmpty(allowHostMap)) {
            return;
        }
        String host = "";
        boolean isAllowAccess = false;
        String allowhost =  request.getHeader("Origin"); //httpRequest.getRequestURL().toString();
        if(ToolsKit.isEmpty(allowhost)) {
            allowhost = request.getHeader("Referer");
            if (ToolsKit.isEmpty(allowhost)) {
                allowhost = request.getHeader("Host");
            }
            if (ToolsKit.isEmpty(allowhost)) {
                String key = request.getParameter("allowhost");
                allowhost = allowHostMap.get(key);
            }
        }

        if(ToolsKit.isNotEmpty(allowhost)) {
            host = allowhost.toLowerCase().replace(PROTOCOL,"").replace(PROTOCOLS,"").replace("*","");
            isAllowAccess = allowHostMap.containsValue(host);
            if(!isAllowAccess) {
                for(Iterator<Map.Entry<String,String>> it = allowHostMap.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, String> entry = it.next();
                    if(allowhost.endsWith(entry.getValue())) {
                        isAllowAccess = true;
                        break;
                    }
                }
            }
        }

        if(isAllowAccess) {
            response.setHeader("Access-Control-Allow-Origin", host.endsWith("/") ? host.substring(0, host.length()-1) : host);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            String allowString = "Accept,Content-Type,Access-Control-Allow-Headers,Authorization,X-Requested-With,Authoriza,duang-token-id";
            if(ToolsKit.isEmpty(accessControlAllowHeaders)) {
                accessControlAllowHeaders = allowString;
                String[] arrayItem = PropertiesKit.duang().key("allow.host.headers").asArray();
                if (ToolsKit.isNotEmpty(arrayItem)) {
                    for (String allowItem : arrayItem) {
                        accessControlAllowHeaders += "," + allowItem;
                    }
                }
            }
            response.setHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);
        }
    }

    /**
     *  取请求协议
     * @param httpRequest   请求
     * @return		请求协议
     */
    private String getScheme(IRequest httpRequest ) {
        String forwardedProto = httpRequest.getHeader(DUANG_HTTPS);
        forwardedProto = ToolsKit.isEmpty(forwardedProto) ? httpRequest.getHeader(FORWARDED_PROTO) : forwardedProto;
        if (ToolsKit.isEmpty(forwardedProto)) {
            forwardedProto = httpRequest.getScheme();
        }
        return forwardedProto;
    }
}
