package cn.plaf.dataauth.util;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;

public class MybatisPlusInterceptorHelper {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            //创建SqlSessionFactory
            Reader reader = Resources.getResourceAsReader(TestUtil.getXmlPath() + "/mybatis-config.xml");
            sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(reader);
            reader.close();
            if (TestUtil.getXmlPath().equalsIgnoreCase("hsqldb")
                    || TestUtil.getXmlPath().equalsIgnoreCase("h2")
                    || TestUtil.getXmlPath().equalsIgnoreCase("derby")) {
                //创建数据库
                SqlSession session = null;
                try {
                    session = sqlSessionFactory.openSession();
                    Connection conn = session.getConnection();
                    reader = Resources.getResourceAsReader(TestUtil.getXmlPath() + "/" + TestUtil.getXmlPath() + ".sql");
                    ScriptRunner runner = new ScriptRunner(conn);
                    runner.setLogWriter(null);
                    runner.runScript(reader);
                    reader.close();
                } finally {
                    if (session != null) {
                        session.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Session
     *
     * @return
     */
    public static SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }
}
