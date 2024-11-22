package cn.plaf.dataauth.param.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.plaf.dataauth.config.DataAuthType;
import cn.plaf.dataauth.param.auth.AreaDataAuthDTO;
import cn.plaf.dataauth.param.auth.DataAuthDTO;

/**
 * 1、此代码写在业务代码中
 * @author powersi
 *
 */
public class DataParamBuilder extends AbstractDataParamHelper {
	/**
	 * 全局 不加数据权限账户
	 */
	public static final  String GOBLE_EXT_USER_IDS="admin";
	public static final  String GOBLE_EXT_ROLE_IDS="1233";
	
	@Override
	public Map<String,Object> initParamMap() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("poolarea",  Arrays.asList(new String[] {"430700","430700","430700"}));
		map.put("crter", Arrays.asList(new String[] {"3","4","5"}));
		map.put("dept_id", "3");
		map.put("role_id", "3");
		return map;
	}



	@Override
	public List<String> initGobleExtUserIds() {

//		return Arrays.asList(GOBLE_EXT_USER_IDS.split(","));
		return null;
	}

	@Override
	public List<String> initGobleExtRoleIds() {

//		return Arrays.asList(GOBLE_EXT_ROLE_IDS.split(","));
		return null;
	}

	/**
	 * 1、从业务代码中读取权限参数 优先级：先业务 后注解
	 * 2、用户可以覆盖 plugin interceptor 配置的表字段和表名称，设置字段名注意有大小写区分 有如下几个类
	 * 	a、区域查询条件  AreaDataAuthDTO ，DataAuthType=DATA_SCOPE_AREA
	 * 	b、自定义in CustomDataAuthDTO,DataAuthType.DATA_SCOPE_CUSTOM
	 * 	c、自定义exists CustomExistsDataAuthDTO, DataAuthType.DATA_SCOPE_CUSTOM_EXISTS
	 * 	d、部门  DeptDataAuthDTO，   DataAuthType.DATA_SCOPE_DEPT
	 * 	e、创建人查询 SelfDataAuthDTO,DataAuthType.DATA_SCOPE_SELF
	 *
	 */
	@Override
	public DataAuthDTO getSelectDataAuth(String methodAbsName) {
		AreaDataAuthDTO da = null;

		da= new AreaDataAuthDTO() ;
//		da.setMethodAbsName("cn.plaf.dataauth.mapper.ArchDao.getArchList");
		da.setMethodAbsName("cn.plaf.dataauth.mapper.ArchDao.selectList");
////		DataAuthType[] dataAuthType=new DataAuthType[]{DataAuthType.DATA_SCOPE_AREA,
//////				DataAuthType.DATA_SCOPE_CUSTOM,DataAuthType.DATA_SCOPE_DEPT,
//////				DataAuthType.DATA_SCOPE_DEPT_AND_CHILD,DataAuthType.DATA_SCOPE_SELF,DataAuthType.DATA_SCOPE_FULL_MATCH,
//////				DataAuthType.DATA_SCOPE_CONTAIN_MATCH};
		DataAuthType[] dataAuthType=new DataAuthType[]{DataAuthType.DATA_SCOPE_CONTAIN_MATCH};
		da.setDataAuthType(dataAuthType);
//		da.setAreaColumn("DEPT_ID");
		return da;
	}

}
