package com.duangframework.mvc.handles;

import com.duangframework.core.common.Const;
import com.duangframework.core.common.DuangId;
import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.common.dto.result.HeadDto;
import com.duangframework.core.interfaces.IHandle;
import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ToolsKit;

/**
 * @author Created by laotang
 * @date createed in 2018/1/11.
 */
public class initHandle implements IHandle {

    @Override
    public void execute(String target, IRequest request, IResponse response) throws Exception {
        builderRequestHeadDto(target, request);
    }

    /**
     *  构建请求DTO，并将该DTO设置到ThreadLocal变量中
     * @param request       请求对象
     */
    private void builderRequestHeadDto(String target, IRequest request) {
        HeadDto headDto = new HeadDto();
        // 设置请求ID
        String requestId = (String) request.getAttribute(Const.DUANG_REQUEST_ID);
        if (ToolsKit.isEmpty(requestId)) {
            requestId = PropertiesKit.duang().key("product.code").asString() + "_" + DuangId.get();
            request.setAttribute(Const.DUANG_REQUEST_ID, requestId);
        }
        headDto.setUri(target);
        headDto.setRequestId(requestId);
        headDto.setTimestamp(System.currentTimeMillis());
        headDto.setHeaderMap(request.getHeaderMap());
        ToolsKit.setThreadLocalDto(headDto);
    }
}
