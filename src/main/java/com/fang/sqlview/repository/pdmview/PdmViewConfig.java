package com.fang.sqlview.repository.pdmview;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/16
 */
@Configuration
@ConfigurationProperties(prefix = "app.datasource.pdmview")
@EnableJpaRepositories(
        entityManagerFactoryRef = "pdmViewEntityManagerFactory",
        transactionManagerRef = "pdmViewTransactionManager")
public class PdmViewConfig extends HikariConfig {

    @Bean
    PlatformTransactionManager pdmViewTransactionManager() {
        return new JpaTransactionManager(pdmViewEntityManagerFactory().getObject());
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean pdmViewEntityManagerFactory() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setDataSource(dataSource());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setPackagesToScan(PdmViewConfig.class.getPackage().getName());

        return factoryBean;
    }

    @Bean(name = "pdmViewDataSource")
    @Primary
    public DataSource dataSource() {
        return new HikariDataSource(this);
    }

}

