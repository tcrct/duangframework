package com.duangframework.server.netty.decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duangframework.core.common.dto.ReturnDto;
import com.duangframework.core.kit.ToolsKit;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
        if(ToolsKit.isMapJsonString(json)) {
            parseMap(JSON.parseObject(json, Map.class));
        } else if(ToolsKit.isArrayJsonString(json)) {
            //TODO ..数组JSON方式待处理
//            parseArray(JSON.parseArray(json, ArrayList.class));
        }
        return paramsMap;
    }



    private void parseMap(Map<String, Object> sourceMap) {
        if(ToolsKit.isEmpty(sourceMap)) {
            return;
        }
        String tokenid =  sourceMap.get(ReturnDto.TOKENID_FIELD)+"";
        if(ToolsKit.isNotEmpty(tokenid)) {
            paramsMap.put(ReturnDto.TOKENID_FIELD, tokenid);
        }
        JSONObject dataObj = (JSONObject) sourceMap.get(ReturnDto.DATA_FIELD);
        if (ToolsKit.isNotEmpty(dataObj)) {		//自定义格式的
            for(Iterator<Map.Entry<String,Object>> entryIterator = dataObj.entrySet().iterator(); entryIterator.hasNext();){
                Map.Entry<String,Object> entry = entryIterator.next();
                Object value = entry.getValue();
                if(ToolsKit.isNotEmpty(value)) {
                    if (ToolsKit.isArray(value)) {
                        JSONArray jsonArray = (JSONArray) entry.getValue();
                        List<Object> valueList = new ArrayList(jsonArray.size());
                        for(Iterator<Object> iterator = jsonArray.iterator(); iterator.hasNext();) {
                            Object valueObj = iterator.next();
                            if( valueObj instanceof  String) {
                                valueList.add(((String)valueObj).trim());
                            } else {
                                valueList.add(valueObj);
                            }
                        }
                        dataObj.put(entry.getKey(), valueList);
                    }
                }
            }
            paramsMap.putAll(dataObj);
        } else {
            paramsMap.putAll(sourceMap);
        }
    }

    private void parseArray(ArrayList<Object> sourceList) {
        if(ToolsKit.isNotEmpty(sourceList)) {
            return;
        }

    }

}
