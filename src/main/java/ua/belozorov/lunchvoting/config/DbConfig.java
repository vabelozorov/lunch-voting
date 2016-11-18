package ua.belozorov.lunchvoting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 16.11.16.
 */
@Configuration
@EnableJpaRepositories(basePackages = "ua.belozorov.lunchvoting.repository")
@EnableTransactionManagement
public class DbConfig {

    @Autowired
    private Environment env;

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(env.getProperty("database.driverClassName"));
        dataSource.setUrl(env.getProperty("database.url"));
        dataSource.setUsername(env.getProperty("database.username"));
        dataSource.setPassword(env.getProperty("database.password"));
        return dataSource;
    }

    @Bean("entityManagerFactory")
    public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setDataSource(dataSource);
        em.setPackagesToScan("ua.belozorov.lunchvoting.model");
        em.setJpaVendorAdapter(vendorAdapter);
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("database.hbm2ddl.auto"));
        em.setJpaProperties(jpaProperties);
        em.afterPropertiesSet();
        return em.getObject();
    }

    @Bean("transactionManager")
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory em) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(em);
        return jpaTransactionManager;
    }
}
