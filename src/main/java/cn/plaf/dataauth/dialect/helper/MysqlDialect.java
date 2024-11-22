package cn.plaf.dataauth.dialect.helper;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.plaf.dataauth.util.StringUtil;
import org.apache.commons.text.StringSubstitutor;

import cn.plaf.dataauth.DataAuthHelper;
import cn.plaf.dataauth.PageException;
import cn.plaf.dataauth.dialect.AbstractHelperDialect;
import cn.plaf.dataauth.param.helper.DataAuthParamValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;


/**
 * @description 组装sql 
 * @author luoboy
 *
 */
public class MysqlDialect extends AbstractHelperDialect{
	
	
	/**
	 *  统筹区权限
	 */
	public  final String DATA_SCOPE_AREA_SQL=" ${ALIAS_TABLE_NAME}.${AREA_COLUMN}= ? ";
	
	
	/**
	 * 自定义权限 " ${ALIAS_TABLE_NAME}.${DEPT_ID_COLUMN} IN ( SELECT ${DEPT_ID_COLUMN} FROM ${SYS_ROLE_DEPT_TABLE_NAME} WHERE ${ROLE_ID_COLUMN} = '${ROLE_ID}' ) "
	 */
	public  final String DATA_SCOPE_CUSTOM_SQL=" ${ALIAS_TABLE_NAME}.${%s} IN ( SELECT ${%s} FROM ${%s} WHERE ${%s} = ? ) ";


	/**
	 * 自定义权限 "  EXISTS ( SELECT ${DEPT_ID_COLUMN} FROM ${SYS_ROLE_DEPT_TABLE_NAME} WHERE ${ALIAS_TABLE_NAME}.${DEPT_ID_COLUMN}= ${DEPT_ID_COLUMN} AND   ${ROLE_ID_COLUMN} = '${ROLE_ID}' ) "
	 * 			String paramSql = String.format(DATA_SCOPE_CUSTOM_EXISTS_SQL, SYS_ROLE_DEPT_TABLE_NAME,SYS_ROLE_DEPT_TABLE_NAME,DEPT_ID_COLUMN,DEPT_ID_COLUMN,SYS_ROLE_DEPT_TABLE_NAME,DEPT_ID_COLUMN
	 * 					param.get(ROLE_ID_COLUMN));
	 */
	public  final String DATA_SCOPE_CUSTOM_EXISTS_SQL=" EXISTS( SELECT 1 FROM ${%s} WHERE ${%s}.${%s} = ${ALIAS_TABLE_NAME}.${%s} AND ${%s}.${%s} = ? ) ";
	
	/**
	 * 本部门权限 ${ALIAS_TABLE_NAME}.${DEPT_ID_COL}='${DEPT_ID}'
	 */
	public  final String DATA_SCOPE_DEPT_SQL ="  ${ALIAS_TABLE_NAME}.${%s}= ?  ";
	
	/**CHECK_STATE
	 * 部门及以下数据权限 select * from sys_dept sd  where  find_in_set('1578539952180020381',DEPT_UP_ID)  or DEPT_ID ='1578539952180020381'
	 * 
	 * ${ALIAS_TABLE_NAME}.${DEPT_ID_COL} in ( select ${DEPT_ID_COL} from ${DEPT_TABLE_NAME} where ${DEPT_ID_COL} ='${DEPT_ID}'  or  find_in_set(${DEPT_ID_COL},${DEPT_UP_ID_COL}) )
	 */
	public  final String DATA_SCOPE_DEPT_AND_CHILD_SQL=" ${ALIAS_TABLE_NAME}.${%s} in ( select ${%s} from ${%s} where ${%s} =?  or  find_in_set( ? ,${%s}) )";
	
	/**
	 * 仅本人数据权限 ${ALIAS_TABLE_NAME}.${USER_ID_COLUMN}='${CRTER}'
	 */
	public  final String DATA_SCOPE_SELF_SQL=" ${ALIAS_TABLE_NAME}.${%s}= ? ";

