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

package cn.plaf.dataauth.util;

import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import cn.plaf.dataauth.Dialect;
import cn.plaf.dataauth.PageException;
import cn.plaf.dataauth.config.Constant;
import cn.plaf.dataauth.config.DataAuthType;
import cn.plaf.dataauth.param.helper.DataAuthMapMethod;
import cn.plaf.dataauth.param.helper.DataAuthParamValue;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author liuzenghui
 */
public abstract class ExecutorUtil {

    private static Field additionalParametersField;

    private static Field providerMethodArgumentNamesField;

    static {
        try {
            additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
            additionalParametersField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new PageException("获取 BoundSql 属性 additionalParameters 失败: " + e, e);
        }
        try {
            //兼容低版本
            providerMethodArgumentNamesField = ProviderSqlSource.class.getDeclaredField("providerMethodArgumentNames");
            providerMethodArgumentNamesField.setAccessible(true);
        } catch (NoSuchFieldException ignore) {
        }
    }

    /**
     * 获取 BoundSql 属性值 additionalParameters
     *
     * @param boundSql
     * @return
     */
    public static Map<String, Object> getAdditionalParameter(BoundSql boundSql) {
        try {
            return (Map<String, Object>) additionalParametersField.get(boundSql);
        } catch (IllegalAccessException e) {
            throw new PageException("获取 BoundSql 属性值 additionalParameters 失败: " + e, e);
        }
    }

    /**
     * 获取 ProviderSqlSource 属性值 providerMethodArgumentNames
     *
     * @param providerSqlSource
     * @return
     */
    public static String[] getProviderMethodArgumentNames(ProviderSqlSource providerSqlSource) {
        try {
            return providerMethodArgumentNamesField != null ? (String[]) providerMethodArgumentNamesField.get(providerSqlSource) : null;
        } catch (IllegalAccessException e) {
            throw new PageException("获取 ProviderSqlSource 属性值 providerMethodArgumentNames: " + e, e);
        }
    }

    /**
     * 尝试获取已经存在的在 MS，提供对手写count和page的支持
     *
     * @param configuration
     * @param msId
     * @return
     */
    public static MappedStatement getExistedMappedStatement(Configuration configuration, String msId) {
        MappedStatement mappedStatement = null;
        try {
            mappedStatement = configuration.getMappedStatement(msId, false);
        } catch (Throwable t) {
            //ignore
        }
        return mappedStatement;
    }
    /**
     * 权限查询
     *
     * @param dialect
     * @param executor
     * @param ms
     * @param parameter
     * @param rowBounds
     * @param resultHandler
     * @param boundSql
     * @param cacheKey
     * @param <E>
     * @return
     * @throws SQLException
     */
    public static <E> List<E> dataAuthQuery(Dialect dialect, Executor executor, MappedStatement ms, Object parameter,
    		RowBounds rowBounds, ResultHandler resultHandler,
    		BoundSql boundSql, CacheKey cacheKey,Map<String,Object> paramMap,List<DataAuthType> datList) throws SQLException {
    		//生成分页的缓存 key
    		CacheKey pageKey = cacheKey;
    		//调用方言获取分页 sql 如果有多条件
    		String pageSql = null;
    		String  dialectSql = boundSql.getSql();
			for(DataAuthType dat:datList) {
				pageSql  = dialect.builderSql(dialectSql, dat, paramMap);
				dialectSql =pageSql;
			}
    		//处理参数对象
    		parameter = dialect.processParameterObject(ms, parameter, boundSql,paramMap, pageKey);
    		
    		BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql, boundSql.getParameterMappings(), parameter);
    		
    		Map<String, Object> additionalParameters = getAdditionalParameter(boundSql);
    		
    		//设置动态参数
    		for (String key : additionalParameters.keySet()) {
    			pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
    		}
    		// 处理添加的IN 参数
    		for(DataAuthParamValue dto : DataAuthMapMethod.getLocalList()) {
    			if(dto.getKey().startsWith(Constant.FRECH_START)){
    				pageBoundSql.setAdditionalParameter(dto.getKey(), dto.getValue());
    			}
    		}
    		
    		//执行权限查询
    		return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, pageKey, pageBoundSql);
    }
    

}
