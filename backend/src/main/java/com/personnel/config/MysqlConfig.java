package com.personnel.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import java.util.HashMap;
import java.util.Map;

/** mysql profile 下手动启用 JPA（默认 profile 排除 JPA 自动配置，避免无数据源启动失败）。 */
@Configuration
@Profile("mysql")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.personnel.repository")
@EntityScan(basePackages = "com.personnel.model")
public class MysqlConfig {
  @Bean
  public DataSource dataSource(DataSourceProperties props) { return props.initializeDataSourceBuilder().build(); }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("com.personnel.model");
    HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
    vendor.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
    vendor.setShowSql(false);
    em.setJpaVendorAdapter(vendor);
    Map<String, Object> jpa = new HashMap<>();
    jpa.put("hibernate.hbm2ddl.auto", "update");
    em.setJpaPropertyMap(jpa);
    return em;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }
}