	/**
	 * 完全匹配 ${ALIAS_TABLE_NAME}.${COLUMN}='${value1}'
	 */
	public  final String DATA_SCOPE_FULL_MATCH_SQL=" ${ALIAS_TABLE_NAME}.%s= ? ";

	/**
	 * 包含 匹配 ${ALIAS_TABLE_NAME}.${COLUMN} in ('${value1}')
	 */
	public  final String DATA_SCOPE_CONTAIN_MATCH_SQL=" ${ALIAS_TABLE_NAME}.%s IN ( %s ) ";
	
	
	
	
	
	
	
	@Override
	public String builderDataScopeArea(String sql,Map<String,Object> param) {
		
		CCJSqlParserManager parserManager=  null;
		Select select = null;
		try {
			parserManager = new CCJSqlParserManager();
			select = (Select) parserManager.parse(new StringReader(sql));
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			Table fromItem = (Table) plain.getFromItem();
			if(StringUtil.isEmpty(AREA_COLUMN)  ){
				return select.toString();
			}
			// 添加参数
			String key  = String.valueOf(param.get(AREA_COLUMN));
			DataAuthHelper.getLocalList().add(new DataAuthParamValue(key, param.get(key)));
			String paramSql  =   String.format(DATA_SCOPE_AREA_SQL, param.get(AREA_COLUMN));
	        //有别名用别名，无别名用表名，防止字段冲突报错
	        String aliasTableName = fromItem.getAlias() == null ? fromItem.getName() : fromItem.getAlias().getName();
	        param.put("ALIAS_TABLE_NAME", aliasTableName);
	        StringSubstitutor sub = new StringSubstitutor(param);
	        String authSql = sub.replace(paramSql);
	        if (plain.getWhere() == null) {
	            plain.setWhere(CCJSqlParserUtil.parseCondExpression(authSql));
	        } else {
	            plain.setWhere(new AndExpression(plain.getWhere(), CCJSqlParserUtil.parseCondExpression(authSql)));
	        }
		} catch (Exception e) {
			throw new PageException(e);
		}
		return select.toString();
	}

	@Override
	public String builderDataScopeCustom(String sql,Map<String,Object> param) {
		CCJSqlParserManager parserManager=  null;
		Select select = null;
		try {
			parserManager = new CCJSqlParserManager();
			select = (Select) parserManager.parse(new StringReader(sql));
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			Table fromItem = (Table) plain.getFromItem();
			// 如果没有配置值，直接过
			if(StringUtil.isEmpty(DEPT_ID_COLUMN) ||  StringUtil.isEmpty(SYS_ROLE_DEPT_TABLE_NAME) || StringUtil.isEmpty(ROLE_ID_COLUMN) ){
				return select.toString();
			}
			String paramSql = String.format(DATA_SCOPE_CUSTOM_SQL, DEPT_ID_COLUMN,
					DEPT_ID_COLUMN,SYS_ROLE_DEPT_TABLE_NAME,
					ROLE_ID_COLUMN,param.get(ROLE_ID_COLUMN));
			String key  = String.valueOf(param.get(ROLE_ID_COLUMN));
			DataAuthHelper.getLocalList().add(new DataAuthParamValue(key, param.get(key)));
			
	        //有别名用别名，无别名用表名，防止字段冲突报错
	        String aliasTableName = fromItem.getAlias() == null ? fromItem.getName() : fromItem.getAlias().getName();
	        param.put("ALIAS_TABLE_NAME", aliasTableName);
	        StringSubstitutor sub = new StringSubstitutor(param);
	        String authSql = sub.replace(paramSql);
	        if (plain.getWhere() == null) {
	            plain.setWhere(CCJSqlParserUtil.parseCondExpression(authSql));
	        } else {
	            plain.setWhere(new AndExpression(plain.getWhere(), CCJSqlParserUtil.parseCondExpression(authSql)));
	        }
		} catch (Exception e) {
			throw new PageException(e);
		}
		return select.toString();
	}

