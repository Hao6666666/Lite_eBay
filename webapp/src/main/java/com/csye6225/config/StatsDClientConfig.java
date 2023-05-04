package com.csye6225.config;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class StatsDClientConfig {

    @Value("${metrics.statsd.enabled}")
    private boolean statsDEnabled;

    @Value("${metrics.statsd.host}")
    private String statsDHost;

    @Value("${metrics.statsd.port}")
    private int statsDPort;

    @Bean
    public StatsDClient statsDClient() {
        if (statsDEnabled) {
            return new NonBlockingStatsDClient("csye6225", statsDHost, statsDPort);
        }
        return new NoOpStatsDClient();
    }

}

