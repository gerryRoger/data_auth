package cn.plaf.dataauth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.plaf.dataauth.config.DataAuthType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataAuth
{
	/**
	 * 类型类别
	 * @return
	 */
	public DataAuthType[] dataAuthType() default {};
    
    /**
     *  非权限控制用户列表
     * @return
     */
    public String extUserId() default "";
    
    /**
     *  非权限控制角色列表
     * @return
     */
    public String extRoleId() default "";
    
}
