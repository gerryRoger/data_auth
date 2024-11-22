package cn.plaf.dataauth.param.auth;

/**
 * @Package cn.plaf.dataauth.param
 * @Title:
 * @Description: 统筹区权限控制
 * @author gerryluo
 * @version V1.0
 */

public class AreaDataAuthDTO extends DataAuthDTO {
    private String areaColumn;

    public String getAreaColumn() {
        return areaColumn;
    }

    public void setAreaColumn(String areaColumn) {
        this.areaColumn = areaColumn;
    }


}
