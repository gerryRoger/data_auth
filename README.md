
# mybatis ���ݹ��˲��

mybatis ���ݹ��˲��
�����Ҳ���� MyBatis�����鳢�Ը����ݹ��˲����Ŀǰ���֧�����ݿ�Ϊmysql,oracle,�������ݿ⻹û����չ��

## ��װ

�����ʹ�� Maven����ֻ��Ҫ�� pom.xml ����������������
xml����  



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
ǰ�᣺��Ҫ���ݹ��˵ı�������������±���ֶΣ�
userIdColumn�������û��ֶ����ơ�
areaColumn��ͳ�����ֶ�����
deptIdColumn������id�ֶ�����
deptUpIdColumn���ϼ�����id�ֶ�����
roleIdColumn�� ��ɫid�ֶ�����
sysDeptTableName�����ű�����
sysRoleDeptTableName����ɫ���ű�����
fullMatchColumn����ȫƥ���ֶ������� A.column=?�������Ƕ���ֶ� �ö��ŷָע�����õ��ֶα����Ǳ��д���
containMatchColumn������ƥ���ֶ����ƣ���A.column in (?)�������Ƕ���ֶ� �ö��ŷָע�����õ��ֶα����Ǳ��д�
dataAuthInterceptMethods:��������·���б� ���ŷָ���ֻ�������õ�·���࣬���Ϊ��������ȫ����ѯ
dataAuthExtMethods: ���˷����б������಻���أ�ֱ������
# һ������
    <plugins>
    <!--  
        <plugin interceptor="com.github.pagehelper.PageInterceptor">
            <property name="helperDialect" value="mysql"/>
        </plugin>-->
        <plugin interceptor="cn.plaf.dataauth.config.DataAuthInterceptor">
            <!-- ֧��ͨ��Mapper�ӿڲ��������ݷ�ҳ���� -->
            <property name="helperDialect" value="mysql"/>
			<!-- Ȩ�޲�������-->
            <property name="defaultDataParam" value="cn.plaf.dataauth.param.helper.DataParamBuilder"/>
            <!-- �����û��ֶ����� -->
            <property name="userIdColumn" value="crter"/>
            <!-- ͳ�����ֶ����� -->
            <property name="areaColumn" value="poolarea"/>
            <!-- ����id�ֶ����� -->
            <property name="deptIdColumn" value="dept_id"/>
            <!-- �ϼ�����id�ֶ����� -->
            <property name="deptUpIdColumn" value="dept_up_id"/>            
            <!-- ��ɫid�ֶ����� -->
            <property name="roleIdColumn" value="role_id"/>
            <!-- �����û��ֶ����� -->
            <property name="userIdColumn" value="crter"/>
            <!-- ��ȫƥ���ֶ����� -->
            <property name="fullMatchColumn" value="poolarea,crter"/>
            <!-- ����ƥ�䴴���û��ֶ����� -->
            <property name="containMatchColumn" value="poolarea,crter"/>
            <!-- ���ű����� -->
            <property name="sysDeptTableName" value="sys_dept"/>
            <!-- ��ɫ���ű����� -->
            <property name="sysRoleDeptTableName" value="sys_role_dept"/>
            <!-- ��������·���б� ���ŷָ���ֻ�������õ�·���࣬���Ϊ��������ȫ����ѯ -->
            <property name="dataAuthInterceptMethods" value="cn.plaf.dataauth"/>
            <!-- ���˷����б������಻���أ�ֱ������-->
            <property name="dataAuthExtMethods" value="cn.plaf.dataauth1"/>			
             <!-- ����Ȩ�޹��˿��� true �� false �ر� -->
            <property name="dataAuthFlag" value="true"/>              
        </plugin>
    </plugins>
**spring xml�ļ�����**

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


#�� ҵ�񴫲���



