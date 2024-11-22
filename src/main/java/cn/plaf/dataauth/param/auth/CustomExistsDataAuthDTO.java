package cn.plaf.dataauth.param.auth;/**
 * @Package cn.plaf.dataauth.param.auth
 * @Title:
 * @Description: 自定义 添加Exists 条件
 * @author luoboy
 * @date 2023/7/25 9:43
 * @version V1.0
 */

public class CustomExistsDataAuthDTO extends DataAuthDTO{

    private String deptIdColumn;
    private String sysDeptTableName;
    public String getDeptIdColumn() {
        return deptIdColumn;
    }

    public void setDeptIdColumn(String deptIdColumn) {
        this.deptIdColumn = deptIdColumn;
    }

    public String getSysDeptTableName() {
        return sysDeptTableName;
    }

    public void setSysDeptTableName(String sysDeptTableName) {
        this.sysDeptTableName = sysDeptTableName;
    }
}
