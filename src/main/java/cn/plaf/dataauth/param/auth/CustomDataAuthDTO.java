package cn.plaf.dataauth.param.auth;

/**
 * @Package cn.plaf.dataauth.param
 * @Title:
 * @Description:用户自定义数据
 * @author gerryluo
 * @version V1.0
 */
public class CustomDataAuthDTO extends DataAuthDTO {
    private String deptIdColumn;
    private String roleIdColumn;
    private String sysRoleDeptTableName;
    public String getDeptIdColumn() {
        return deptIdColumn;
    }

    public void setDeptIdColumn(String deptIdColumn) {
        this.deptIdColumn = deptIdColumn;
    }

    public String getRoleIdColumn() {
        return roleIdColumn;
    }

    public void setRoleIdColumn(String roleIdColumn) {
        this.roleIdColumn = roleIdColumn;
    }

    public String getSysRoleDeptTableName() {
        return sysRoleDeptTableName;
    }

    public void setSysRoleDeptTableName(String sysRoleDeptTableName) {
        this.sysRoleDeptTableName = sysRoleDeptTableName;
    }

}
