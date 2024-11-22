package cn.plaf.dataauth.dialect.auto;

import org.apache.ibatis.mapping.MappedStatement;

import cn.plaf.dataauth.AutoDialect;
import cn.plaf.dataauth.config.DataAuthAutoDialect;
import cn.plaf.dataauth.dialect.AbstractHelperDialect;

import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Properties;

/**
 * 使用 Hikari 连接池时，简单获取jdbcUrl
 *
 * @author gerryluo
 */
public abstract class DataSourceAutoDialect<Ds extends DataSource> implements AutoDialect<String> {
    protected Class dataSourceClass;

    public DataSourceAutoDialect() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        dataSourceClass = (Class) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    }

    public abstract String getJdbcUrl(Ds ds);

    @Override
    public String extractDialectKey(MappedStatement ms, DataSource dataSource, Properties properties) {
        if (dataSourceClass.isInstance(dataSource)) {
            return getJdbcUrl((Ds) dataSource);
        }
        return null;
    }

    @Override
    public AbstractHelperDialect extractDialect(String dialectKey, MappedStatement ms, DataSource dataSource, Properties properties) {
        String dialect = DataAuthAutoDialect.fromJdbcUrl(dialectKey);
        return DataAuthAutoDialect.instanceDialect(dialect, properties);
    }

}
