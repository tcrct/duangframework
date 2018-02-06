package com.duangframework.ext.crud;

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
public abstract  class CRUDController<T extends IdEntity> extends BaseController {

    private CRUDService CRUDService;
    private Class<T> entityClass;

    private CRUDService getCRUDService() {
        if(ToolsKit.isEmpty(CRUDService)) {
            CRUDService = new CRUDService(getEntityClass());
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

    public void add() throws ServiceException{
        try {
            T entityObj = getBean(getEntityClass());
            getCRUDService().add(entityObj);
            returnSuccessJson("新增记录成功");
        } catch (Exception e) {
            returnFailJson(e);
        }
    }

    public void delete() {
        String id = getValue("id");
        if(ToolsKit.isEmpty(id)) {
            throw new EmptyNullException("ID不能为空");
        }
        try {
            getCRUDService().delete(id);
            returnSuccessJson("删除记录成功");
        } catch (Exception  e) {
            returnFailJson(e);
        }
    }
}
