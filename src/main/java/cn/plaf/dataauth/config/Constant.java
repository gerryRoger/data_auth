package cn.plaf.dataauth.config;

import java.util.Arrays;
import java.util.List;

public interface Constant {
	
	
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "DATA_SCOPE_ALL";
    
    
    /**
     * 统筹区权限
     */
    public static final String DATA_SCOPE_AREA = "DATA_SCOPE_AREA";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "DATA_SCOPE_CUSTOM";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "DATA_SCOPE_DEPT";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "DATA_SCOPE_DEPT_AND_CHILD";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "DATA_SCOPE_SELF";

    /**
     * 指定字段完全匹配
     */
    public static final String DATA_SCOPE_FULL_MATCH = "DATA_SCOPE_FULL_MATCH";
    /**
     * 指定字段包含匹配
     */
    public static final String DATA_SCOPE_CONTAIN_MATCH = "DATA_SCOPE_CONTAIN_MATCH";



    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";
    
    
    
    
    /**
     * 部门表名
     */
    public static final String SYS_DEPT_TABLE_NAME = "SYS_DEPT_TABLE_NAME";

    /**
     * 用户名字段
     */
    public static final String USER_ID_COLUMN = "USER_ID_COLUMN";

    /**
     * 部门字段
     */
    public static final String DEPT_ID_COLUMN = "DEPT_ID_COLUMN";
    
    /**
     * 上级部门字段
     */
    public static final String DEPT_UP_ID_COLUMN = "DEPT_UP_ID_COLUMN";
    
    
    /**
     * 角色字段
     */
    public static final String ROLE_ID_COLUMN = "ROLE_ID_COLUMN";
    
    /**
     * 角色部门表
     */
    public static final String SYS_ROLE_DEPT_TABLE_NAME = "SYS_ROLE_DEPT_TABLE_NAME";
    /**
     * 权限过滤方法列表 逗号分隔,前缀匹配 先过滤再拦截
     **/
    public static final String DATA_AUTH_EXT_METHODS = "DATA_AUTH_EXT_METHODS";
    /**
     * 权限拦截方法列表 逗号分隔,前缀匹配 先过滤再拦截
     **/
    public static final String DATA_AUTH_INTERCEPT_METHODS = "DATA_AUTH_INTERCEPT_METHODS";




    /**
     * 统筹区字段名称
     */
    public static final String AREA_COLUMN = "AREA_COLUMN";

    /**
     * 完全匹配字段名称集合 用英文逗号分隔
     */
    public static final String FULL_MATCH_COLUMN = "FULL_MATCH_COLUMN";

    /**
     * 包含字段名称集合 用英文逗号分隔
     */
    public static final String CONTAIN_MATCH_COLUMN = "CONTAIN_MATCH_COLUMN";

    /**
     * 数据权限过滤开关
     */
    public static final String DATA_AUTH_FLAG="dataAuthFlag";
    
    
    /**
     * 配置信息集合列表
     */
    public static final List<String> CONFIG_KEYS= Arrays.asList(new String[]{USER_ID_COLUMN,AREA_COLUMN,DEPT_ID_COLUMN,DEPT_UP_ID_COLUMN,ROLE_ID_COLUMN,FULL_MATCH_COLUMN,CONTAIN_MATCH_COLUMN,SYS_DEPT_TABLE_NAME,SYS_ROLE_DEPT_TABLE_NAME,DATA_AUTH_FLAG});

    
    //第一个分页参数
    public static final String PAGEPARAMETER_FIRST = "First_PageHelper" ;
    //第二个分页参数
    public static final String PAGEPARAMETER_SECOND = "Second_PageHelper" ;
    
    
    public static final String FRECH_FORMART="__frch_%s_%d";
    public static final String FRECH_START="__frch";
    
    
    
}
