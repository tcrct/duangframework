package com.duangframework.server.netty.decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duangframework.core.common.Const;
import com.duangframework.core.common.dto.result.ReturnDto;
import com.duangframework.core.kit.ToolsKit;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;

import java.util.*;

/**
 *
 * @author laotang
 * @date 2017/10/31
 */
public class JsonDecoder extends AbstractDecoder<Map<String, String[]>> {

    public JsonDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, String[]> decoder() throws Exception {
        String json = request.content().toString(HttpConstants.DEFAULT_CHARSET);
        if(ToolsKit.isMapJsonString(json)) {
            parseMap(JSON.parseObject(json, Map.class));
        } else if(ToolsKit.isArrayJsonString(json)) {
            //TODO ..数组JSON方式待处理
//            parseArray(JSON.parseArray(json, ArrayList.class));
        }
        if(ToolsKit.isNotEmpty(json)) {
            paramsMap.put(Const.DUANG_INPUTSTREAM_STR_NAME, new String[]{json});
        }
        return paramsMap;
    }



    private void parseMap(Map<String, Object> sourceMap) {
        if(ToolsKit.isEmpty(sourceMap)) {
            return;
        }
        String tokenid =  sourceMap.get(ReturnDto.TOKENID_FIELD)+"";
        if(ToolsKit.isNotEmpty(tokenid)) {
            paramsMap.put(ReturnDto.TOKENID_FIELD, new String[]{tokenid});
        }
        JSONObject dataObj = (JSONObject) sourceMap.get(ReturnDto.DATA_FIELD);

        if (ToolsKit.isNotEmpty(dataObj)) {		//自定义格式的
            paramsMap.putAll(parseMapValue(dataObj));
        } else {
            paramsMap.putAll(parseMapValue(sourceMap));
        }
    }

    private Map<String, String[]> parseMapValue(Map<String, Object> dataObj) {
        Map<String, String[]> params = new HashMap<>(dataObj.size());
        for(Iterator<Map.Entry<String,Object>> entryIterator = dataObj.entrySet().iterator(); entryIterator.hasNext();){
            Map.Entry<String,Object> entry = entryIterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if(ToolsKit.isNotEmpty(value)) {
                if (ToolsKit.isArray(value)) {
                    JSONArray jsonArray = (JSONArray) entry.getValue();
                    List<String> valueList = jsonArray.toJavaObject(List.class); //.toJavaList(String.class);
                        /*
                        List<String> valueList = new ArrayList(jsonArray.size());
                        for(Iterator<Object> iterator = jsonArray.iterator(); iterator.hasNext();) {
                            Object valueObj = iterator.next();
                            if( valueObj instanceof  String) {
                                valueList.add(((String)valueObj).trim());
                            } else {
                                valueList.add(valueObj);
                            }
                        }
                        */
                    params.put(key, valueList.toArray(EMPTY_ARRAYS));
                } else {
                    String[] valueArray = {value+""};
                    params.put(key, valueArray);
                }
            }
        }
        return params;

    }

}
