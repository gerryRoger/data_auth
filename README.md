
# mybatis 数据过滤插件

mybatis 数据过滤插件
如果你也在用 MyBatis，建议尝试该数据过滤插件，目前插件支持数据库为mysql,oracle,其他数据库还没做拓展。

## 安装

如果你使用 Maven，你只需要在 pom.xml 中添加下面的依赖：
xml配置  



```yaml
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-text</artifactId>
	<version>1.8</version>
</dependency>
 <dependency>
    <groupId>cn.plaf.dataauthhelper</groupId>
    <artifactId>dataauthhelper</artifactId>
    <version>1.0.0</version>
</dependency>

```

    
## Demo
前提：需要数据过滤的表必须有冗余以下标记字段：
userIdColumn：创建用户字段名称、
areaColumn：统筹区字段名称
deptIdColumn：部门id字段名称
deptUpIdColumn：上级部门id字段名称
roleIdColumn： 角色id字段名称
sysDeptTableName：部门表名称
sysRoleDeptTableName：角色部门表名称
fullMatchColumn：完全匹配字段名称如 A.column=?，可以是多个字段 用逗号分割，注意配置的字段必须是表中存在
containMatchColumn：包含匹配字段名称，如A.column in (?)，可以是多个字段 用逗号分割，注意配置的字段必须是表中存
dataAuthInterceptMethods:包含方法路径列表 逗号分隔，只处理配置的路径类，如果为空则拦截全部查询
dataAuthExtMethods: 过滤方法列表，包含类不拦截，直接跳过
# 一、配置
    <plugins>
    <!--  
        <plugin interceptor="com.github.pagehelper.PageInterceptor">
            <property name="helperDialect" value="mysql"/>
        </plugin>-->
        <plugin interceptor="cn.plaf.dataauth.config.DataAuthInterceptor">
            <!-- 支持通过Mapper接口参数来传递分页参数 -->
            <property name="helperDialect" value="mysql"/>
			<!-- 权限参数定义-->
            <property name="defaultDataParam" value="cn.plaf.dataauth.param.helper.DataParamBuilder"/>
            <!-- 创建用户字段名称 -->
            <property name="userIdColumn" value="crter"/>
            <!-- 统筹区字段名称 -->
            <property name="areaColumn" value="poolarea"/>
            <!-- 部门id字段名称 -->
            <property name="deptIdColumn" value="dept_id"/>
            <!-- 上级部门id字段名称 -->
            <property name="deptUpIdColumn" value="dept_up_id"/>            
            <!-- 角色id字段名称 -->
            <property name="roleIdColumn" value="role_id"/>
            <!-- 创建用户字段名称 -->
            <property name="userIdColumn" value="crter"/>
            <!-- 完全匹配字段名称 -->
            <property name="fullMatchColumn" value="poolarea,crter"/>
            <!-- 包含匹配创建用户字段名称 -->
            <property name="containMatchColumn" value="poolarea,crter"/>
            <!-- 部门表名称 -->
            <property name="sysDeptTableName" value="sys_dept"/>
            <!-- 角色部门表名称 -->
            <property name="sysRoleDeptTableName" value="sys_role_dept"/>
            <!-- 包含方法路径列表 逗号分隔，只处理配置的路径类，如果为空则拦截全部查询 -->
            <property name="dataAuthInterceptMethods" value="cn.plaf.dataauth"/>
            <!-- 过滤方法列表，包含类不拦截，直接跳过-->
            <property name="dataAuthExtMethods" value="cn.plaf.dataauth1"/>			
             <!-- 数据权限过滤开关 true 打开 false 关闭 -->
            <property name="dataAuthFlag" value="true"/>              
        </plugin>
    </plugins>
**spring xml文件配置**

```
                <bean class="cn.plaf.dataauth.config.DataAuthInterceptor">
                    <property name="properties">
                        <value>
                            dataAuthFlag=true
                            helperDialect=mysql
                            defaultDataParam=cn.plaf.dataauth.param.helper.DataParamBuilder
                            userIdColumn=crter
                            areaColumn=poolarea
                            deptIdColumn=dept_id
                            dataAuthInterceptMethods=cn.plaf.dataauth
                            dataAuthExtMethods=cn.plaf.dataauth1
                        </value>
                    </property>
                </bean>
```


#二 业务传参类



