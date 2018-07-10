package com.duangframework.core.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.duangframework.core.annotation.db.Id;
import com.duangframework.core.kit.ToolsKit;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

public class IdEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ENTITY_ID_FIELD = "id";
	public static final String ID_FIELD = "_id";
	public static final String CREATETIME_FIELD = "createtime";
	public static final String CREATEUSERID_FIELD = "createuserid";
	public static final String UPDATETIME_FIELD = "updatetime";
	public static final String UPDATEUSERID_FIELD = "updateuserid";
	public static final String STATUS_FIELD = "status";
	public static final String SOURCE_FIELD = "source";
	public static final String STATUS_FIELD_SUCCESS = "审核通过";
	public static final String STATUS_FIELD_DELETE = "已删除";

	@Id
	private String id;

	private Date createtime;			//创建时间

	private String createuserid;		//创建人ID

	private Date updatetime;			//更新时间

	private String updateuserid;		//更新人ID

	private String status;			//数据状态(查数据字典)

	private String source;			//数据来源

	public IdEntity(String id, Date createtime, String createuserid,
                    Date updatetime, String updateuserid, String status, String source) {
		super();
		this.id = id;
		this.createtime = createtime;
		this.createuserid = createuserid;
		this.updatetime = updatetime;
		this.updateuserid = updateuserid;
		this.status = status;
		this.source = source;
	}

	public IdEntity() {
		super();
	}

	public String getId() {
		return id;
	}

    @JSONField(serialize = false, deserialize = false)
    public int getMysqlId() {
        if(ToolsKit.isNotEmpty(getId())) {
            return Integer.parseInt(getId());
        }
        return -1;
    }

	public void setId(String id) {
		if(ToolsKit.isEmpty(id)) {
			this.id = id;
		} else {
			try {
				if (ToolsKit.isNotEmpty(id) && id.length() == 24) {
					this.id = id;
				} else {
					this.id = Integer.parseInt(id) + "";
				}
			} catch (Exception e) {
				if (id.length() == 24) {
					this.id = id;
				}
			}
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getCreateuserid() {
		return createuserid;
	}

	public void setCreateuserid(String createuserid) {
		this.createuserid = createuserid;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getUpdateuserid() {
		return updateuserid;
	}

	public void setUpdateuserid(String updateuserid) {
		this.updateuserid = updateuserid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * 如果反序列化得到的json只存在key为_id的，也设置到id这个属性值里
	 * 如果_id，id两个key同时存在，则以key为id的为准, 一个正常的OBJECTID的长度是24位
	 * @param id
	 */
	@JSONField(name=ID_FIELD, serialize = false, ordinal = 100 )
	public void setFixId(String id) {
		if(ToolsKit.isNotEmpty(id) && ToolsKit.isEmpty(this.id) &&  id.length()==24 ) {
			this.id = id;
		}
	}

}
