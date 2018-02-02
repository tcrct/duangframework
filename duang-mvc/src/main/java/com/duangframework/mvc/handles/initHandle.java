package com.duangframework.mvc.handles;

import com.duangframework.core.common.Const;
import com.duangframework.core.common.DuangId;
import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.common.dto.result.HeadDto;
import com.duangframework.core.interfaces.IHandle;
import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.IpUtils;

/**
 * @author Created by laotang
 * @date createed in 2018/1/11.
 */
public class initHandle implements IHandle {


    private static String LOCALHOST_IP = "";

    @Override
    public void execute(String target, IRequest request, IResponse response) throws Exception {
        builderRequestHeadDto(target, request, response);
    }

    /**
     *  构建请求DTO，并将该DTO设置到ThreadLocal变量中
     * @param request       请求对象
     */
    private void builderRequestHeadDto(String target, IRequest request, IResponse response) {
        HeadDto headDto = new HeadDto();
        // 设置请求ID
        String requestId = (String) request.getAttribute(Const.DUANG_REQUEST_ID);
        if (ToolsKit.isEmpty(requestId)) {
            requestId = PropertiesKit.duang().key("product.code").asString() + "_" + DuangId.get();
            request.setAttribute(Const.DUANG_REQUEST_ID, requestId);
        }

        // 设置客户端IP
        String remoteClientIp = request.getHeader(Const.FORWARDED_FOR);
        // 设置服务器端IP
        if(ToolsKit.isEmpty(LOCALHOST_IP)) {
            LOCALHOST_IP = IpUtils.getLocalHostIP(true, true);
        }
        if (ToolsKit.isNotEmpty(LOCALHOST_IP)){
            response.setHeader(Const.REMOTE_SERVICE_IP, LOCALHOST_IP);
        }
        if (ToolsKit.isEmpty(remoteClientIp)) { remoteClientIp = request.getHeader("X-Real-IP"); }
        if (ToolsKit.isEmpty(remoteClientIp)) { remoteClientIp = request.getHeader("Host"); }
        if (ToolsKit.isEmpty(remoteClientIp)) { remoteClientIp = request.getRemoteHost(); }
        if (ToolsKit.isNotEmpty(remoteClientIp)){
            response.setHeader(Const.REMOTE_CLIENT_IP, remoteClientIp.split(",")[0]);
        }

        headDto.setUri(target);
        headDto.setRequestId(requestId);
        headDto.setTimestamp(System.currentTimeMillis());
        headDto.setHeaderMap(request.getHeaderMap());
        headDto.setMethod(request.getMethod());
        headDto.setRequestId((String)request.getAttribute(Const.DUANG_REQUEST_ID));
        headDto.setClientId(response.getHeader(Const.REMOTE_CLIENT_IP));
        ToolsKit.setThreadLocalDto(headDto);
    }
}
