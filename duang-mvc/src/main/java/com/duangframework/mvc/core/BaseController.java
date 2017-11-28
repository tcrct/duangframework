package com.duangframework.mvc.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.duangframework.core.common.dto.http.head.HttpHeaders;
import com.duangframework.core.common.dto.http.request.HttpRequest;
import com.duangframework.core.common.dto.http.response.HttpResponse;
import com.duangframework.core.common.dto.result.HeadDto;
import com.duangframework.core.common.dto.result.ReturnDto;
import com.duangframework.core.exceptions.DuangMvcException;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.render.JsonRender;
import com.duangframework.mvc.render.Render;
import com.duangframework.mvc.render.TextRender;
import com.duangframework.mvc.render.XmlRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Created by laotang
 * @date on 2017/11/17.
 */
public abstract class BaseController {

    private static Logger logger = LoggerFactory.getLogger(BaseController.class);

    private HttpRequest request;
    private HttpResponse response;
    private Render render;

    public void init(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        logger.warn(ToolsKit.toJsonString(request.getParameterMap()));
    }

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    private void printRequest() {
        logger.info("******************************************************************************");
        logger.info("###########RequestDate:   " + ToolsKit.formatDate(getRequestDate(), ConfigKit.duang().key("default.date.format").defaultValue("yyyy-MM-dd HH:mm:ss").asString()));
        logger.info("###########RequestHeader: " + request.getHeader(HttpHeaders.USER_AGENT));
        logger.info("###########RequestURL:    " + request.getRequestURL());
        logger.info("###########RemoteMethod:  " + request.getMethod());
        logger.info("###########getContentType:  " + request.getContentType());
        logger.info("###########RequestValues: " + JSON.toJSONString(getAllParams()));
        logger.info("******************************************************************************");
    }

    /**
     * 取出请求日期时间
     * @return
     */
    private Date getRequestDate() {
        String d = request.getHeader(HttpHeaders.DATE);
        if (ToolsKit.isEmpty(d)) {
            return new Date();
        }
        try {
            return new Date(Long.parseLong(d));
        } catch (Exception e) {
            return ToolsKit.parseDate(d, ConfigKit.duang().key("default.date.format").defaultValue("yyyy-MM-dd HH:mm:ss").asString());
        }
    }

