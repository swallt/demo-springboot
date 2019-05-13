package com.example.demospringboot.cat;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

/**
 * 对MyBatis进行拦截，添加Cat监控
 */
@Intercepts({@Signature(method = "query",type = Executor.class,args ={
        MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class
})})
@ConditionalOnProperty(name = "cat.mybatis.interceptor", havingValue = "true")
public class CatMybatisPlugin implements Interceptor {

    //缓存 提高性能
    private static final Map<String, String> sqlURLCache = new ConcurrentHashMap<>(256);

    private static String EMPTY_CONNECTION = "";

    private Executor target;

    public CatMybatisPlugin(String dataBaseUrl){EMPTY_CONNECTION=dataBaseUrl; }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        //得到类名，方法
        String[] strings = mappedStatement.getId().split("\\.");
        String methodName = strings[strings.length - 2] + "." + strings[strings.length - 1];

        Transaction t = Cat.newTransaction("SQL",methodName);

        //得到sql语句
        Object parameter = null;
        if (invocation.getArgs().length > 1){
            parameter = invocation.getArgs()[1];
        }
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        String sql = showSql(configuration,boundSql);

        //得到sql类型
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Cat.logEvent("SQL.Method",sqlCommandType.name().toLowerCase(),Message.SUCCESS,sql);

        String s = this.getSQLDatabase();
        Cat.logEvent("SQL.Database", s);
        Object returnObj = null;
        try {
            returnObj = invocation.proceed();
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e){
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();;
        }
        return returnObj;
    }

    @Override
    public Object plugin(Object o) {
        if (o instanceof Executor){
            this.target = (Executor) o;
            return Plugin.wrap(o,this);
        }
        return o;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private String getSQLDatabase(){
        //根据设置的多数据源修改此处，获取dbname
        String dbName = null;
        if (dbName == null){
            dbName = "DEFAULT";
        }
        String url = CatMybatisPlugin.sqlURLCache.get(dbName);
        if (url != null){
            return url;
        }
        url = String.format(EMPTY_CONNECTION,dbName);
        CatMybatisPlugin.sqlURLCache.put(dbName,url);
        return url;
    }

    /**
     * 解析sql语句
     * @param configuration
     * @param boundSql
     * @return
     */
    public String showSql(Configuration configuration,BoundSql boundSql){
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+"," ");
        if (parameterMappings.size() > 0 && parameterObject != null){
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())){
                sql = sql.replaceFirst("\\?",Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings){
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)){
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?",Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)){
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?",Matcher.quoteReplacement(getParameterValue(obj)));
                    }
                }
            }
        }
        return sql;
    }

    /**
     * 参数解析
     * @param obj
     * @return
     */
    private String getParameterValue(Object obj){
        String value = null;
        if (obj instanceof String){
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date){
            DateFormat format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + format.format((Date) obj)+"'";
        } else {
            if (obj != null){
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }
}
