package cn.plaf.dataauth.dialect.auto;

import org.apache.ibatis.mapping.MappedStatement;

import cn.plaf.dataauth.AutoDialect;
import cn.plaf.dataauth.PageException;
import cn.plaf.dataauth.config.DataAuthAutoDialect;
import cn.plaf.dataauth.dialect.AbstractHelperDialect;
import cn.plaf.dataauth.util.StringUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DefaultAutoDialect implements AutoDialect<String> {

    public static final AutoDialect<String> DEFAULT = new DefaultAutoDialect();

    @Override
    public String extractDialectKey(MappedStatement ms, DataSource dataSource, Properties properties) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return conn.getMetaData().getURL();
        } catch (SQLException e) {
            throw new PageException(e);
        } finally {
            if (conn != null) {
                try {
                    String closeConn = properties.getProperty("closeConn");
                    if (StringUtil.isEmpty(closeConn) || Boolean.parseBoolean(closeConn)) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    //ignore
                }
            }
        }
    }

    @Override
    public AbstractHelperDialect extractDialect(String dialectKey, MappedStatement ms, DataSource dataSource, Properties properties) {
        String dialectStr = DataAuthAutoDialect.fromJdbcUrl(dialectKey);
        if (dialectStr == null) {
            throw new PageException("无法自动获取数据库类型，请确认 helperDialect 参数指定!");
        }
        return DataAuthAutoDialect.instanceDialect(dialectStr, properties);
    }
}