	@Override
	public String builderDataScopeCustomExists(String sql, Map<String, Object> param) {
		CCJSqlParserManager parserManager=  null;
		Select select = null;
		try {
			parserManager = new CCJSqlParserManager();
			select = (Select) parserManager.parse(new StringReader(sql));
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			Table fromItem = (Table) plain.getFromItem();
			// 如果没有配置值，直接过
			if(StringUtil.isEmpty(DEPT_ID_COLUMN) ||  StringUtil.isEmpty(SYS_DEPT_TABLE_NAME)  ){
				return select.toString();
			}
//			String paramSql = String.format(DATA_SCOPE_CUSTOM_EXISTS_SQL, DEPT_ID_COLUMN,
//					DEPT_ID_COLUMN,SYS_ROLE_DEPT_TABLE_NAME,
//					ROLE_ID_COLUMN,param.get(ROLE_ID_COLUMN));
			String paramSql = String.format(DATA_SCOPE_CUSTOM_EXISTS_SQL, SYS_DEPT_TABLE_NAME,SYS_DEPT_TABLE_NAME,DEPT_ID_COLUMN,DEPT_ID_COLUMN,SYS_DEPT_TABLE_NAME,DEPT_ID_COLUMN
					 					,param.get(DEPT_ID_COLUMN));
			String key  = String.valueOf(param.get(DEPT_ID_COLUMN));
			DataAuthHelper.getLocalList().add(new DataAuthParamValue(key, param.get(key)));

			//有别名用别名，无别名用表名，防止字段冲突报错
			String aliasTableName = fromItem.getAlias() == null ? fromItem.getName() : fromItem.getAlias().getName();
			param.put("ALIAS_TABLE_NAME", aliasTableName);
			StringSubstitutor sub = new StringSubstitutor(param);
			String authSql = sub.replace(paramSql);
			if (plain.getWhere() == null) {
				plain.setWhere(CCJSqlParserUtil.parseCondExpression(authSql));
			} else {
				plain.setWhere(new AndExpression(plain.getWhere(), CCJSqlParserUtil.parseCondExpression(authSql)));
			}
		} catch (Exception e) {
			throw new PageException(e);
		}
		return select.toString();
	}

	@Override
	public String builderDataScopeDept(String sql,Map<String,Object> param) {
		CCJSqlParserManager parserManager=  null;
		Select select = null;
		try {
			parserManager = new CCJSqlParserManager();
			select = (Select) parserManager.parse(new StringReader(sql));
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			Table fromItem = (Table) plain.getFromItem();
			// 如果没有配置值，直接过
			if(StringUtil.isEmpty(DEPT_ID_COLUMN)  ){
				return select.toString();
			}
//			${ALIAS_TABLE_NAME}.${DEPT_ID_COLUMN}='${DEPT_ID}'
			String paramSql = String.format(DATA_SCOPE_DEPT_SQL, DEPT_ID_COLUMN, param.get(DEPT_ID_COLUMN));
			String key  = String.valueOf(param.get(DEPT_ID_COLUMN));
			DataAuthHelper.getLocalList().add(new DataAuthParamValue(key, param.get(key)));

	        //有别名用别名，无别名用表名，防止字段冲突报错
	        String aliasTableName = fromItem.getAlias() == null ? fromItem.getName() : fromItem.getAlias().getName();
	        param.put("ALIAS_TABLE_NAME", aliasTableName);
	        StringSubstitutor sub = new StringSubstitutor(param);
	        String authSql = sub.replace(paramSql);
	        if (plain.getWhere() == null) {
	            plain.setWhere(CCJSqlParserUtil.parseCondExpression(authSql));
	        } else {
	            plain.setWhere(new AndExpression(plain.getWhere(), CCJSqlParserUtil.parseCondExpression(authSql)));
	        }
		} catch (Exception e) {
			throw new PageException(e);
		}
		return select.toString();
	}

