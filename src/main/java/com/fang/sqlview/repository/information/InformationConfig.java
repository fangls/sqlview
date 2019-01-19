package com.fang.sqlview.repository.information;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/16
 */
@Configuration
@ConfigurationProperties(prefix = "app.datasource.information")
@EnableJpaRepositories(
        entityManagerFactoryRef = "informationEntityManagerFactory",
        transactionManagerRef = "informationTransactionManager")
public class InformationConfig extends HikariConfig {

    @Bean
    PlatformTransactionManager informationTransactionManager() {
        return new JpaTransactionManager(informationEntityManagerFactory().getObject());
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean informationEntityManagerFactory() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(false);
        jpaVendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setPackagesToScan(InformationConfig.class.getPackage().getName());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.physical_naming_strategy","org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        factoryBean.setJpaPropertyMap(properties);

        return factoryBean;
    }

    @Bean(name = "informationDataSource")
    public DataSource dataSource() {
        return new HikariDataSource(this);
    }

}

