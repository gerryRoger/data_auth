package cn.plaf.dataauth.param.auth;

/**
 * @Package cn.plaf.dataauth.param
 * @Title:
 * @Description: 部门权限
 * @author luoboy
 * @date 2023/7/25 9:18
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
