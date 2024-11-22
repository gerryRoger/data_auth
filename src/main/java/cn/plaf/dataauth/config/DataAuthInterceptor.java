package cn.plaf.dataauth.config;

import cn.plaf.dataauth.param.auth.*;
import cn.plaf.dataauth.util.ObjectUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import cn.plaf.dataauth.Dialect;
import cn.plaf.dataauth.PageException;
import cn.plaf.dataauth.annotation.DataAuth;
import cn.plaf.dataauth.cache.Cache;
import cn.plaf.dataauth.cache.CacheFactory;
import cn.plaf.dataauth.param.DataParamHelper;
import cn.plaf.dataauth.util.ExecutorUtil;
import cn.plaf.dataauth.util.StringUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author gerryluo
 * @description 数据权限拦截器
 * @date 2022/3/13
 */
//mybatis 拦截顺序Executor -> StatementHandler->ParameterHandler->ResultSetHandler
//要在分页面插件之前完成sql语句的修改 应拦截Executor
@Intercepts({
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class }), })
public class DataAuthInterceptor implements Interceptor {

	private static final Log log = LogFactory.getLog(DataAuthInterceptor.class);

	private String default_dialect_class = "cn.plaf.dataauth.DataAuthHelper";
	private volatile Dialect dialect;

	private String DEFAULT_DATA_PARAM = "defaultDataParam";
	protected Cache<String, DataAuthDTO> msDataAuthMap = null;

	private DataParamHelper dataParamBuilder;

	/**
	 * 表名字段参数表
	 */
	private Map<String, Object> tableNameMap = new HashMap<String, Object>();

	/**
	 * 部门表名
	 */
	private String SYS_DEPT_TABLE_NAME = "sysDeptTableName";
	/**
	 * 用户名字段
	 */
	private String USER_ID_COLUMN = "userIdColumn";
	/**
	 * 部门字段
	 */
	private String DEPT_ID_COLUMN = "deptIdColumn";
	/**
	 * 上级部门字段
	 */
	private String DEPT_UP_ID_COLUMN = "deptUpIdColumn";
	/**
	 * 角色字段
	 */
	private String ROLE_ID_COLUMN = "roleIdColumn";
	/**
	 * 角色部门表
	 */
	private String SYS_ROLE_DEPT_TABLE_NAME = "sysRoleDeptTableName";

	/**
	 * 统筹区字段名称
	 */
	private String AREA_COLUMN = "areaColumn";

	/**
	 * 完全匹配字段名称集合 用英文逗号分隔
	 */
	private  String FULL_MATCH_COLUMN = "fullMatchColumn";

	/**
	 * 包含字段名称集合 用英文逗号分隔 containMatchColumn
	 */
	private  String CONTAIN_MATCH_COLUMN = "containMatchColumn";

	/**
	 * 统筹区字段名称
	 */
	private String DATA_AUTH_FLAG = "dataAuthFlag";

