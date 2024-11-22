package cn.plaf.dataauth.dialect;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;


import cn.plaf.dataauth.DataAuthHelper;
import cn.plaf.dataauth.config.Constant;
import cn.plaf.dataauth.config.DataAuthType;
import cn.plaf.dataauth.param.helper.DataAuthParamValue;
import cn.plaf.dataauth.util.ExecutorUtil;
import cn.plaf.dataauth.util.MetaObjectUtil;

public abstract class AbstractHelperDialect extends AbstractDialect implements Constant {

	@Override
	public final boolean skip(Map<String, Object> paramMap, List<String> extGobleUseIds, List<String> extGobleRoleIds) {
		// 该方法不会被执行
		return true;
	}

	@Override
	public Object processParameterObject(MappedStatement ms, Object parameterObject, BoundSql boundSql,
			Map<String, Object> dataMap, CacheKey pageKey) {
		// 处理参数
		Map<String, Object> paramMap = null;
		if (parameterObject == null) {
			paramMap = new HashMap<String, Object>();
		} else if (parameterObject instanceof Map) {
			// 解决不可变Map的情况
			paramMap = new HashMap<String, Object>();
			paramMap.putAll((Map) parameterObject);
		} else {
			paramMap = new HashMap<String, Object>();
			// sqlSource为ProviderSqlSource时，处理只有1个参数的情况
			if (ms.getSqlSource() instanceof ProviderSqlSource) {
				String[] providerMethodArgumentNames = ExecutorUtil
						.getProviderMethodArgumentNames((ProviderSqlSource) ms.getSqlSource());
				if (providerMethodArgumentNames != null && providerMethodArgumentNames.length == 1) {
					paramMap.put(providerMethodArgumentNames[0], parameterObject);
					paramMap.put("param1", parameterObject);
				}
			}
			// 动态sql时的判断条件不会出现在ParameterMapping中，但是必须有，所以这里需要收集所有的getter属性
			// TypeHandlerRegistry可以直接处理的会作为一个直接使用的对象进行处理
			boolean hasTypeHandler = ms.getConfiguration().getTypeHandlerRegistry()
					.hasTypeHandler(parameterObject.getClass());
			MetaObject metaObject = MetaObjectUtil.forObject(parameterObject);
			// 需要针对注解形式的MyProviderSqlSource保存原值
			if (!hasTypeHandler) {
				for (String name : metaObject.getGetterNames()) {
					paramMap.put(name, metaObject.getValue(name));
				}
			}
			// 下面这段方法，主要解决一个常见类型的参数时的问题
			if (boundSql.getParameterMappings() != null && boundSql.getParameterMappings().size() > 0) {
				for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
					String name = parameterMapping.getProperty();
					if (paramMap.get(name) == null) {
						if (hasTypeHandler || parameterMapping.getJavaType().equals(parameterObject.getClass())) {
							paramMap.put(name, parameterObject);
							break;
						}
					}
				}
			}
		}

		Map<String, Object> paramDataMap = new HashMap<String, Object>();
		for (String key : dataMap.keySet()) {
			if (!Constant.CONFIG_KEYS.contains(key)) {
				paramDataMap.put(key, dataMap.get(key));
			}
		}

