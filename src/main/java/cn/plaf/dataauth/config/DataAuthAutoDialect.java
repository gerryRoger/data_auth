package cn.plaf.dataauth.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.MappedStatement;

import cn.plaf.dataauth.AutoDialect;
import cn.plaf.dataauth.Dialect;
import cn.plaf.dataauth.PageException;
import cn.plaf.dataauth.dialect.AbstractHelperDialect;
import cn.plaf.dataauth.dialect.auto.C3P0AutoDialect;
import cn.plaf.dataauth.dialect.auto.DataSourceNegotiationAutoDialect;
import cn.plaf.dataauth.dialect.auto.DbcpAutoDialect;
import cn.plaf.dataauth.dialect.auto.DefaultAutoDialect;
import cn.plaf.dataauth.dialect.auto.DruidAutoDialect;
import cn.plaf.dataauth.dialect.auto.HikariAutoDialect;
import cn.plaf.dataauth.dialect.auto.TomcatAutoDialect;
import cn.plaf.dataauth.dialect.helper.MysqlDialect;
import cn.plaf.dataauth.dialect.helper.OracleDialect;
import cn.plaf.dataauth.util.StringUtil;


public class DataAuthAutoDialect {
    private static Map<String, Class<? extends Dialect>> dialectAliasMap = new HashMap<String, Class<? extends Dialect>>();
    private static Map<String, Class<? extends AutoDialect>> autoDialectMap = new HashMap<String, Class<? extends AutoDialect>>();

    public static void registerDialectAlias(String alias, Class<? extends Dialect> dialectClass) {
        dialectAliasMap.put(alias, dialectClass);
    }
    public static void registerAutoDialectAlias(String alias, Class<? extends AutoDialect> autoDialectClass) {
        autoDialectMap.put(alias, autoDialectClass);
    }
    static {
        //注册别名
        registerDialectAlias("mysql", MysqlDialect.class);
        registerDialectAlias("oracle", OracleDialect.class);
        registerDialectAlias("dm", OracleDialect.class);

//        //注册 AutoDialect
//        //想要实现和以前版本相同的效果时，可以配置 autoDialectClass=old
        registerAutoDialectAlias("old", DefaultAutoDialect.class);
        registerAutoDialectAlias("hikari", HikariAutoDialect.class);
        registerAutoDialectAlias("druid", DruidAutoDialect.class);
        registerAutoDialectAlias("tomcat-jdbc", TomcatAutoDialect.class);
        registerAutoDialectAlias("dbcp", DbcpAutoDialect.class);
        registerAutoDialectAlias("c3p0", C3P0AutoDialect.class);
        //不配置时，默认使用 DataSourceNegotiationAutoDialect
        registerAutoDialectAlias("default", DataSourceNegotiationAutoDialect.class);
    }
    
    /**
     * 自动获取dialect,如果没有setProperties或setSqlUtilConfig，也可以正常进行
     */
    private boolean autoDialect = true;
    /**
     * 属性配置
     */
    private Properties properties;
    /**
     * 缓存 dialect 实现，key 有两种，分别为 jdbcurl 和 dialectClassName
     */
    private Map<Object, AbstractHelperDialect> urlDialectMap = new ConcurrentHashMap<Object, AbstractHelperDialect>();
    private ReentrantLock lock = new ReentrantLock();
    private AbstractHelperDialect delegate;
    private ThreadLocal<AbstractHelperDialect> dialectThreadLocal = new ThreadLocal<AbstractHelperDialect>();
    private AutoDialect autoDialectDelegate;

    public static String fromJdbcUrl(String jdbcUrl) {
        final String url = jdbcUrl.toLowerCase();
        for (String dialect : dialectAliasMap.keySet()) {
            if (url.contains(":" + dialect.toLowerCase() + ":")) {
                return dialect;
            }
        }
        return null;
    }

    //获取当前的代理对象
    public AbstractHelperDialect getDelegate() {
        if (delegate != null) {
            return delegate;
        }
        return dialectThreadLocal.get();
    }

    //移除代理对象
    public void clearDelegate() {
        dialectThreadLocal.remove();
    }

    /**
     * 反射类
     *
     * @param className
     * @return
     * @throws Exception
     */
    public static Class resloveDialectClass(String className) throws Exception {
        if (dialectAliasMap.containsKey(className.toLowerCase())) {
            return dialectAliasMap.get(className.toLowerCase());
        } else {
            return Class.forName(className);
        }
    }

    /**
     * 初始化 helper
     *
     * @param dialectClass
     * @param properties
     */
    public static AbstractHelperDialect instanceDialect(String dialectClass, Properties properties) {
        AbstractHelperDialect dialect;
        if (StringUtil.isEmpty(dialectClass)) {
            throw new PageException("使用 DataAuthHelper 插件时，必须设置 helper 属性");
        }
        try {
            Class sqlDialectClass = resloveDialectClass(dialectClass);
            if (AbstractHelperDialect.class.isAssignableFrom(sqlDialectClass)) {
                dialect = (AbstractHelperDialect) sqlDialectClass.newInstance();
            } else {
                throw new PageException("使用 DataAuthHelper 时，方言必须是实现 " + AbstractHelperDialect.class.getCanonicalName() + " 接口的实现类!");
            }
        } catch (Exception e) {
            throw new PageException("初始化 helper [" + dialectClass + "]时出错:" + e.getMessage(), e);
        }
        dialect.setProperties(properties);
        return dialect;
    }