	/**
	 * @description: 权限过滤方法列表 逗号分隔,前缀匹配  先过滤 再拦截
	 * @date: 10:56 2023/8/2
	 * @param:
	 * @return:
	 **/
	private String DATA_AUTH_EXT_METHODS="dataAuthExtMethods";
	/**
	 * @description: 权限拦截方法列表 逗号分隔,前缀匹配，先过滤 再拦截
	 * @date: 10:56 2023/8/2
	 * @param:
	 * @return:
	 **/
	private String DATA_AUTH_INTERCEPT_METHODS="dataAuthInterceptMethods";



	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		try {
			// 获取拦截下的mapper
			Object[] args = invocation.getArgs();
			MappedStatement ms = (MappedStatement) args[0];
			Object parameter = args[1];
			RowBounds rowBounds = (RowBounds) args[2];
			ResultHandler resultHandler = (ResultHandler) args[3];
			Executor executor = (Executor) invocation.getTarget();
			CacheKey cacheKey;
			BoundSql boundSql;
			// 由于逻辑关系，只会进入一次
			if (args.length == 4) {
				// 4 个参数时
				boundSql = ms.getBoundSql(parameter);
				cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
			} else {
				// 6 个参数时
				cacheKey = (CacheKey) args[4];
				boundSql = (BoundSql) args[5];
			}
			checkDialectExists();
			// 权限校验开关
			boolean checkDataAuthFlag  = Boolean.valueOf(tableNameMap.get(Constant.DATA_AUTH_FLAG).toString()) ;
			if(!checkDataAuthFlag) {
				return invocation.proceed();
			}
			DataAuthDTO dataAuth = getMethodAnnation(ms);
			// 没有注解直接放行,可根据情况补充更多的业务逻辑
			if (dataAuth == null) {
				return invocation.proceed();
			}

			List<DataAuthType> list = new ArrayList<DataAuthType>();
			if (dataAuth.getDataAuthType()!=null) {
				DataAuthType[] dataAuthArr =  dataAuth.getDataAuthType();
				for(int i=0,len=dataAuthArr.length;i<len;i++) {
					list.add(dataAuthArr[i]);
				}

			}
			// 1、判断是否进行数据过滤 主要是校验数据过滤配置
			// 2、判断操作人员是否没有在数据权限范围内
			// 3、按照数据权限规则组装sql
			Map<String, Object> paramMap = dataParamBuilder.dealParamMap();
			paramMap.putAll(tableNameMap);
			changeTableColumn(dataAuth,paramMap);

			// 根据类型校验字段
			boolean checkParaFlag = true;
			// 分步校验数据
			for (DataAuthType dat : list) {
				checkParaFlag = checkParaFlag & checkParaMap(dat, paramMap);
			}
			if (!checkParaFlag) {
				return invocation.proceed();
			}
			// 加入表字段
			List<String> userIds = dataParamBuilder.initGobleExtUserIds();
			List<String> roleIds = dataParamBuilder.initGobleExtRoleIds();
			if (StringUtil.isNotEmpty(dataAuth.getExtUserId())) {
				String[] users = dataAuth.getExtUserId().split(",");
				userIds.addAll(Arrays.asList(users));
			}
			if (StringUtil.isNotEmpty(dataAuth.getExtRoleId())) {
				String[] roles = dataAuth.getExtRoleId().split(",");
				roleIds.addAll(Arrays.asList(roles));
			}
			if (!dialect.skip(paramMap, userIds, roleIds)) {
				return invocation.proceed();
			}
			List resultList = ExecutorUtil.dataAuthQuery(dialect, executor, ms, parameter, rowBounds, resultHandler,
					boundSql, cacheKey, paramMap, list);
			return resultList;
		} finally {
			if (dialect != null) {
				dialect.afterAll();
			}
		}
	}
	
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
	/**
	 * Spring bean 方式配置时，如果没有配置属性就不会执行下面的 setProperties 方法，就不会初始化
	 * <p>
	 * 因此这里会出现 null 的情况 fixed #26
	 */
	private void checkDialectExists() {
		if (dialect == null) {
			synchronized (default_dialect_class) {
				if (dialect == null) {
					setProperties(new Properties());
				}
			}
		}
	}

	/**
	 * 通过反射获取mapper方法是否加了数据拦截注解
	 */
	private DataAuthDTO getMethodAnnation(MappedStatement mappedStatement) throws ClassNotFoundException {
		DataAuthDTO dto = null;
		DataAuth dataAuth = null;
		String dataAuthId = mappedStatement.getId();
		String className = dataAuthId.substring(0, dataAuthId.lastIndexOf("."));
		String methodName = dataAuthId.substring(dataAuthId.lastIndexOf(".") + 1);
		
		// 读取缓存
		if (msDataAuthMap != null) {
			dto = msDataAuthMap.get(dataAuthId);
		}
		if(dto != null) {
			return dto;
		}
		// 校验方法是否在排除列表中
		if(checkMethodContainExtList(Constant.DATA_AUTH_EXT_METHODS,dataAuthId)){
			return null;
		}
		// 校验方法是否在包含的列表中
		if(!checkMethodContainExtList(Constant.DATA_AUTH_INTERCEPT_METHODS,dataAuthId)){
			return null;
		}
		// 读取传入值
		dto = dataParamBuilder.getSelectDataAuth(dataAuthId);
		if(dto!= null) {
			if(dto.getDataAuthType()== null ) {
				throw new PageException("dataAuthType 不能为空!");
			}
			msDataAuthMap.put(dataAuthId, dto);
			return dto;
		}

		// 读取方法注解
		if (dto == null) {
			final Class<?> cls = Class.forName(className);
			final Method[] methods = cls.getMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName) && method.isAnnotationPresent(DataAuth.class)) {
					dataAuth = method.getAnnotation(DataAuth.class);
					dto =new  DataAuthDTO();
					dto.setMethodAbsName(dataAuthId);
					dto.setDataAuthType(dataAuth.dataAuthType());
					dto.setExtRoleId(StringUtil.replace(dataAuth.extRoleId(), " ", ""));
					dto.setExtUserId(StringUtil.replace(dataAuth.extUserId(), " ", ""));
					msDataAuthMap.put(dataAuthId, dto);
					break;
				}
			}
		}
		if(dto != null) {
			if( dto.getDataAuthType()== null) {
				return null;
			}
		}
		return dto;
	}

	/**
	 * 根据类型校验传入参上
	 * 
	 * @param dataAuthType
	 * @param param
	 * @return
	 */
	private boolean checkParaMap(DataAuthType dataAuthType, Map<String, Object> param) {

		boolean checkFlag = true;
		switch (dataAuthType) {
		case DATA_SCOPE_ALL:
			break;
		case DATA_SCOPE_AREA:
			Object obj = param.get(Constant.AREA_COLUMN);
			Object value = param.get(String.valueOf(obj));
			if (ObjectUtil.isEmpty(value)) {
				checkFlag = false;
				log.trace("统筹区编码值不能为空!");
			}
			break;
		case DATA_SCOPE_CUSTOM:
			obj = param.get(Constant.ROLE_ID_COLUMN);
			value = param.get(String.valueOf(obj));
			if (ObjectUtil.isEmpty(value)) {
				checkFlag = false;
				log.trace("角色编码值不能为空!");
			}
			break;
		case DATA_SCOPE_CUSTOM_EXISTS:
			obj = param.get(Constant.DEPT_ID_COLUMN);
			value = param.get(String.valueOf(obj));
			if (ObjectUtil.isEmpty(value)) {
				checkFlag = false;
				log.trace("部门编码值不能为空!");
			}
			break;
		case DATA_SCOPE_DEPT:
			obj = param.get(Constant.DEPT_ID_COLUMN);
			value = param.get(String.valueOf(obj));
			if (ObjectUtil.isEmpty(value)) {
				checkFlag = false;
				log.trace("部门编码值不能为空!");
			}
			break;
		case DATA_SCOPE_DEPT_AND_CHILD:
			obj = param.get(Constant.DEPT_ID_COLUMN);
			value = param.get(String.valueOf(obj));
			if (ObjectUtil.isEmpty(value)) {
				checkFlag = false;
				log.trace("部门编码值不能为空!");
			}
			break;
		case DATA_SCOPE_SELF:
			obj = param.get(Constant.USER_ID_COLUMN);
			value = param.get(String.valueOf(obj));
			if (ObjectUtil.isEmpty(value)) {
				checkFlag = false;
				log.trace("用户值不能为空!");
			}
			break;
			case DATA_SCOPE_FULL_MATCH:
				String[] columns= String.valueOf(param.get(Constant.FULL_MATCH_COLUMN)).split(",");
				for(int i=0,len=columns.length;i<len;i++) {
					value = param.get(columns[i]);
					if (ObjectUtil.isEmpty(value)) {
						checkFlag = false;
						log.trace("完全匹配字符串不能为空!");
						break;
					}
				}
				break;
			case DATA_SCOPE_CONTAIN_MATCH:
				columns= String.valueOf(param.get(Constant.CONTAIN_MATCH_COLUMN)).split(",");
				for(int i=0,len=columns.length;i<len;i++) {
					value = param.get(columns[i]);
					if (ObjectUtil.isEmpty(value)) {
						checkFlag = false;
						log.trace("包含匹配字符串不能为空!");
						break;
					}
				}
				
			break;
		}
		return checkFlag;
	}

	/**
	 * @description: 运行时候动态修改表数据
	 * @date: 10:29 2023/7/25
	 * @param: [dataAuth, param]
	 * @return: void
	 **/
	private void changeTableColumn(DataAuthDTO dataAuth,Map<String,Object> param){
		//  统筹区权限
		if(dataAuth instanceof AreaDataAuthDTO){
			String areaColumn=((AreaDataAuthDTO) dataAuth).getAreaColumn();
			if(StringUtil.isNotEmpty(areaColumn)){
			   param.put(Constant.AREA_COLUMN,areaColumn);
			}
			return;
		}
		// 用户自定义IN
		if(dataAuth instanceof CustomDataAuthDTO){
			String deptIdColumn=((CustomDataAuthDTO) dataAuth).getDeptIdColumn();
			String  roleIdColumn = ((CustomDataAuthDTO) dataAuth).getRoleIdColumn();
			String  sysRoleDeptTableName = ((CustomDataAuthDTO) dataAuth).getSysRoleDeptTableName();
			if(StringUtil.isNotEmpty(deptIdColumn) &&   StringUtil.isNotEmpty(roleIdColumn) && StringUtil.isNotEmpty(sysRoleDeptTableName)  ){
				param.put(Constant.DEPT_ID_COLUMN,deptIdColumn);
				param.put(Constant.ROLE_ID_COLUMN,roleIdColumn);
				param.put(Constant.SYS_ROLE_DEPT_TABLE_NAME,sysRoleDeptTableName);
			}
			return;
		}
		// 用户自定义 Exists
		if(dataAuth instanceof CustomExistsDataAuthDTO){
			String deptIdColumn=((CustomExistsDataAuthDTO) dataAuth).getDeptIdColumn();
			String sysDeptTableName=((CustomExistsDataAuthDTO) dataAuth).getSysDeptTableName();
			if(StringUtil.isNotEmpty(deptIdColumn) && StringUtil.isNotEmpty(sysDeptTableName) ){
				param.put(Constant.DEPT_ID_COLUMN,deptIdColumn);
				param.put(Constant.SYS_DEPT_TABLE_NAME,sysDeptTableName);
			}
			return;
		}
		// 部门权限
		if(dataAuth instanceof DeptDataAuthDTO){
			String deptIdColumn=((DeptDataAuthDTO) dataAuth).getDeptIdColumn();
			if(StringUtil.isNotEmpty(deptIdColumn) ){
				param.put(Constant.DEPT_ID_COLUMN,deptIdColumn);
			}
			return;
		}
		// 自己查看
		if(dataAuth instanceof SelfDataAuthDTO){
			String userIdColumn=((SelfDataAuthDTO) dataAuth).getUserIdColumn();
			if(StringUtil.isNotEmpty(userIdColumn) ){
				param.put(Constant.USER_ID_COLUMN,userIdColumn);
			}
			return;
		}

	}

	/**
	 * @description: 校验方法是否在不拦截的类中
	 * @date: 11:10 2023/8/2
	 * @param: [absMethods]
	 * @return: boolean
	 **/
	private boolean checkMethodContainExtList(String key,String absMethods){
		Object obj = tableNameMap.get(key);
		boolean checkFlag =false;
		if(obj== null){
			return checkFlag;
		}
		String extMethods=  StringUtil.replace(String.valueOf(obj)," ","");
		List<String> list = Arrays.asList(extMethods.split(","));
		for(String str:list){
			if(absMethods.toUpperCase().startsWith(str)){
				checkFlag=true;
				break;
			}
		}
		return checkFlag;
	}



	@Override
	public void setProperties(Properties properties) {

		msDataAuthMap = CacheFactory.createCache(properties.getProperty("msDataAuthCache"), "msData", properties);
		String dialectClass = properties.getProperty("dialect");
		String dataparamClass = properties.getProperty(DEFAULT_DATA_PARAM);
		// 设置表相关参数
		String userIdColumn = properties.getProperty(USER_ID_COLUMN);
		String areaColumn = properties.getProperty(AREA_COLUMN);
		String deptIdColumn = properties.getProperty(DEPT_ID_COLUMN);
		String deptUpIdColumn = properties.getProperty(DEPT_UP_ID_COLUMN);
		String roleIdColumn = properties.getProperty(ROLE_ID_COLUMN);
		String fullMatchColumn = properties.getProperty(FULL_MATCH_COLUMN);
		String containMatchColumn = properties.getProperty(CONTAIN_MATCH_COLUMN);
		String sysDeptTableName = properties.getProperty(SYS_DEPT_TABLE_NAME);
		String sysRoleDeptTableName = properties.getProperty(SYS_ROLE_DEPT_TABLE_NAME);
		String dataAuthExtMethods =properties.getProperty(DATA_AUTH_EXT_METHODS);
		String dataAuthInterceptMethods =properties.getProperty(DATA_AUTH_INTERCEPT_METHODS);
		if(StringUtil.isNotEmpty(userIdColumn)){
			tableNameMap.put(Constant.USER_ID_COLUMN, userIdColumn.toUpperCase());
		}
		if(StringUtil.isNotEmpty(areaColumn)){
			tableNameMap.put(Constant.AREA_COLUMN, areaColumn.toUpperCase());
		}
		if(StringUtil.isNotEmpty(deptIdColumn)){
			tableNameMap.put(Constant.DEPT_ID_COLUMN, deptIdColumn.toUpperCase());
		}
		if(StringUtil.isNotEmpty(deptUpIdColumn)){
			tableNameMap.put(Constant.DEPT_UP_ID_COLUMN, deptUpIdColumn.toUpperCase());
		}
		if(StringUtil.isNotEmpty(roleIdColumn)){
			tableNameMap.put(Constant.ROLE_ID_COLUMN, roleIdColumn.toUpperCase());
		}
		if(StringUtil.isNotEmpty(fullMatchColumn)){
			tableNameMap.put(Constant.FULL_MATCH_COLUMN, fullMatchColumn.toUpperCase());
		}
		if(StringUtil.isNotEmpty(containMatchColumn)){
			tableNameMap.put(Constant.CONTAIN_MATCH_COLUMN, containMatchColumn.toUpperCase());
		}
		if(StringUtil.isNotEmpty(sysDeptTableName)){
			tableNameMap.put(Constant.SYS_DEPT_TABLE_NAME, sysDeptTableName.toUpperCase());
		}
		if(StringUtil.isNotEmpty(sysRoleDeptTableName)){
			tableNameMap.put(Constant.SYS_ROLE_DEPT_TABLE_NAME, sysRoleDeptTableName.toUpperCase());
		}
		if(StringUtil.isNotEmpty(dataAuthExtMethods)){
			tableNameMap.put(Constant.DATA_AUTH_EXT_METHODS, dataAuthExtMethods.toUpperCase());
		}
		if(StringUtil.isNotEmpty(dataAuthInterceptMethods)){
			tableNameMap.put(Constant.DATA_AUTH_INTERCEPT_METHODS, dataAuthInterceptMethods.toUpperCase());
		}


		if (properties.getProperty(DATA_AUTH_FLAG) == null) {
			tableNameMap.put(Constant.DATA_AUTH_FLAG, true);
		} else {
			tableNameMap.put(Constant.DATA_AUTH_FLAG, properties.getProperty(DATA_AUTH_FLAG));
		}

		if (StringUtil.isEmpty(dialectClass)) {
			dialectClass = default_dialect_class;
		}
		try {
			Class<?> aClass = Class.forName(dialectClass);
			dialect = (Dialect) aClass.newInstance();
		} catch (Exception e) {
			throw new PageException("加载[" + dialectClass + "]时出错" + e.getMessage(), e);
		}
		try {
			Class<?> dataClass = Class.forName(dataparamClass);
			dataParamBuilder = (DataParamHelper) dataClass.newInstance();
		} catch (Exception e) {
			throw new PageException("加载[" + dataparamClass + "]时出错" + e.getMessage(), e);
		}

		dialect.setProperties(properties);
	}

}