		processDataAuthParameter(ms, paramMap, boundSql, pageKey);
		paramMap.putAll(paramDataMap);
		return paramMap;
	}

	/**
	 * 组装sql 参数，? 占位符替换
	 * 
	 * @param ms
	 * @param paramMap
	 * @param boundSql
	 * @param pageKey
	 */
	private Object processDataAuthParameter(MappedStatement ms, Map<String, Object> paramMap, BoundSql boundSql,
			CacheKey pageKey) {
		List<DataAuthParamValue> localList = DataAuthHelper.getLocalList();
		// 处理参数配置
		if (boundSql.getParameterMappings() != null) {
			ParameterMapping first = new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_FIRST,
					Integer.class).build();
			ParameterMapping second = new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_SECOND,
					Integer.class).build();
			List<ParameterMapping> newParameterMappings = new ArrayList<ParameterMapping>(
					boundSql.getParameterMappings());
			int len = newParameterMappings.size();
			boolean HAVE_PAGEPARAMETER_FIRST = false;
			boolean HAVE_PAGEPARAMETER_SECOND = false;
			if (len == 0) {
				// 添加权限参数
				addDataAuthParamValue(ms, localList, newParameterMappings);
			} else {
				
				if (newParameterMappings.get(len - 1).getProperty().equals(PAGEPARAMETER_SECOND)
						|| newParameterMappings.get(len - 1).getProperty().equals(PAGEPARAMETER_FIRST)) {
					// 包含分页参数
					// 先删除分页参数
					Iterator<ParameterMapping> it = newParameterMappings.iterator();
					while (it.hasNext()) {
						ParameterMapping node = it.next();
						if (node.getProperty().equals(PAGEPARAMETER_FIRST)) {
							HAVE_PAGEPARAMETER_FIRST = true;
							it.remove();
							continue;
						}
						if (node.getProperty().equals(PAGEPARAMETER_SECOND)) {
							HAVE_PAGEPARAMETER_SECOND = true;
							it.remove();
							continue;
						}
					}
					// 添加权限参数
					addDataAuthParamValue(ms, localList, newParameterMappings);
					// 将分页参数重新加入
					if (HAVE_PAGEPARAMETER_FIRST) {
						newParameterMappings.add(first);
					}
					if (HAVE_PAGEPARAMETER_SECOND) {
						newParameterMappings.add(second);
					}
					
				} else {
					// 添加权限参数
					addDataAuthParamValue(ms, localList, newParameterMappings);
				}
			} 

			MetaObject metaObject = MetaObjectUtil.forObject(boundSql);
			metaObject.setValue("parameterMappings", newParameterMappings);
		}
		return paramMap;
	}

	private void addDataAuthParamValue(MappedStatement ms, List<DataAuthParamValue> localList,
			List<ParameterMapping> newParameterMappings) {
		for (DataAuthParamValue node : localList) {
			if (node.getValue().getClass() == long.class) {
				newParameterMappings
						.add(new ParameterMapping.Builder(ms.getConfiguration(), node.getKey(), long.class).build());
			} else if (node.getValue().getClass() == int.class) {
				newParameterMappings
						.add(new ParameterMapping.Builder(ms.getConfiguration(), node.getKey(), int.class).build());
			} else if (node.getValue().getClass() == String.class) {
				newParameterMappings
						.add(new ParameterMapping.Builder(ms.getConfiguration(), node.getKey(), String.class).build());
			} else if (node.getValue().getClass() == boolean.class) {
				newParameterMappings
						.add(new ParameterMapping.Builder(ms.getConfiguration(), node.getKey(), String.class).build());
			} else if (node.getValue().getClass() == Date.class) {
				newParameterMappings
						.add(new ParameterMapping.Builder(ms.getConfiguration(), node.getKey(), Date.class).build());
			}
			
//			else if(node.getValue().getClass().isArray()){
//				newParameterMappings
//				.add(new ParameterMapping.Builder(ms.getConfiguration(), node.getKey(), Arrays.class).build());
//			}else if( node.getValue() instanceof Collection<?>) {
//				newParameterMappings
//				.add(new ParameterMapping.Builder(ms.getConfiguration(), node.getKey(), Collection.class).build());
//			}
		}
	}

	@Override
	public void afterAll() {

	}

	@Override
	public void setProperties(Properties properties) {

	}

	@Override
	public String builderSql(String sql, DataAuthType dataAuthType, Map<String, Object> param) {
		StringBuffer sqlbuffer = new StringBuffer();
		// 获取传参
		List<DataAuthParamValue> localList = DataAuthHelper.getLocalList();
		/**
		 * 确保每次处理参数的前都是空值，防止参数重复加入，比如分页的时候 会俩次调用 此方法。
		 */
		localList = new ArrayList<DataAuthParamValue>();
		DataAuthHelper.setLocalList(localList);

		switch (dataAuthType) {

		case DATA_SCOPE_ALL:
			sqlbuffer.append(sql);
			break;
		case DATA_SCOPE_AREA:
			
			// 设置统筹区值
			sqlbuffer.append(builderDataScopeArea(sql, param));
			break;
		case DATA_SCOPE_CUSTOM:
			sqlbuffer.append(builderDataScopeCustom(sql, param));
			break;
		case DATA_SCOPE_CUSTOM_EXISTS:
			sqlbuffer.append(builderDataScopeCustomExists(sql, param));
			break;
		case DATA_SCOPE_DEPT:
			sqlbuffer.append(builderDataScopeDept(sql, param));
			break;
		case DATA_SCOPE_DEPT_AND_CHILD:
			sqlbuffer.append(builderDataScopeDeptAndChild(sql, param));
			break;
		case DATA_SCOPE_SELF:
			sqlbuffer.append(builderDataScopeSelf(sql, param));
			break;
		case DATA_SCOPE_FULL_MATCH:
			sqlbuffer.append(builderDataScopeFullMatch(sql, param));
			break;
		case DATA_SCOPE_CONTAIN_MATCH:
			sqlbuffer.append(builderDataScopeContainMatch(sql, param));
			break;
		}
		DataAuthHelper.setLocalList(localList);
		return sqlbuffer.toString();
	}

	/**
	 * 以下单独各个类型sql数据
	 */
	@Override
	public abstract String builderDataScopeArea(String sql, Map<String, Object> param);

	@Override
	public abstract String builderDataScopeCustom(String sql, Map<String, Object> param);

	@Override
	public abstract String builderDataScopeCustomExists(String sql, Map<String, Object> param);

	@Override
	public abstract String builderDataScopeDept(String sql, Map<String, Object> param);

	@Override
	public abstract String builderDataScopeDeptAndChild(String sql, Map<String, Object> param);

	@Override
	public abstract String builderDataScopeSelf(String sql, Map<String, Object> param);

	@Override
	public abstract String builderDataScopeFullMatch(String sql, Map<String, Object> param);

	@Override
	public abstract String builderDataScopeContainMatch(String sql, Map<String, Object> param);

}
