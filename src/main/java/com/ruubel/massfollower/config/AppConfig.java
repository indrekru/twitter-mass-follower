package com.ruubel.massfollower.config;

import com.rollbar.notifier.Rollbar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;

@Configuration
public class AppConfig {


    public AppConfig() {
        configureUTC();
    }

    public void configureUTC() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    }

    @Bean
    public ConfigParams configParams() throws Exception {
        return new ConfigParams();
    }

    @Bean
    public Rollbar rollbar() {
        Rollbar rollbar = Rollbar.init(withAccessToken("34cea2fc2be749e7a2ec60d066286f9f").build());
        return rollbar;
    }

}
