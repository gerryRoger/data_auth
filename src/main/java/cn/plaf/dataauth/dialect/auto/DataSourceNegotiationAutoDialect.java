package cn.plaf.dataauth.dialect.auto;

import org.apache.ibatis.mapping.MappedStatement;

import cn.plaf.dataauth.AutoDialect;
import cn.plaf.dataauth.dialect.AbstractHelperDialect;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 *
 * @author gerryluo
 */
public class DataSourceNegotiationAutoDialect implements AutoDialect<String> {
    private static final List<DataSourceAutoDialect> AUTO_DIALECTS = new ArrayList<DataSourceAutoDialect>();
    private Map<String, DataSourceAutoDialect> urlMap = new ConcurrentHashMap<String, DataSourceAutoDialect>();

    static {
        //创建时，初始化所有实现，当依赖的连接池不存在时，这里不会添加成功，所以理论上这里包含的内容不会多，执行时不会迭代多次
        try {
            AUTO_DIALECTS.add(new HikariAutoDialect());
        } catch (Exception ignore) {
        }
        try {
            AUTO_DIALECTS.add(new DruidAutoDialect());
        } catch (Exception ignore) {
        }
        try {
            AUTO_DIALECTS.add(new TomcatAutoDialect());
        } catch (Exception ignore) {
        }
        try {
            AUTO_DIALECTS.add(new C3P0AutoDialect());
        } catch (Exception ignore) {
        }
        try {
            AUTO_DIALECTS.add(new DbcpAutoDialect());
        } catch (Exception ignore) {
        }
    }

    /**
     * 允许手工添加额外的实现，实际上没有必要
     *
     * @param autoDialect
     */
    public static void registerAutoDialect(DataSourceAutoDialect autoDialect) {
        AUTO_DIALECTS.add(autoDialect);
    }

    @Override
    public String extractDialectKey(MappedStatement ms, DataSource dataSource, Properties properties) {
        for (DataSourceAutoDialect autoDialect : AUTO_DIALECTS) {
            String dialectKey = autoDialect.extractDialectKey(ms, dataSource, properties);
            if (dialectKey != null) {
                if (!urlMap.containsKey(dialectKey)) {
                    urlMap.put(dialectKey, autoDialect);
                }
                return dialectKey;
            }
        }
      //都不匹配的时候使用默认方法
        return DefaultAutoDialect.DEFAULT.extractDialectKey(ms, dataSource, properties);
    }

    @Override
    public AbstractHelperDialect extractDialect(String dialectKey, MappedStatement ms, DataSource dataSource, Properties properties) {
        if (urlMap.containsKey(dialectKey)) {
            return urlMap.get(dialectKey).extractDialect(dialectKey, ms, dataSource, properties);
        }
        //都不匹配的时候使用默认方法
        return DefaultAutoDialect.DEFAULT.extractDialect(dialectKey, ms, dataSource, properties);
    }

}