    /**
     * 多数据动态获取时，每次需要初始化，还可以运行时指定具体的实现
     *
     * @param ms
     * @param dialectClass 分页实现，必须是 {@link AbstractHelperDialect} 实现类，可以使用当前类中注册的别名，例如 "mysql", "oracle"
     */
    public void initDelegateDialect(MappedStatement ms, String dialectClass) {
        if (StringUtil.isNotEmpty(dialectClass)) {
            AbstractHelperDialect dialect = urlDialectMap.get(dialectClass);
            if (dialect == null) {
                lock.lock();
                try {
                    if ((dialect = urlDialectMap.get(dialectClass)) == null) {
                        dialect = instanceDialect(dialectClass, properties);
                        urlDialectMap.put(dialectClass, dialect);
                    }
                } finally {
                    lock.unlock();
                }
            }
            dialectThreadLocal.set(dialect);
        } else if (delegate == null) {
            if (autoDialect) {
                this.delegate = autoGetDialect(ms);
            } else {
                dialectThreadLocal.set(autoGetDialect(ms));
            }
        }
    }

    /**
     * 自动获取sql方言实现
     *
     * @param ms
     * @return
     */
    public AbstractHelperDialect autoGetDialect(MappedStatement ms) {
        DataSource dataSource = ms.getConfiguration().getEnvironment().getDataSource();
        Object dialectKey = autoDialectDelegate.extractDialectKey(ms, dataSource, properties);
        if (dialectKey == null) {
            return autoDialectDelegate.extractDialect(dialectKey, ms, dataSource, properties);
        } else if (!urlDialectMap.containsKey(dialectKey)) {
            lock.lock();
            try {
                if (!urlDialectMap.containsKey(dialectKey)) {
                    urlDialectMap.put(dialectKey, autoDialectDelegate.extractDialect(dialectKey, ms, dataSource, properties));
                }
            } finally {
                lock.unlock();
            }
        }
        return urlDialectMap.get(dialectKey);
    }

    /**
     * 初始化自定义 AutoDialect
     *
     * @param properties
     */
    private void initAutoDialectClass(Properties properties) {
        String autoDialectClassStr = properties.getProperty("autoDialectClass");
        if (StringUtil.isNotEmpty(autoDialectClassStr)) {
            try {
                Class<? extends AutoDialect> autoDialectClass;
                if (autoDialectMap.containsKey(autoDialectClassStr)) {
                    autoDialectClass = autoDialectMap.get(autoDialectClassStr);
                } else {
                    autoDialectClass = (Class<AutoDialect>) Class.forName(autoDialectClassStr);
                }
                this.autoDialectDelegate = autoDialectClass.getConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("请确保 autoDialectClass 配置的 AutoDialect 实现类(" + autoDialectClassStr + ")存在!", e);
            } catch (Exception e) {
                throw new RuntimeException(autoDialectClassStr + " 类必须提供无参的构造方法", e);
            }
        } else {
            this.autoDialectDelegate = new DataSourceNegotiationAutoDialect();
        }
    }

    /**
     * 初始化方言别名
     *
     * @param properties
     */
    private void initDialectAlias(Properties properties) {
        String dialectAlias = properties.getProperty("dialectAlias");
        if (StringUtil.isNotEmpty(dialectAlias)) {
            String[] alias = dialectAlias.split(";");
            for (int i = 0; i < alias.length; i++) {
                String[] kv = alias[i].split("=");
                if (kv.length != 2) {
                    throw new IllegalArgumentException("dialectAlias 参数配置错误，" +
                            "请按照 alias1=xx.dialectClass;alias2=dialectClass2 的形式进行配置!");
                }
                for (int j = 0; j < kv.length; j++) {
                    try {
                        Class<? extends Dialect> diallectClass = (Class<? extends Dialect>) Class.forName(kv[1]);
                        //允许覆盖已有的实现
                        registerDialectAlias(kv[0], diallectClass);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException("请确保 dialectAlias 配置的 Dialect 实现类存在!", e);
                    }
                }
            }
        }
    }

    public void setProperties(Properties properties) {
        //初始化自定义AutoDialect
        initAutoDialectClass(properties);
        //使用 sqlserver2012 作为默认分页方式，这种情况在动态数据源时方便使用
        String useSqlserver2012 = properties.getProperty("useSqlserver2012");
        if (StringUtil.isNotEmpty(useSqlserver2012) && Boolean.parseBoolean(useSqlserver2012)) {
//            registerDialectAlias("sqlserver", SqlServer2012Dialect.class);
//            registerDialectAlias("sqlserver2008", SqlServerDialect.class);
        }
        initDialectAlias(properties);
        //指定的 Helper 数据库方言，和  不同
        String dialect = properties.getProperty("helperDialect");
        //运行时获取数据源
        String runtimeDialect = properties.getProperty("autoRuntimeDialect");
        //1.动态多数据源
        if (StringUtil.isNotEmpty(runtimeDialect) && "TRUE".equalsIgnoreCase(runtimeDialect)) {
            this.autoDialect = false;
            this.properties = properties;
        }
        //2.动态获取方言
        else if (StringUtil.isEmpty(dialect)) {
            autoDialect = true;
            this.properties = properties;
        }
        //3.指定方言
        else {
            autoDialect = false;
            this.delegate = instanceDialect(dialect, properties);
        }
    }



}
