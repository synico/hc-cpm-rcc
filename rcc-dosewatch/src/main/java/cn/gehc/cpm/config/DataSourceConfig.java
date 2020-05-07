package cn.gehc.cpm.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * @author 212706300
 */

@Configuration
public class DataSourceConfig {

    @Autowired
    private Environment env; //加载配置文件

    @Bean("rccDataSource")
    @Primary //此注解表示在默认的数据源配置，即在默认配置时用到的数据源配置
    public DataSource primaryDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(env.getProperty("custom.datasource.primary.driverClassName"));
        dataSource.setJdbcUrl(env.getProperty("custom.datasource.primary.url"));
        dataSource.setUsername(env.getProperty("custom.datasource.primary.username"));
        dataSource.setPassword(env.getProperty("custom.datasource.primary.password"));
        dataSource.setConnectionTimeout(env.getProperty("custom.datasource.primary.connectionTimeout", Long.class));
        dataSource.setIdleTimeout(env.getProperty("custom.datasource.primary.idleTimeout", Long.class));
        dataSource.setMaxLifetime(env.getProperty("custom.datasource.primary.maxLifetime", Long.class));
        dataSource.setMaximumPoolSize(env.getProperty("custom.datasource.primary.maximumPoolSize", Integer.class));
        return dataSource;
    }

}