	@Override
	public String builderDataScopeDeptAndChild(String sql,Map<String,Object> param) {
		CCJSqlParserManager parserManager=  null;
		Select select = null;
		try {
			parserManager = new CCJSqlParserManager();
			select = (Select) parserManager.parse(new StringReader(sql));
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			Table fromItem = (Table) plain.getFromItem();
			// 如果没有配置值，直接过
			if(StringUtil.isEmpty(DEPT_ID_COLUMN) || StringUtil.isEmpty(SYS_DEPT_TABLE_NAME) || StringUtil.isEmpty(DEPT_UP_ID_COLUMN) ){
				return select.toString();
			}
//			 * ${ALIAS_TABLE_NAME}.${DEPT_ID_COLUMN} in ( select ${DEPT_ID_COLUMN} from ${SYS_DEPT_TABLE_NAME} where ${DEPT_ID_COLUMN} ='${DEPT_ID}'  or  find_in_set('${DEPT_ID}',${DEPT_UP_ID_COLUMN}) )

			String paramSql = String.format(DATA_SCOPE_DEPT_AND_CHILD_SQL, 
					DEPT_ID_COLUMN, 
					DEPT_ID_COLUMN,
					SYS_DEPT_TABLE_NAME,
					DEPT_ID_COLUMN,
					param.get(DEPT_ID_COLUMN),
					param.get(DEPT_ID_COLUMN),
					DEPT_UP_ID_COLUMN
					);
			String key  = String.valueOf(param.get(DEPT_ID_COLUMN));
			DataAuthHelper.getLocalList().add(new DataAuthParamValue(key, param.get(key)));
			DataAuthHelper.getLocalList().add(new DataAuthParamValue(key, param.get(key)));
	        //有别名用别名，无别名用表名，防止字段冲突报错
	        String aliasTableName = fromItem.getAlias() == null ? fromItem.getName() : fromItem.getAlias().getName();
	        param.put("ALIAS_TABLE_NAME", aliasTableName);
	        StringSubstitutor sub = new StringSubstitutor(param);
	        String authSql = sub.replace(paramSql);
	        
	        if (plain.getWhere() == null) {
	            plain.setWhere(CCJSqlParserUtil.parseCondExpression(authSql));
	        } else {
	            plain.setWhere(new AndExpression(plain.getWhere(), CCJSqlParserUtil.parseCondExpression(authSql)));
	        }
		} catch (Exception e) {
			throw new PageException(e);
		}
		return select.toString();
	}

	@Override
	public String builderDataScopeSelf(String sql,Map<String,Object> param) {
		CCJSqlParserManager parserManager=  null;
		Select select = null;
		try {
			parserManager = new CCJSqlParserManager();
			select = (Select) parserManager.parse(new StringReader(sql));
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			Table fromItem = (Table) plain.getFromItem();
			// 如果没有配置值，直接过
			if(StringUtil.isEmpty(USER_ID_COLUMN)){
				return select.toString();
			}
//			${ALIAS_TABLE_NAME}.${USER_ID_COLUMN}='${CRTER}'
			String paramSql = String.format(DATA_SCOPE_SELF_SQL,
					USER_ID_COLUMN,param.get(USER_ID_COLUMN));
			String key  = String.valueOf(param.get(USER_ID_COLUMN));
			DataAuthHelper.getLocalList().add(new DataAuthParamValue(key, param.get(key)));

	        //有别名用别名，无别名用表名，防止字段冲突报错
	        String aliasTableName = fromItem.getAlias() == null ? fromItem.getName() : fromItem.getAlias().getName();
	        param.put("ALIAS_TABLE_NAME", aliasTableName);
	        StringSubstitutor sub = new StringSubstitutor(param);
	        String authSql = sub.replace(paramSql);
	        if (plain.getWhere() == null) {
	            plain.setWhere(CCJSqlParserUtil.parseCondExpression(authSql));
	        } else {
	            plain.setWhere(new AndExpression(plain.getWhere(), CCJSqlParserUtil.parseCondExpression(authSql)));
	        }
		} catch (Exception e) {
			throw new PageException(e);
		}
		return select.toString();
	}

