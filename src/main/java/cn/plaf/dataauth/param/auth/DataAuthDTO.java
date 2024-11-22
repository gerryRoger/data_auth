package cn.plaf.dataauth.param.auth;

import java.io.Serializable;

import cn.plaf.dataauth.config.DataAuthType;


public class DataAuthDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 方法绝对路径
	 */
	private String methodAbsName;
	


	/**
	 * 类型类别
	 * @return
	 */
	private DataAuthType[] dataAuthType;
    


	/**
     *  非权限控制用户列表
     * @return
     */
	private String extUserId;
    
    /**
     *  非权限控制角色列表
     * @return
     */
	private String extRoleId;
	
	
	

	public String getMethodAbsName() {
		return methodAbsName;
	}

	public void setMethodAbsName(String methodAbsName) {
		this.methodAbsName = methodAbsName;
	}
	
    public DataAuthType[] getDataAuthType() {
		return dataAuthType;
	}

	public void setDataAuthType(DataAuthType[] dataAuthType) {
		this.dataAuthType = dataAuthType;
	}

	public String getExtUserId() {
		return extUserId;
	}

	public void setExtUserId(String extUserId) {
		this.extUserId = extUserId;
	}

	public String getExtRoleId() {
		return extRoleId;
	}

	public void setExtRoleId(String extRoleId) {
		this.extRoleId = extRoleId;
	}

}
