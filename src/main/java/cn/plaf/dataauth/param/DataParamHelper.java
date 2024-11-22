package cn.plaf.dataauth.param;


import cn.plaf.dataauth.param.auth.DataAuthDTO;

import java.util.List;
import java.util.Map;

/**
 * 外部传参接口
 * @author gerryluo
 *
 */
public interface DataParamHelper {
	
	

	/**
	 * 用户设置参数
	 * @return
	 */
	public  Map<String,Object> initParamMap();


	public Map<String, Object> dealParamMap();
	
	/**
	 * 根据方法名称获取权限值
	 * @param methodAbsName
	 * @return
	 */
	public DataAuthDTO getSelectDataAuth(String methodAbsName);
	
	
	
	
	/**
	 * 设置不需要权限过滤的用户
	 * @return
	 */
	public List<String> initGobleExtUserIds();
	
	
	/**
	 * 设置不需要权限过滤的角色
	 * @return
	 */
	public List<String> initGobleExtRoleIds();
	
}
