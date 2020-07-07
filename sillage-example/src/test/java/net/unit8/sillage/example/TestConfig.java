package net.unit8.sillage.example;

import net.unit8.sillage.example.port.out.PromotePort;
import net.unit8.sillage.example.port.out.UpdateEmployeeStatePort;
import org.javamoney.moneta.convert.IdentityRateProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.money.convert.ExchangeRateProvider;

import static org.mockito.Mockito.mock;

@Profile("test")
@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public ExchangeRateProvider exchangeRateProvider() {
        return new IdentityRateProvider();
    }

    @Bean
    @Primary
    public UpdateEmployeeStatePort updateEmployeeStatePort() {
        return mock(UpdateEmployeeStatePort.class);
    }

    @Bean
    @Primary
    public PromotePort promotePort() {
        return mock(PromotePort.class);
    }
}
