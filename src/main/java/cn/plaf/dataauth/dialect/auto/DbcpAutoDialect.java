package cn.plaf.dataauth.dialect.auto;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * commons-dbcp
 *
 * @author gerryluo
 */
public class DbcpAutoDialect extends DataSourceAutoDialect<BasicDataSource> {

    @Override
    public String getJdbcUrl(BasicDataSource basicDataSource) {
        return basicDataSource.getUrl();
    }

}
