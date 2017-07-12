package com.trs.om.bean;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

/**
 * 实现RBAC模型的许可对象，提供的属性有：应用名称、对象标识、操作标识
 * 
 * @author chang
 * 
 */
public class Permission extends AuthorizationObject{//

	public Permission(){};
	public Permission(Long id){
		this.id = id;
	};

	private Long id;
	/**
	 * 
	 */
	private String object;
	/**
	 * 
	 */
	private String application;
	/**
	 * 
	 */
	private String operation;
	/**
	 *所属模块
	 * */
	private String module;
	private boolean checked;
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permission other = (Permission) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getModule() {
		return module;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
}
