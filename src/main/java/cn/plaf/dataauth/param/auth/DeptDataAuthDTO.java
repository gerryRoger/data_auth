package cn.plaf.dataauth.param.auth;

/**
 * @Package cn.plaf.dataauth.param
 * @Title:
 * @Description: 部门权限
 * @author gerryluo
 * @version V1.0
 */


public class DeptDataAuthDTO extends DataAuthDTO {
    private String deptIdColumn;
    public String getDeptIdColumn() {
        return deptIdColumn;
    }

    public void setDeptIdColumn(String deptIdColumn) {
        this.deptIdColumn = deptIdColumn;
    }
}
