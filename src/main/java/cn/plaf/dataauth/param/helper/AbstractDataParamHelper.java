package cn.plaf.dataauth.param.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.plaf.dataauth.param.auth.DataAuthDTO;
import cn.plaf.dataauth.param.DataParamHelper;

/**
 * 数据传值
 * @author luoboy
 *
 */
public abstract class AbstractDataParamHelper implements DataParamHelper {
	

	/**
	 * @description: 设置参数值
	 * @date: 11:44 2023/7/25
	 * @param: []
	 * @return: java.util.Map<java.lang.String,java.lang.Object>
	 **/
	@Override
	public abstract  Map<String,Object> initParamMap();
	/**
	 * @description: 处理map
	 * @date: 11:41 2023/7/25
	 * @param: []
	 * @return: java.util.Map<java.lang.String,java.lang.Object>
	 **/
	@Override
	public Map<String, Object> dealParamMap(){
		Map<String, Object> map  = initParamMap();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		for(String key: map.keySet()){
			paraMap.put(key.toUpperCase(),map.get(key));
		}
//		paraMap.putAll(map);
		return paraMap;
	}
	/**
	 * @description:
	 * @date: 11:45 2023/7/25
	 * @param: []
	 * @return: java.util.List<java.lang.String>
	 **/
	@Override
	public abstract List<String> initGobleExtUserIds();
	
	/**
	 * 根据方法名称获取权限值
	 * @param methodAbsName
	 * @return
	 */
	@Override
	public abstract DataAuthDTO getSelectDataAuth(String methodAbsName);
	
	

}
