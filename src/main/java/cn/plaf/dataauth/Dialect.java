/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package cn.plaf.dataauth;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

import cn.plaf.dataauth.config.DataAuthType;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 数据库方言，针对不同数据库进行实现
 *
 * @author gerryluo
 */
public interface Dialect {
	
	/**
	 * @param paramMap  参数列表
	 * @param extGobleUseIds 不需要执行数据过滤的用户
	 * @return
	 */
    boolean skip(Map<String,Object> paramMap,List<String> extGobleUseIds,List<String> extGobleRoleIds);


    /**
     * 处理查询参数对象
     *
     * @param ms              MappedStatement
     * @param parameterObject
     * @param boundSql
     * @param pageKey
     * @return
     */
    Object processParameterObject(MappedStatement ms, Object parameterObject, BoundSql boundSql,Map<String,Object> dataMap, CacheKey pageKey);




    /**
     * 完成所有任务后
     */
    void afterAll();

    /**
     * 设置参数
     *
     * @param properties 插件属性
     */
    void setProperties(Properties properties);
    
	/**
	 * 根据权限配置编辑sql
	 * @param sql
	 * @param dataAuthType
	 * @return
	 */
	String builderSql(String sql,DataAuthType dataAuthType,Map<String,Object> param);
	
	
	/**
	 * 统筹区权限
	 * @param sql
	 * @return
	 */
	String builderDataScopeArea(String sql,Map<String,Object> param);
	/**
	 * 自定义权限 in
	 * @param sql
	 * @return
	 */
	String builderDataScopeCustom(String sql,Map<String,Object> param);


	/**
	 * 自定义权限  exists
	 * @param sql
	 * @return
	 */
	String builderDataScopeCustomExists(String sql,Map<String,Object> param);
	
	/**
	 * 本部门权限
	 * @param sql
	 * @return
	 */
	String builderDataScopeDept(String sql,Map<String,Object> param);
	/**
	 * 部门及以下数据权限
	 * @param sql
	 * @return
	 */
	String builderDataScopeDeptAndChild(String sql,Map<String,Object> param);
	
	/**
	 * 仅本人数据权限
	 * @param sql
	 * @return
	 */
	String builderDataScopeSelf(String sql,Map<String,Object> param);


	/**
	 * 枚举字段完全匹配  column1=value1 and column2=value2
	 * @param sql
	 * @return
	 */
	String builderDataScopeFullMatch(String sql, Map<String, Object> param) ;
	/**
	 * 枚举字段完全匹配 column1=value1 and column2=value2
	 * @param sql
	 * @return
	 */
	String builderDataScopeContainMatch(String sql, Map<String, Object> param);


}