	@Override
	public String builderDataScopeFullMatch(String sql, Map<String, Object> param) {
		CCJSqlParserManager parserManager=  null;
		Select select = null;
		try {
			parserManager = new CCJSqlParserManager();
			select = (Select) parserManager.parse(new StringReader(sql));
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			Table fromItem = (Table) plain.getFromItem();
			String fullMatchColumn = String.valueOf(param.get(FULL_MATCH_COLUMN));
			// 如果没有配置值，直接过
			if(StringUtil.isEmpty( fullMatchColumn)){
				return select.toString();
			}
//			${ALIAS_TABLE_NAME}.${COLUMN}='${VALUE1}'
			String[] columns = fullMatchColumn.split(",");
			StringBuffer paramSql= new StringBuffer();
			for(int i=0,len=columns.length;i<len;i++){
				paramSql.append(String.format(DATA_SCOPE_FULL_MATCH_SQL,columns[i],param.get(columns[i]) ));
				DataAuthHelper.getLocalList().add(new DataAuthParamValue(columns[i], param.get(columns[i])));
				if(i< len-1) {
					paramSql.append(" and  ");
				}
			}
			//有别名用别名，无别名用表名，防止字段冲突报错
			String aliasTableName = fromItem.getAlias() == null ? fromItem.getName() : fromItem.getAlias().getName();
			param.put("ALIAS_TABLE_NAME", aliasTableName);
			StringSubstitutor sub = new StringSubstitutor(param);
			String authSql = sub.replace(paramSql.toString());
			if (plain.getWhere() == null) {
				plain.setWhere(CCJSqlParserUtil.parseCondExpression(authSql));
			} else {
				plain.setWhere(new AndExpression(plain.getWhere(), CCJSqlParserUtil.parseCondExpression(authSql)));
			}
		} catch (Exception e) {
			throw new PageException(e);
		}
		return select.toString();
	}

	@Override
	public String builderDataScopeContainMatch(String sql, Map<String, Object> param) {
		CCJSqlParserManager parserManager=  null;
		Select select = null;
		try {
			parserManager = new CCJSqlParserManager();
			select = (Select) parserManager.parse(new StringReader(sql));
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			Table fromItem = (Table) plain.getFromItem();
			String containMatchColumn = String.valueOf(param.get(CONTAIN_MATCH_COLUMN));
			// 如果没有配置值，直接过
			if(StringUtil.isEmpty(containMatchColumn)){
				return select.toString();
			}
			//${ALIAS_TABLE_NAME}.${COLUMN} in ('${VALUE1}')
			String[] columns = containMatchColumn.split(",");

			StringBuffer paramSql= new StringBuffer();
			for(int i=0,len=columns.length;i<len;i++){
				Object obj = param.get(columns[i]);
				
				if(obj instanceof Collection<?> || obj.getClass().isArray()) {
					paramSql.append(String.format(DATA_SCOPE_CONTAIN_MATCH_SQL,columns[i],StringUtil.getPlaceholder(columns[i],obj)));
				}else {
					paramSql.append(String.format(DATA_SCOPE_CONTAIN_MATCH_SQL,columns[i],StringUtil.PLACE_HOLDER_STR));
				}
				DataAuthHelper.addDataAuthParamValue(columns[i], obj);
				if(i< len-1) {
					paramSql.append(" and  ");
				}
			}
			//有别名用别名，无别名用表名，防止字段冲突报错
			String aliasTableName = fromItem.getAlias() == null ? fromItem.getName() : fromItem.getAlias().getName();
			param.put("ALIAS_TABLE_NAME", aliasTableName);
			StringSubstitutor sub = new StringSubstitutor(param);
			String authSql = sub.replace(paramSql);
			if (plain.getWhere() == null) {
				plain.setWhere(CCJSqlParserUtil.parseCondExpression(authSql));
			} else {
				plain.setWhere(new AndExpression(plain.getWhere(), CCJSqlParserUtil.parseCondExpression(authSql)));
			}
		} catch (Exception e) {
			throw new PageException(e);
		}
		return select.toString();
	}




}
