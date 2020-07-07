package net.unit8.sillage;

import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import net.unit8.sillage.data.ClassResourceFactory;
import net.unit8.sillage.data.DecisionHandlerAdapter;
import net.unit8.sillage.resolver.DecisionResponseHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.zalando.problem.ProblemModule;

import java.text.SimpleDateFormat;
import java.util.List;

@Configuration
public class SillageConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return  new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .modulesToInstall(new ProblemModule().withStackTraces())
                .modulesToInstall(new ParameterNamesModule());
    }

    @Bean
    public ClassResourceFactory classResourceFactory(ApplicationContext context,
                                                     Jackson2ObjectMapperBuilder builder) {
        DecisionHandlerAdapter adapter = new DecisionHandlerAdapter();
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
}
