package com.rajcloud.observability;

import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@AutoConfiguration
public class ObservabilityAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "jakarta.servlet.Filter")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class ServletCorrelationConfiguration {
        @Bean
        @ConditionalOnMissingBean
        CorrelationIdFilter correlationIdFilter() {
            return new CorrelationIdFilter();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.springframework.web.server.WebFilter")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    static class ReactiveCorrelationConfiguration {
        @Bean
        @ConditionalOnMissingBean
        WebFilter reactiveCorrelationIdFilter() {
            return new ReactiveCorrelationIdFilter();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "feign.RequestInterceptor")
    static class FeignCorrelationConfiguration {
        @Bean
        @ConditionalOnMissingBean
        RequestInterceptor correlationRequestInterceptor() {
            return template -> template.header(CorrelationConstants.CORRELATION_ID_HEADER, CorrelationId.currentOrNew());
        }
    }
}
