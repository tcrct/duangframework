//package com.duangframework.mvc.render;
//
//import com.alibaba.fastjson.serializer.JSONSerializer;
//import com.alibaba.fastjson.serializer.PropertyFilter;
//import com.alibaba.fastjson.serializer.SerializeWriter;
//import org.apache.commons.io.IOUtils;
//import org.duang.config.Config;
//import org.duang.kit.ProtostruffKit;
//import org.duang.kit.ToolsKit;
//import org.duang.logs.Logger;
//
//import java.io.OutputStream;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//
//
///**
// * 返回Protostuff Response
// * @author laotang
// *
// */
//public class ProtostuffRender extends Render {
//
//	private static final long serialVersionUID = -6757577835886443202L;
//	private static Logger logger = Logger.getLogger(ProtostuffRender.class);
//	private static final String contentType = "application/octet-stream;charset=" + Config.getEncoding();
//
//	private Set<String> fieldFilterSet ;
//
//	public class CustomFieldPropertyFilter implements PropertyFilter {
//			private Set<String> filterSet = new HashSet<String>();
//			//@Override
//		    public boolean apply(Object source, String name, Object value) {
//	            for(Iterator<String> it =  filterSet.iterator(); it.hasNext();){
//	                if(it.next().equals(name)){
//	                    return false;
//	                }
//	            }
//		        return true;
//		    }
//
//		    public CustomFieldPropertyFilter(Set<String> filterSet){
//		        this.filterSet = filterSet;
//		    }
//		}
//
//	public static Render init(){
//		return new ProtostuffRender(null);
//	}
//
//	public ProtostuffRender(Object obj){
//		this.obj = obj;
//	}
//
//	public ProtostuffRender(Object obj, Set<String> fieldFilterSet){
//		this.obj = obj;
//		this.fieldFilterSet = fieldFilterSet;
//	}
//
//	@Override
//	public void render() {
//		if(null == request || null == response) return;
//		setDefaultValue2Response();
//		OutputStream dataOutputSeream = null;
//		try {
////			response.setHeader(HttpHeaders.PRAGMA, "no-cache");
////			response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
////			response.setDateHeader(HttpHeaders.EXPIRES, 0);
//			response.setHeader(Config.OWNER_FILED, Config.OWNER);
//			response.setHeader(Config.RENDER_TYPE_FILED,  Config.PROTOSTUFF_BUFFER);
//			response.setContentType(contentType);
//			dataOutputSeream = response.getOutputStream();
//			if(_DUANG_BACKDOOR_PWD_.equals(request.getParameter("__bug__"))){
//				logger.print(ToolsKit.toJsonString(obj));
//			}
//
//			if(ToolsKit.isNotEmpty(fieldFilterSet)){
//				  CustomFieldPropertyFilter customFieldPropertyFilter = new CustomFieldPropertyFilter(fieldFilterSet);
//				  SerializeWriter serializeWriter = new SerializeWriter();
//				  JSONSerializer serializer = new JSONSerializer(serializeWriter);
//				  serializer.setDateFormat("yyyy-MM-dd HH:mm:ss");
//				  serializer.getPropertyFilters().add(customFieldPropertyFilter);
//				  serializer.write(obj);
//				  byte[] data = ProtostruffKit.serialize(serializeWriter.toString());
//				  response.setContentLength(data.length);
//				  dataOutputSeream.write(data);
//				  fieldFilterSet.clear();
//			} else {
//				byte[] data = ProtostruffKit.serialize(obj);
//				response.setContentLength(data.length);
//				dataOutputSeream.write(data);
//			}
//			dataOutputSeream.flush();
//		} catch (Exception e) {
//			logger.print("returnJson exception:  "+e.getMessage(), e);
//			e.printStackTrace();
//			throw new IllegalArgumentException(e);
//		}
//		finally {
//			if (dataOutputSeream != null){
//				IOUtils.closeQuietly(dataOutputSeream);
//			}
//		}
//	}
//}
