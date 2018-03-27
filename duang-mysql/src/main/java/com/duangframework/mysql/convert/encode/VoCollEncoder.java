//package com.duangframework.mysql.convert.encode;
//
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBObject;
//import org.duang.db.annotation.VoColl;
//import org.duang.kit.DataType;
//import org.duang.kit.MongoKit;
//import org.duang.kit.ToolsKit;
//
//import java.lang.reflect.Array;
//import java.lang.reflect.Field;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.*;
//
///**
// * Vo集合对象属性转换
// * @author laotang
// */
//public class VoCollEncoder extends Encoder {
//
//	public VoCollEncoder( Object value, Field field ) {
//		super(value, field);
//	}
//
//	@Override
//	public String getFieldName() {
//		String fieldName = field.isAnnotationPresent(VoColl.class) ? field.getAnnotation(VoColl.class).name() : null;
//		return (ToolsKit.isNotEmpty(fieldName)) ? fieldName : field.getName();
//	}
//
//	@Override
//	public Object getValue() {
//		Object result = null;
//		Class<?> fieldType = field.getType();
//		if(fieldType.isArray()){
//			result = encodeArray();
//		} else if (DataType.isListType(fieldType)) {
//			result = encodeList(value);
//		} else if (DataType.isSetType(fieldType)) {
//			result = encodeSet(value);
//		} else if (DataType.isMapType(fieldType)) {
//			result = encodeMap(value);
//		}
//		return result;
//	}
////	@Override
////	public Object getValue() {
////		Object result = null;
////		Class<?> fieldType = field.getType();
////		if(fieldType.isArray()){
////			result = encodeArray();
////		} else {
////			System.out.println(value.getClass()+"                 "+field.getName() + " ############: " + fieldType.getName());
////			ParameterizedType paramType = (ParameterizedType)field.getGenericType();
////			Type[] paramTypes = paramType.getActualTypeArguments();
////			if(paramTypes.length == 1) {
////				result = encodeCollection();
////			} else if (paramTypes.length == 2){
////				result = encodeMap();
////			}
////		}
////		return result;
////	}
//
//	@SuppressWarnings("restriction")
//	private static Class<?> getParamTypesItemClass(Type type) {
//		if(type.getClass().equals(sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl.class)){
//			ParameterizedType paramTypeItem = (ParameterizedType)type;
//			Type[] paramTypesItem = paramTypeItem.getActualTypeArguments();
//			if(paramTypesItem.length == 1){
//				return (Class<?>)paramTypesItem[0];
//			} else if(paramTypesItem.length == 2){
//				return (Class<?>)paramTypesItem[1];
//			}
//		}
//		return (Class<?>)type;
//	}
//
//	private Object encodeArray(){
////		return encodeArray(value);
//		int length = Array.getLength(value);
//		List<DBObject> result = new ArrayList<DBObject>(length);
//		for (int i = 0; i < length; i++) {
//			Object obj = Array.get(value, i);
//			if(null !=obj){
//				result.add(MongoKit.toDBObject(obj));
//			}
//		}
//		return result;
//	}
//
//	private Object encodeArray(Object value) {
//		int length = Array.getLength(value);
//		List<DBObject> result = new ArrayList<DBObject>(length);
//		for (int i = 0; i < length; i++) {
//			Object obj = Array.get(value, i);
//			if(ToolsKit.isNotEmpty(obj)){
//				result.add(MongoKit.toDBObject(obj));
//			}
//		}
//		return result;
//	}
//
//
//
//	@SuppressWarnings("unchecked")
//	private Object encodeList(Object value) {
//		List<Object> list = (List<Object>) value;
//		List<DBObject> result = new ArrayList<DBObject>(list.size());
//		for (Object itemObj : list) {
//			if (null != itemObj) {
//				encodeCollection(result, itemObj.getClass(), itemObj);
//			}
//		}
//		return result;
//	}
//
//	@SuppressWarnings("unchecked")
//	private Object encodeSet(Object value) {
//		Set<Object> set = (Set<Object>) value;
//		Set<DBObject> result = new HashSet<DBObject>(set.size());
//		for (Object itemObj : set) {
//			if (null != itemObj) {
//				encodeCollection(result, itemObj.getClass(), itemObj);
//			}
//		}
//		return result;
//	}
//
//	@SuppressWarnings("unchecked")
//	private Object encodeMap(Object value) {
//		Map<Object, Object> map = (Map<Object, Object>) value;
//		Map<Object, Object> result = new HashMap<Object, Object>(map.size());
//		for (Object key : map.keySet()) {
//			Object itemObj = map.get(key);
//			if (null != itemObj) {
//				Class<?> objClass = itemObj.getClass();
//				if (DataType.isListType(objClass)) {
//					result.put(key, encodeList(itemObj));
//				} else if (DataType.isSetType(objClass)) {
//					result.put(key, encodeSet(itemObj));
//				} else if (DataType.isMapType(objClass)) {
//					result.put(key, (Map<Object, Object>) encodeMap(itemObj));
//				} else if(DataType.isBaseType(objClass)) {
//					result.put(key,value);
//				}  else {
//					result.put(key, MongoKit.toDBObject(itemObj));
//				}
//			} else {
//				result.put(key, null);
//			}
//		}
//		return result;
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private void encodeCollection(Collection result, Class<?> objClass, Object obj) {
//		if(DataType.isBaseType(objClass)) {
//			result.add(obj);
//		} else if (DataType.isListType(objClass)) {
//			result.add((List<DBObject>) encodeList(obj));
//		} else if (DataType.isSetType(objClass)) {
//			result.add((Set<DBObject>) encodeSet(obj));
//		} else if (DataType.isMapType(objClass)) {
//			DBObject dboMap = new BasicDBObject((Map<Object, Object>) encodeMap(obj));
//			result.add(dboMap);
//		} else {
//			result.add(MongoKit.toDBObject(obj));
//		}
//	}
//
//
//
//	private Object encodeCollection() {
////		 return encodeCollection(value);
//		List<DBObject> result = new ArrayList<DBObject>();
//		Collection collection = (Collection) value;
//		for (Object obj : collection) {
//			if (null != obj) {
//				result.add(MongoKit.toDBObject(obj));
//			}
//		}
//		return result;
//	}
//
//	@SuppressWarnings("rawtypes")
//	private Object encodeCollection(Object value){
//		List<Object> result = new ArrayList<Object>();
//		Collection coll = (Collection)value;
//		if(null != coll){
//			for(Iterator it = coll.iterator(); it.hasNext();){
//				Object obj = it.next();
//				System.out.println(field.getName() +"              "+ToolsKit.toJsonString(obj)+"                "+obj.getClass());
//				if(ToolsKit.isNotEmpty(obj)){
//					if(DataType.isListType(obj.getClass()) || DataType.isSetType(obj.getClass()) || DataType.isQueueType(obj.getClass())){
////						List objList = new ArrayList((List)encodeCollection(obj));
////						DBObject dboObj = new BasicDBObject();
////						dboObj.put(this.getFieldName(), new ArrayList(objList));
////						result.add(dboObj);
//						Object collectionObj = encodeCollection(obj);
////						System.out.println(collectionObj.getClass().getName());
////						System.out.println(ToolsKit.toJsonString(collectionObj));
//						result.add((List)collectionObj);
//					}else if(DataType.isMapType(obj.getClass())) {
//						DBObject dboMap = new BasicDBObject((Map)encodeMap(obj));
//						result.add(dboMap);
//					} else {
////						System.out.println(field.getName() + "################"+ obj+"                   "+ToolsKit.toJsonString(value));
////						result.add(EncodeConvetor.convetor(obj));
//						result.add( MongoKit.toDBObject(obj));
//					}
//				}
//			}
//		}
//		return result;
//	}
//
//	private Object encodeMap() {
////		 return encodeMap(value);
//		Map map = (Map) value;
//		Map result = new HashMap();
//		for (Object key : map.keySet()) {
//			Object obj = map.get(key);
//			if (null != obj) {
////				if(DataType.isListType(obj.getClass()) || DataType.isSetType(obj.getClass()) || DataType.isQueueType(obj.getClass())){
////					result.put(key, encodeCollection(obj));
////				} else if(DataType.isMapType(obj.getClass())) {
////					result.put(key, encodeMap(obj));
////				} else {
////					result.put(key, MongoKit.toDBObject(obj));
////				}
//				result.put(key, MongoKit.toDBObject(obj));
//			} else {
//				result.put(key, null);
//			}
//		}
//		return result;
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private Object encodeMap1(Object value){
//		Map map = (Map)value;
//		Map result = new HashMap(map.size());
//		for(Iterator<String> it = map.keySet().iterator(); it.hasNext();){
//			String key = it.next();
//			Object obj = map.get(key);
//			if(ToolsKit.isNotEmpty(obj)){
//				if(DataType.isListType(obj.getClass()) || DataType.isSetType(obj.getClass()) || DataType.isQueueType(obj.getClass())){
//					result.put(key, encodeCollection(obj));
//				} else if(DataType.isMapType(obj.getClass())) {
//					result.put(key, encodeMap(obj));
//				} else {
////					result.put(key, EncodeConvetor.convetor(obj));
//					result.put(key, MongoKit.toDBObject(obj));
//				}
//			} else {
//				result.put(key, null);
//			}
//		}
//		return result;
//	}
//
//}
