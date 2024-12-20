package cn.plaf.dataauth.dialect.auto;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Hikari
 *
 * @author gerryluo
 */
public class HikariAutoDialect extends DataSourceAutoDialect<HikariDataSource> {

    @Override
    public String getJdbcUrl(HikariDataSource hikariDataSource) {
        return hikariDataSource.getJdbcUrl();
    }

}
