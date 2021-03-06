package ua.belozorov.lunchvoting.config;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**

 *
 * Created on 16.11.16.
 */
@Configuration
@EnableJpaRepositories(basePackages = "ua.belozorov.lunchvoting.repository", considerNestedRepositories = true)
@EnableTransactionManagement
public class JpaConfig {

    public JpaConfig() {
        System.out.println();
    }

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @Bean(name = "entityManagerFactory")
    public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform(env.getProperty("database.dialect"));
        vendorAdapter.setGenerateDdl(false);
        em.setDataSource(dataSource);
        em.setPackagesToScan("ua.belozorov.lunchvoting.model", "ua.belozorov.lunchvoting.util", "ua.belozorov.lunchvoting.model.voting.polling");
        em.setJpaVendorAdapter(vendorAdapter);
        em.afterPropertiesSet();
        return em.getObject();
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory em) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(em);
        return jpaTransactionManager;
    }
}
