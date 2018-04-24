package com.duangframework.server.netty.decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duangframework.core.common.Const;
import com.duangframework.core.common.dto.result.ReturnDto;
import com.duangframework.core.kit.ToolsKit;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author laotang
 * @date 2017/10/31
 */
public class JsonDecoder extends AbstractDecoder<Map<String, Object>> {

    public JsonDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        String json = request.content().toString(HttpConstants.DEFAULT_CHARSET);
        json = ToolsKit.isNotEmpty(json) ? json.trim() : "";
        if(ToolsKit.isMapJsonString(json)) {
            parseMap(JSON.parseObject(json, Map.class));
        } else if(ToolsKit.isArrayJsonString(json)) {
            //TODO ..数组JSON方式待处理
//            parseArray(JSON.parseArray(json, ArrayList.class));
        }
        if(ToolsKit.isNotEmpty(json)) {
            attributeMap.put(Const.DUANG_INPUTSTREAM_STR_NAME, json);
        }
        return attributeMap;
    }



    private void parseMap(Map<String, Object> sourceMap) {
        if(ToolsKit.isEmpty(sourceMap)) {
            return;
        }
        String tokenid =  sourceMap.get(ReturnDto.TOKENID_FIELD)+"";
        if(ToolsKit.isNotEmpty(tokenid)) {
            attributeMap.put(ReturnDto.TOKENID_FIELD, tokenid);
        }
        JSONObject dataObj = (JSONObject) sourceMap.get(ReturnDto.DATA_FIELD);

        if (ToolsKit.isNotEmpty(dataObj)) {		//自定义格式的
            attributeMap.putAll(parseMapValue(dataObj));
        } else {
            attributeMap.putAll(parseMapValue(sourceMap));
        }
    }

    private Map<String, Object> parseMapValue(Map<String, Object> dataObj) {
        Map<String, Object> params = new HashMap<>(dataObj.size());
        for (Iterator<Map.Entry<String, Object>> entryIterator = dataObj.entrySet().iterator(); entryIterator.hasNext(); ) {
            Map.Entry<String, Object> entry = entryIterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if(ToolsKit.isNotEmpty(value)) {
                params.put(key, value);
            }
        }
        return params;
    }
}
