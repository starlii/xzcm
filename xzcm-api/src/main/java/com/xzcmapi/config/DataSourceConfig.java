package com.xzcmapi.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig {

    private static final String PREFIX = "spring.datasource.";
    private Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
    @Autowired
    private Environment environment;

    /**
     * 数据库配置
     *
     * @return
     */
    @Bean(name = "db")
    @ConfigurationProperties(prefix = "spring.datasource") // application.properteis中对应属性的前缀
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();

        datasource.setUrl(environment.getProperty(PREFIX + "url"));
        datasource.setUsername(environment.getProperty(PREFIX + "username"));
        datasource.setPassword(environment.getProperty(PREFIX + "password"));
        datasource.setDriverClassName(environment.getProperty(PREFIX + "driverClassName"));

        //configuration
        datasource.setInitialSize(Integer.valueOf(environment.getProperty(PREFIX + "initialSize")));
        datasource.setMinIdle(Integer.valueOf(environment.getProperty(PREFIX + "minIdle")));
        datasource.setMaxActive(Integer.valueOf(environment.getProperty(PREFIX + "maxActive")));
        datasource.setMaxWait(Long.valueOf(environment.getProperty(PREFIX + "maxWait")));
        datasource.setTimeBetweenEvictionRunsMillis(Long.valueOf(environment.getProperty(PREFIX + "timeBetweenEvictionRunsMillis")));
        datasource.setMinEvictableIdleTimeMillis(Long.valueOf(environment.getProperty(PREFIX + "minEvictableIdleTimeMillis")));
        datasource.setValidationQuery(environment.getProperty(PREFIX + "validationQuery"));
        datasource.setTestWhileIdle(Boolean.valueOf(environment.getProperty(PREFIX + "testWhileIdle")));
        datasource.setTestOnBorrow(Boolean.valueOf(environment.getProperty(PREFIX + "testOnBorrow")));
        datasource.setTestOnReturn(Boolean.valueOf(environment.getProperty(PREFIX + "testOnReturn")));
        datasource.setPoolPreparedStatements(Boolean.valueOf(environment.getProperty(PREFIX + "poolPreparedStatements")));
        datasource.setMaxPoolPreparedStatementPerConnectionSize(Integer.valueOf(environment.getProperty(PREFIX + "maxPoolPreparedStatementPerConnectionSize")));
        try {
            datasource.setFilters(environment.getProperty(PREFIX + "filters"));
        } catch (SQLException e) {
            logger.error("druid configuration initialization filter", e);
        }
        datasource.setConnectionProperties(environment.getProperty(PREFIX + "connectionProperties"));
        return datasource;
    }
}
