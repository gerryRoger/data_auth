package cn.plaf.dataauth.param.auth;/**
 * @Package cn.plaf.dataauth.param
 * @Title:
 * @Description: 个人权限
 * @author gerryluo
 * @version V1.0
 */

public class SelfDataAuthDTO extends DataAuthDTO {
    private String userIdColumn;
    public String getUserIdColumn() {
        return userIdColumn;
    }

    public void setUserIdColumn(String userIdColumn) {
        this.userIdColumn = userIdColumn;
    }
}
