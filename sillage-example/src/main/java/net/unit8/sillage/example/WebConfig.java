package net.unit8.sillage.example;

import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.zalando.jackson.datatype.money.MoneyModule;
import org.zalando.problem.ProblemModule;

import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;
import java.text.SimpleDateFormat;

@Configuration
public class WebConfig {
    @Bean
    public HttpMessageConverter<Object> createJacksonConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .indentOutput(false)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .modules(new ProblemModule().withStackTraces(),
                        new MoneyModule(),
                        new ParameterNamesModule());
        return new MappingJackson2HttpMessageConverter(builder.build());
    }

    @Bean
    public ExchangeRateProvider exchangeRateProvider() {
        return MonetaryConversions.getExchangeRateProvider("IMF");
    }

}