    /**
     * 取出所有请求的参数
     * @return
     */
    private Map<String, Object> getAllParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, String[]> requestParams = request.getParameterMap();
        if (ToolsKit.isNotEmpty(requestParams)) {
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }
        }
        return params;
    }

    public Render getRender() {
        return render;
    }

    /**
     * 返回JSON格式字符串
     *
     * @param obj
     */
    private void returnJson(Object obj) {
        render = new JsonRender(obj);
    }

    /**
     * 返回JSON格式字符串
     *
     * @param obj
     */
    private void returnJson(Object obj, Set<String> fieldSet) {
        render = new JsonRender(obj, fieldSet);
    }

    public void returnJson(int ret, String msg, Object obj) {
        ReturnDto<Object> dto = new ReturnDto<>();
        HeadDto head = new HeadDto();
        head.setRet(ret);
        head.setMsg(msg);
        head.setUri(request.getRequestURI());
        dto.setHead(head);
        dto.setData(obj);
        render = new JsonRender(dto);
    }

    /**
     * 返回文本格式
     * @param text
     */
    public void returnText(String text) {
        render = new TextRender(text);
    }

    /**
     * 返回XML格式
     * @param text
     */
    public void returnXml(String text) {
        render = new XmlRender(text);
    }

    public BaseController setValue(String key, Object obj) {
        request.setAttribute(key, obj);
        return this;
    }

    /**
     * 取出请求值
     *
     * @param key
     *            请求参数的key
     * @return 如果存在则返回字符串内容,不存在则返回""
     */
    public String getValue(String key) {
        String values = "";
        try {
            values = request.getParameter(key);
            if (ToolsKit.isEmpty(values)) {
                values = request.getAttribute(key)+"";
            }
//			if("GET".equalsIgnoreCase(request.getMethod().toString())){
//				try{
//					values = java.net.URLDecoder.decode(values);
//				} catch(Exception ex){}
//			}
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return values;
    }

    private Object getValueObj(String key) {
        Object values = null;
        try {
            values = request.getParameter(key);
            if (null == values) {
                values = request.getAttribute(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValueObj(String key, Class<?> cls) {
        Object values = getValueObj(key);
        if(ToolsKit.isEmpty(values)) {
            return null;
        }
        String jsonText = ToolsKit.toJsonString(values);
        if(ToolsKit.isEmpty(jsonText)) {
            return ObjectKit.newInstance(cls);
        }
        return (T) ToolsKit.jsonParseObject(jsonText, cls);
    }

    /**
     * 取出请求值
     *
     * @param key
     *            请求参数的key
     * @return 如果存在则返回字符串内容,不存在则返回null
     */
    public String[] getValues(String key) {
        String[] values = null;
        String errorMsg = "";
        try {
            values = request.getParameterValues(key);
            if (ToolsKit.isEmpty(values)) {
                values = ToolsKit.isEmpty(request.getAttribute(key)) ? null : (String[]) request.getAttribute(key);
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            try{
                Object valObj = request.getAttribute(key);
                if(valObj instanceof JSONArray) {
                    JSONArray array = (JSONArray)valObj;
                    values = array.toArray(new String[]{});
                }
            }catch(Exception e1) {
                errorMsg = e1.getMessage();
            }
        }
        if(ToolsKit.isEmpty(values)) {
            logger.warn(errorMsg);
        }
        return values;
    }

    /**
     * 根据key取请求值，并将数据转换为int返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回-1
     */
    protected int getIntValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                try {
                    logger.warn(e.getMessage(), e);
                    return getNumberValue(value).intValue();
                } catch (Exception e1) {
                    throw new DuangMvcException(e1.getMessage(), e1);
                }
            }
        }
        return -1;
    }

    /**
     * 根据key取请求值，并将数据转换为long返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回-1
     */
    protected long getLongValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                return Long.parseLong(value);
            } catch (Exception e) {
                try {
                    logger.warn(e.getMessage(), e);
                    return getNumberValue(value).longValue();
                } catch (Exception e1) {
                    throw new DuangMvcException(e1.getMessage(), e1);
                }
            }
        }
        return -1l;
    }

    /**
     * 根据key取请求值，并将数据转换为float返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回-1
     */
    protected float getFloatValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                return Float.parseFloat(value);
            } catch (Exception e) {
                try {
                    logger.warn(e.getMessage(), e);
                    return getNumberValue(value).floatValue();
                } catch (Exception e1) {
                    throw new DuangMvcException(e1.getMessage(), e1);
                }
            }
        }
        return -1f;
    }

    /**
     * 根据key取请求值，并将数据转换为double返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回-1
     */
    protected double getDoubleValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                return Double.parseDouble(value);
            } catch (Exception e) {
                throw new DuangMvcException(e.getMessage(), e);
            }
        }
        return -1d;
    }

    private Double getNumberValue(String key) {
        try {
            return  getDoubleValue(key);
        } catch (Exception e) {
            throw new DuangMvcException(e.getMessage(), e);
        }
    }

    /**
     * 根据key取请求值，并将数据转换为Boolean返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回false
     */
    protected Boolean getBooleanValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
                throw new DuangMvcException(e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * 根据key取请求值，并将数据转换为Date返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回null
     */
    protected Date getDateValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                long millisecond = Long.parseLong(value);
                Date date = new Date();
                date.setTime(millisecond);
                return date;
            } catch (Exception ex) {
                try {
                    return ToolsKit.parseDate(value, "yyyy-MM-dd HH:mm:ss");
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }
}
