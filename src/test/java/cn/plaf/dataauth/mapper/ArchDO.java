package cn.plaf.dataauth.mapper;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


@TableName("ARCH_B")
public class ArchDO implements Serializable{
    private static final long serialVersionUID=1L;
    @TableId
	private Long archNo; // 档案编号 db_column: ARCH_NO
	private String archName; // 档案名称 db_column: ARCH_NAME
	private Integer deptId; // 部门名称
	
	private String crter;// 创建人
	private String poolarea;// 统筹区
	
	@TableField(exist = false)
	private List<Integer> deptIds;
	public List<Integer> getDeptIds() {
		return deptIds;
	}
	public void setDeptIds(List<Integer> deptIds) {
		this.deptIds = deptIds;
	}
	public Long getArchNo() {
		return archNo;
	}
	public void setArchNo(Long archNo) {
		this.archNo = archNo;
	}
	public String getArchName() {
		return archName;
	}
	public void setArchName(String archName) {
		this.archName = archName;
	}
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public String getCrter() {
		return crter;
	}
	public void setCrter(String crter) {
		this.crter = crter;
	}
	public String getPoolarea() {
		return poolarea;
	}
	public void setPoolarea(String poolarea) {
		this.poolarea = poolarea;
	}



	
	
	
	





}