```
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.plaf.dataauth.config.DataAuthType;
import cn.plaf.dataauth.param.auth.AreaDataAuthDTO;
import cn.plaf.dataauth.param.auth.DataAuthDTO;

/**
 * 1���˴���д��ҵ�������
 * @author powersi
 *
 */
public class DataParamBuilder extends AbstractDataParamHelper {
	/**
	 * ȫ�� ��������Ȩ���˻�
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
	 * 1����ҵ������ж�ȡȨ�޲��� ���ȼ�����ҵ�� ��ע��
	 * 2���û����Ը��� plugin interceptor ���õı��ֶκͱ����ƣ������ֶ���ע���д�Сд���� �����¼�����
	 * 	a�������ѯ����  AreaDataAuthDTO ��DataAuthType=DATA_SCOPE_AREA
	 * 	b���Զ���in CustomDataAuthDTO,DataAuthType.DATA_SCOPE_CUSTOM
	 * 	c���Զ���exists CustomExistsDataAuthDTO, DataAuthType.DATA_SCOPE_CUSTOM_EXISTS
	 * 	d������  DeptDataAuthDTO��   DataAuthType.DATA_SCOPE_DEPT
	 * 	e�������˲�ѯ SelfDataAuthDTO,DataAuthType.DATA_SCOPE_SELF
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



# ����Dao��ע��

DataAuth���������� 
dataAuthType(����������������Ե��� ����˳�������������)��

	/**
	 * ȫ��Ȩ�� ����:1 
	 */
	DATA_SCOPE_ALL("1","ȫ��Ȩ��","data_scope_all"),
	/**
	 * ͳ����Ȩ�� ����:2
	 * ĩβ������� ${ALIAS_TABLE_NAME}.${AREA_COLUMN}= ? 
	 */
	DATA_SCOPE_AREA("2","ͳ����Ȩ��","data_scope_area"),
	/*
	 *
	 * �Զ���Ȩ��IN ����:3
	 * ĩβ������� ${ALIAS_TABLE_NAME}.${DEPT_ID_COLUMN} IN ( SELECT ${DEPT_ID_COLUMN} FROM ${SYS_ROLE_DEPT_TABLE_NAME} WHERE ${ROLE_ID_COLUMN} = '${ROLE_ID}' ) 
	 */
	DATA_SCOPE_CUSTOM("3","�Զ���Ȩ��IN","data_scope_custom"),
	/**
	 * �Զ���Ȩ��Exists ����:4
	 * ĩβ������� "  EXISTS ( SELECT ${DEPT_ID_COLUMN} FROM ${SYS_ROLE_DEPT_TABLE_NAME} WHERE ${ALIAS_TABLE_NAME}.${DEPT_ID_COLUMN}= ${DEPT_ID_COLUMN} AND   ${ROLE_ID_COLUMN} = '${ROLE_ID}' ) " 
	 */
	DATA_SCOPE_CUSTOM_EXISTS("4","�Զ���Ȩ��EXISTS","data_scope_custom_exists"),
	/**
	 * ������Ȩ�� ����:5
	 * ĩβ�������  ${ALIAS_TABLE_NAME}.${DEPT_ID_COL}='${DEPT_ID}'
	 */
	DATA_SCOPE_DEPT("5","������Ȩ��","data_scope_dept"),
	/**
	 * ���ż���������Ȩ�� ����:6
	 * ĩβ������� ${ALIAS_TABLE_NAME}.${DEPT_ID_COL} in ( select ${DEPT_ID_COL} from ${DEPT_TABLE_NAME} where ${DEPT_ID_COL} ='${DEPT_ID}'  or  find_in_set(${DEPT_ID_COL},${DEPT_UP_ID_COL}) )

	 */
	DATA_SCOPE_DEPT_AND_CHILD("6","���ż���������Ȩ��","data_scope_dept_and_child"),
	/**
	 * ����������Ȩ�� ����:7
	 * ĩβ������� ${ALIAS_TABLE_NAME}.${USER_ID_COLUMN}='${CRTER}'
	 */
	DATA_SCOPE_SELF("7","����������Ȩ��","data_scope_self"),

	/**
	 * ָ���ֶξ�ȷƥ��  column1=value1 and clolumn2=value2 ����:8
	 * ĩβ������� ${ALIAS_TABLE_NAME}.${COLUMN}='${value1}'
	 */
	DATA_SCOPE_FULL_MATCH("8","ָ���ֶ���ȫƥ��","data_scope_full_match"),
	/**
	 * ָ���ֶΰ���ƥ��  column1 in (value1_1,value1_2) and clolumn2 in (value2_2 ,value2_2) ����:9
	 * ĩβ������� ���� ƥ�� ${ALIAS_TABLE_NAME}.${COLUMN} in ('${value1}')
	 */
	DATA_SCOPE_CONTAIN_MATCH("9","ָ���ֶΰ���ƥ��","data_scope_contain_match")
extUserId(��Ȩ�޿����û��б�): ���û���Ӣ�Ķ��ŷָ�
extRoleId(��Ȩ�޿��ƽ�ɫ�б�): ���ɫ��Ӣ�Ķ��ŷָ�





```
import java.util.List;		
import org.apache.ibatis.annotations.Param;		
import cn.plaf.dataauth.annotation.DataAuth;		
public interface ArchDao {		
	@DataAuth(dataAuthType= {DataAuthType.DATA_SCOPE_AREA,DataAuthType.DATA_SCOPE_CUSTOM}) /** ҵ��ֻ���������ü���**/
    List<ArchDO> getArchList(@Param("archDTO") ArchDO archDO);
}
```





## ��л

 - [MyBatis ��ҳ��� - PageHelper��5.3.1��](https://apidoc.gitee.com/free/Mybatis_PageHelper)

