package cn.plaf.dataauth.config;

public enum DataAuthType {
	/**
	 * 全部权限 代码:1
	 */
	DATA_SCOPE_ALL("1","全部权限","data_scope_all"),
	/**
	 * 统筹区权限 代码:2
	 */
	DATA_SCOPE_AREA("2","统筹区权限","data_scope_area"),
	/**
	 * 自定义权限IN 代码:3
	 */
	DATA_SCOPE_CUSTOM("3","自定义权限IN","data_scope_custom"),
	/**
	 * 自定义权限Exists 代码:4
	 */
	DATA_SCOPE_CUSTOM_EXISTS("4","自定义权限EXISTS","data_scope_custom_exists"),
	/**
	 * 本部门权限 代码:5
	 */
	DATA_SCOPE_DEPT("5","本部门权限","data_scope_dept"),
	/**
	 * 部门及以下数据权限 代码:6
	 */
	DATA_SCOPE_DEPT_AND_CHILD("6","部门及以下数据权限","data_scope_dept_and_child"),
	/**
	 * 仅本人数据权限 代码:7
	 */
	DATA_SCOPE_SELF("7","仅本人数据权限","data_scope_self"),

	/**
	 * 指定字段精确匹配  column1=value1 and clolumn2=value2 代码:8
	 */
	DATA_SCOPE_FULL_MATCH("8","指定字段完全匹配","data_scope_full_match"),
	/**
	 * 指定字段包含匹配  column1 in (value1_1,value1_2) and clolumn2 in (value2_2 ,value2_2) 代码:9
	 */
	DATA_SCOPE_CONTAIN_MATCH("9","指定字段包含匹配","data_scope_contain_match")
	;
	private String  key;
	private String desc;
	private String code;
	

	DataAuthType(String key, String desc,String code) {
		this.key= key;
		this.desc= desc;
		this.code= code;
	}
	
	
	public static DataAuthType  dataAuthTypeValueOf(String key) {
		DataAuthType type= null;
		Integer d = Integer.valueOf(key);
		switch (d) {
		case 1:
			type = DATA_SCOPE_ALL;
			break;
		case 2:
			type = DATA_SCOPE_AREA;
			break;
		case 3:
			type = DATA_SCOPE_CUSTOM;
			break;
		case 4:
			type = DATA_SCOPE_CUSTOM_EXISTS;
			break;
		case 5:
			type = DATA_SCOPE_DEPT;
			break;
		case 6:
			type = DATA_SCOPE_DEPT_AND_CHILD;
			break;
		case 7:
			type = DATA_SCOPE_SELF;
			break;
		case 8:
			type = DATA_SCOPE_FULL_MATCH;
			break;
		case 9:
			type = DATA_SCOPE_CONTAIN_MATCH;
			break;
		}
		return type;
	}

	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}

}