```
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
		map.put("poolarea", "430900");
		map.put("crter", "3,4,5");
		map.put("dept_id", "3");
		map.put("role_id", "3");
		return map;
	}



	@Override
	public List<String> initGobleExtUserIds() {
		return Arrays.asList(GOBLE_EXT_USER_IDS.split(","));
	}

	@Override
	public List<String> initGobleExtRoleIds() {
		 return Arrays.asList(GOBLE_EXT_ROLE_IDS.split(","));
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
//		DataAuthType[] dataAuthType=new DataAuthType[]{DataAuthType.DATA_SCOPE_AREA,
//				DataAuthType.DATA_SCOPE_CUSTOM,DataAuthType.DATA_SCOPE_DEPT,
//				DataAuthType.DATA_SCOPE_DEPT_AND_CHILD,DataAuthType.DATA_SCOPE_SELF,DataAuthType.DATA_SCOPE_FULL_MATCH,
//				DataAuthType.DATA_SCOPE_CONTAIN_MATCH};
		DataAuthType[] dataAuthType=new DataAuthType[]{DataAuthType.DATA_SCOPE_AREA};
		da.setDataAuthType(dataAuthType);
		da.setAreaColumn("DEPT_ID");
		return da;
	}

}
```



# 三、Dao层注解

DataAuth有三个属性 
dataAuthType(过滤类别，以下类别可以叠加 按照顺序依次添加条件)：

	/**
	 * 全部权限 代码:1 
	 */
	DATA_SCOPE_ALL("1","全部权限","data_scope_all"),
	/**
	 * 统筹区权限 代码:2
	 * 末尾添加条件 ${ALIAS_TABLE_NAME}.${AREA_COLUMN}= ? 
	 */
	DATA_SCOPE_AREA("2","统筹区权限","data_scope_area"),
	/*
	 *
	 * 自定义权限IN 代码:3
	 * 末尾添加条件 ${ALIAS_TABLE_NAME}.${DEPT_ID_COLUMN} IN ( SELECT ${DEPT_ID_COLUMN} FROM ${SYS_ROLE_DEPT_TABLE_NAME} WHERE ${ROLE_ID_COLUMN} = '${ROLE_ID}' ) 
	 */
	DATA_SCOPE_CUSTOM("3","自定义权限IN","data_scope_custom"),
	/**
	 * 自定义权限Exists 代码:4
	 * 末尾添加条件 "  EXISTS ( SELECT ${DEPT_ID_COLUMN} FROM ${SYS_ROLE_DEPT_TABLE_NAME} WHERE ${ALIAS_TABLE_NAME}.${DEPT_ID_COLUMN}= ${DEPT_ID_COLUMN} AND   ${ROLE_ID_COLUMN} = '${ROLE_ID}' ) " 
	 */
	DATA_SCOPE_CUSTOM_EXISTS("4","自定义权限EXISTS","data_scope_custom_exists"),
	/**
	 * 本部门权限 代码:5
	 * 末尾添加条件  ${ALIAS_TABLE_NAME}.${DEPT_ID_COL}='${DEPT_ID}'
	 */
	DATA_SCOPE_DEPT("5","本部门权限","data_scope_dept"),
	/**
	 * 部门及以下数据权限 代码:6
	 * 末尾添加条件 ${ALIAS_TABLE_NAME}.${DEPT_ID_COL} in ( select ${DEPT_ID_COL} from ${DEPT_TABLE_NAME} where ${DEPT_ID_COL} ='${DEPT_ID}'  or  find_in_set(${DEPT_ID_COL},${DEPT_UP_ID_COL}) )

	 */
	DATA_SCOPE_DEPT_AND_CHILD("6","部门及以下数据权限","data_scope_dept_and_child"),
	/**
	 * 仅本人数据权限 代码:7
	 * 末尾添加条件 ${ALIAS_TABLE_NAME}.${USER_ID_COLUMN}='${CRTER}'
	 */
	DATA_SCOPE_SELF("7","仅本人数据权限","data_scope_self"),

	/**
	 * 指定字段精确匹配  column1=value1 and clolumn2=value2 代码:8
	 * 末尾添加条件 ${ALIAS_TABLE_NAME}.${COLUMN}='${value1}'
	 */
	DATA_SCOPE_FULL_MATCH("8","指定字段完全匹配","data_scope_full_match"),
	/**
	 * 指定字段包含匹配  column1 in (value1_1,value1_2) and clolumn2 in (value2_2 ,value2_2) 代码:9
	 * 末尾添加条件 包含 匹配 ${ALIAS_TABLE_NAME}.${COLUMN} in ('${value1}')
	 */
	DATA_SCOPE_CONTAIN_MATCH("9","指定字段包含匹配","data_scope_contain_match")
extUserId(非权限控制用户列表): 多用户用英文逗号分隔
extRoleId(非权限控制角色列表): 多角色用英文逗号分隔





```
import java.util.List;		
import org.apache.ibatis.annotations.Param;		
import cn.plaf.dataauth.annotation.DataAuth;		
public interface ArchDao {		
	@DataAuth(dataAuthType= {DataAuthType.DATA_SCOPE_AREA,DataAuthType.DATA_SCOPE_CUSTOM}) /** 业务只需加入此配置即可**/
    List<ArchDO> getArchList(@Param("archDTO") ArchDO archDO);
}
```





## 致谢

 - [MyBatis 分页插件 - PageHelper（5.3.1）](https://apidoc.gitee.com/free/Mybatis_PageHelper)

