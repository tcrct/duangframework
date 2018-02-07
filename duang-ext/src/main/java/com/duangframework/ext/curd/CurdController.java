package com.duangframework.ext.curd;

import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.ServiceException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mvc.core.BaseController;

/**
 * @author Created by laotang
 * @date createed in 2018/2/6.
 */
public abstract  class CurdController<T extends IdEntity> extends BaseController {

    private CurdService CRUDService;
    private Class<T> entityClass;

    private CurdService getCRUDService() {
        if(ToolsKit.isEmpty(CRUDService)) {
            CRUDService = new CurdService(getEntityClass());
        }
        return CRUDService;
    }

    private Class<T> getEntityClass() {
        if(ToolsKit.isEmpty(entityClass)) {
            this.entityClass = ClassUtils.getSuperClassGenricType(getClass());
            if(Object.class.equals(entityClass)) {
                throw new RuntimeException("the "+entityClass.getCanonicalName()+" is not extends "+ IdEntity.class.getCanonicalName() +", exit...");
            }
        }
        return entityClass;
    }

    /**
     * 新增记录
     * @throws ServiceException
     */
    public void add() throws ServiceException{
        try {
            T entityObj = getBean(getEntityClass());
            returnSuccessJson(getCRUDService().add(entityObj) ? "新增记录成功" : "新增记录失败");
        } catch (Exception e) {
            returnFailJson(e);
        }
    }

    /**
     * 根据ID删除记录
     */
    public void delete() {
        String id = getValue("id");
        try {
            if(ToolsKit.isEmpty(id)) {
                throw new EmptyNullException("ID不能为空");
            }
            returnSuccessJson(getCRUDService().delete(id) ? "删除记录成功" : "删除记录失败");
        } catch (Exception  e) {
            returnFailJson(e);
        }
    }

    /**
     * 更新记录
     */
    public void update() {
        try {
            T entityObj = getBean(getEntityClass());
            returnSuccessJson( getCRUDService().update(entityObj) ? "更新记录成功" : "更新记录失败");
        } catch (Exception e) {
            returnFailJson(e);
        }
    }

    /**
     * 根据ID查找记录，key必须为id字段
     */
    public void findById() {
        String id = getValue("id");
        try {
            returnSuccessJson(getCRUDService().findById(id));
        } catch (Exception e) {
            returnFailJson(e);
        }
    }

    /**
     * 保存记录
     */
    public void save() {
        try {
            T entityObj = getBean(getEntityClass());
            returnSuccessJson(getCRUDService().save(entityObj) ? "保存记录成功" : "保存记录失败");
        } catch (Exception e) {
            returnFailJson(e);
        }
    }
}