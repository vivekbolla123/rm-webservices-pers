package com.akasaair.rm_webservice.common.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.akasaair.nav_ods_webservices.file_generation",
        entityManagerFactoryRef = "rmallocEntityManagerFactory",
        transactionManagerRef = "rmallocTransactionManager"
)
public class RmAllocConfig {
    @Autowired
    JpaProperties jpaProperties;

    @Bean
    @ConfigurationProperties("spring.rmallocdatasource")
    public DataSourceProperties rmAllocDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("rmAllocDatasource")
    @ConfigurationProperties("spring.rmallocdatasource.hikari")
    public DataSource rmAllocDataSource() {
        return rmAllocDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "rmAllocJdbcTemplate")
    public JdbcTemplate dlJdbcTemplate(@Qualifier("rmAllocDatasource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean rmAllocEntityManagerFactory(JpaProperties jpaProperties) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(rmAllocDataSource());
        em.setPackagesToScan("com.akasaair.nav_ods_webservices.file_generation");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        em.setPersistenceUnitName("rmAllocPUP");

        em.setJpaPropertyMap(jpaProperties.getProperties());

        return em;

    }

    @Bean
    public PlatformTransactionManager rmAllocTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(rmAllocEntityManagerFactory(jpaProperties).getObject());

        return transactionManager;
    }
}