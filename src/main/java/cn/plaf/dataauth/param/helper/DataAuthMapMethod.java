package cn.plaf.dataauth.param.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.plaf.dataauth.config.Constant;
import cn.plaf.dataauth.util.StringUtil;



/**
 * 获取业务权限传参信息
 *
 */
public abstract class DataAuthMapMethod {
    protected static final ThreadLocal<List<DataAuthParamValue>> LOCAL_DATA_AUTH = new ThreadLocal<List<DataAuthParamValue>>();
    
    protected static boolean DATA_AUTH_FLAG = true;
    
    
    
    
    public static void startDataAuth() {
    	
    }
    
    /**
     * 设置  参数
     *
     * @param page
     */
    public static void setLocalList(List<DataAuthParamValue> list) {
    	LOCAL_DATA_AUTH.set(list);
    }
    
    /**
     * 获取  参数
     *
     * @return
     */
    public static List<DataAuthParamValue> getLocalList() {
        return LOCAL_DATA_AUTH.get();
    }
    
    /**
     * 移除本地变量
     */
    public static void clearLocalList() {
    	LOCAL_DATA_AUTH.remove();
    }
    
    
    
    public static void addDataAuthParamValue(String name,Object obj){
    	if(StringUtil.isEmpty(obj)) {
    		return ;
    	}
    	List<DataAuthParamValue> list = getLocalList();
    	List lst= null;
    	if(obj instanceof Collection<?> ) {
    		lst= (List) obj;
    	} 
    	if(obj.getClass().isArray()) {
    		lst = Arrays.asList(obj);
    	}
    	if(lst != null) {
    		for(int i=0,len=lst.size();i<len;i++) {
    			list.add( new DataAuthParamValue(String.format(Constant.FRECH_FORMART, name.toLowerCase(),i),lst.get(i)));
    		}
    	}else {
			list.add( new DataAuthParamValue(name,obj));
    	}
    }
    
    
    
    /**
     * 设置参数
     *
     * @param properties 插件属性
     */
    protected static void setStaticProperties(Properties properties){
        //defaultCount，这是一个全局生效的参数，多数据源时也是统一的行为
        if(properties != null){
            DATA_AUTH_FLAG = Boolean.valueOf(properties.getProperty("dataAuthFlag", "true"));
        }
    }
    

}
