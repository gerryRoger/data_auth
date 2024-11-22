package cn.plaf.dataauth.dialect.auto;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * Druid
 *
 * @author gerryluo
 */
public class DruidAutoDialect extends DataSourceAutoDialect<DruidDataSource> {

    @Override
    public String getJdbcUrl(DruidDataSource druidDataSource) {
        return druidDataSource.getUrl();
    }

}
