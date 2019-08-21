/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.config;

import org.apache.camel.component.sql.SqlComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 *
 * @author 212579464
 */
@Configuration
@ImportResource(locations = {"ris-datasource.xml"})
public class DataSourceConfig {

    @Autowired
    private Environment env; //加载配置文件

    @Bean("defaultDS")
    @Primary   //此注解表示在默认的数据源配置，即在默认配置时用到的数据源配置
    public DataSource primaryDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("custom.datasource.primary.driverClassName"));
        dataSource.setUrl(env.getProperty("custom.datasource.primary.url"));
        dataSource.setUsername(env.getProperty("custom.datasource.primary.username"));
        dataSource.setPassword(env.getProperty("custom.datasource.primary.password"));
        return dataSource;
    }

    @Bean("baseSqlComponent")
    public SqlComponent baseSqlComponent(@Qualifier("defaultDS") DataSource dataSource){
        SqlComponent sqlComponent = new SqlComponent();
        sqlComponent.setDataSource(dataSource);
        return sqlComponent;
    }

}
