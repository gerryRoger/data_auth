package cn.plaf.dataauth;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

import cn.plaf.dataauth.config.Constant;
import cn.plaf.dataauth.config.DataAuthAutoDialect;
import cn.plaf.dataauth.config.DataAuthType;
import cn.plaf.dataauth.dialect.AbstractHelperDialect;
import cn.plaf.dataauth.param.helper.DataAuthMapMethod;


public class DataAuthHelper extends DataAuthMapMethod implements Dialect{
	private DataAuthAutoDialect autoDialect;
	
	
	
	

	@Override
	public boolean skip(Map<String,Object> paramMap,List<String> extGobleUseIds,List<String> extGobleRoleIds) {


		boolean checkDataAuthFlag =true;
		// 校验角色权限
		if(extGobleRoleIds!= null && !extGobleRoleIds.isEmpty()) {
			Object roleId=  paramMap.get(Constant.ROLE_ID_COLUMN);
			String roleValue  = (String) paramMap.get(roleId);
			if(extGobleRoleIds.contains(roleValue)) {
				checkDataAuthFlag = false;
			}
		}
		// 校验用户权限
		if(extGobleUseIds!= null && !extGobleUseIds.isEmpty()) {
			Object userId=  paramMap.get(Constant.USER_ID_COLUMN);
			String value  = (String) paramMap.get(userId);
			if(extGobleUseIds.contains(value)) {
				checkDataAuthFlag = false;
			}
		}
		return checkDataAuthFlag;
	}


	@Override
	public Object processParameterObject(MappedStatement ms, Object parameterObject, BoundSql boundSql,Map<String,Object> dataMap,
			CacheKey pageKey) {
		return autoDialect.getDelegate().processParameterObject(ms, parameterObject, boundSql,dataMap, pageKey);
	}



	@Override
	public void afterAll() {
        AbstractHelperDialect delegate = autoDialect.getDelegate();
        if (delegate != null) {
            delegate.afterAll();
            autoDialect.clearDelegate();
        }		
        clearLocalList();
	}

	@Override
	public void setProperties(Properties properties) {
		autoDialect = new DataAuthAutoDialect();
		autoDialect.setProperties(properties);
		
	}

	@Override
	public String builderSql(String sql, DataAuthType dataAuthType, Map<String, Object> param) {
		return  autoDialect.getDelegate().builderSql(sql, dataAuthType, param);
	}

	@Override
	public String builderDataScopeArea(String sql, Map<String, Object> param) {
		return autoDialect.getDelegate().builderDataScopeArea(sql, param);
	}

	@Override
	public String builderDataScopeCustom(String sql, Map<String, Object> param) {
		return autoDialect.getDelegate().builderDataScopeCustom(sql, param);
	}

	@Override
	public String builderDataScopeCustomExists(String sql, Map<String, Object> param) {
		return autoDialect.getDelegate().builderDataScopeCustomExists(sql, param);
	}

	@Override
	public String builderDataScopeDept(String sql, Map<String, Object> param) {
		return autoDialect.getDelegate().builderDataScopeDept(sql, param);
	}

	@Override
	public String builderDataScopeDeptAndChild(String sql, Map<String, Object> param) {
		return autoDialect.getDelegate().builderDataScopeDeptAndChild(sql, param);
	}

	@Override
	public String builderDataScopeSelf(String sql, Map<String, Object> param) {
		return autoDialect.getDelegate().builderDataScopeSelf(sql, param);
	}

	@Override
	public String builderDataScopeFullMatch(String sql, Map<String, Object> param) {
		return autoDialect.getDelegate().builderDataScopeFullMatch(sql, param);
	}

	@Override
	public String builderDataScopeContainMatch(String sql, Map<String, Object> param) {
		return autoDialect.getDelegate().builderDataScopeContainMatch(sql, param);
	}


}
