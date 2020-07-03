package net.unit8.sillage.example;

import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import net.unit8.sillage.ResourceEngine;
import net.unit8.sillage.data.ClassResourceFactory;
import net.unit8.sillage.data.DecisionHandlerAdapter;
import net.unit8.sillage.resolver.DecisionResponseHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.zalando.problem.ProblemModule;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;

@SpringBootApplication
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
public class RestServiceApplication {
  @Bean
  public DataSource dataSource() {
    EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
    return builder.setType(EmbeddedDatabaseType.H2).build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(true);

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(vendorAdapter);
    factory.setPackagesToScan("net.unit8.sillage.example.entity");
    factory.setDataSource(dataSource());
    return factory;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory);
    return txManager;
  }

    @Bean
    public ClassResourceFactory classResourceFactory(ApplicationContext context) {
        DecisionHandlerAdapter adapter = new DecisionHandlerAdapter();
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .modulesToInstall(new ProblemModule().withStackTraces())
                .modulesToInstall(new ParameterNamesModule());
        adapter.getMessageConverters().add(
                new MappingJackson2HttpMessageConverter(builder.build()));
        adapter.setApplicationContext(context);
        adapter.afterPropertiesSet();
        adapter.setReturnValueHandlers(List.of(new DecisionResponseHandler()));
        return new ClassResourceFactory(adapter, context);
    }
    @Bean
    public ResourceEngine resourceEngine() {
        return new ResourceEngine();
    }
    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }
}
