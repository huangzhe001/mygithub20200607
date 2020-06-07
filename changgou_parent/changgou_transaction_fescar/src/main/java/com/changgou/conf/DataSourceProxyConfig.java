package com.changgou.conf;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**此配置文件为分布式事务的配置信息
 * @author Alan
 * @version 1.0
 * @date 2019/12/4 19:41
 */
@Configuration
public class DataSourceProxyConfig {
    /**
     * 普通数据源-通过yml的spring.datasource配置
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return new DruidDataSource();
    }

    /**
     * 代理数据源-引用普通数据源
     * @param dataSource
     * @return
     */
    @Bean
    public DataSourceProxy dataSourceProxy(DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    /**
     * MyBatis需要手动指定SqlSessionFactory绑定代理数据源
     * @param dataSourceProxy
     * @return
     * @throws Exception
     */
  /*  @Bean
    public SqlSessionFactory sqlSessionFactoryBean(DataSourceProxy dataSourceProxy) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        //使用mybatis时要使用代理数据源
        sqlSessionFactoryBean.setDataSource(dataSourceProxy);
        return sqlSessionFactoryBean.getObject();
    }*/
}
