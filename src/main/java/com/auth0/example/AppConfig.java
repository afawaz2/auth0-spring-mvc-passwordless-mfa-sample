package com.auth0.example;

import com.auth0.web.Auth0Config;
import com.auth0.web.Auth0Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties("auth0")
public class AppConfig extends Auth0Config {

    @Bean
    public FilterRegistrationBean filterRegistration() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new Auth0Filter(this));
        registration.addUrlPatterns(securedRoute);
        registration.addInitParameter("redirectOnAuthError", loginRedirectOnFail);
        registration.setName("Auth0Filter");
        return registration;
    }

    @Value(value = "${auth0.managementToken}")
    protected String managementToken;

    @Value(value = "${auth0.accountLinkRedirectOnSuccess}")
    protected String accountLinkRedirectOnSuccess;

    @Value(value = "${auth0.accountLinkRedirectOnFail}")
    protected String accountLinkRedirectOnFail;

    @Value(value = "${auth0.emailLinkRedirectOnSuccess}")
    protected String emailLinkRedirectOnSuccess;

    @Value(value = "${auth0.emailLinkRedirectOnFail}")
    protected String emailLinkRedirectOnFail;


    public String getManagementToken() {
        return managementToken;
    }

    public String getAccountLinkRedirectOnSuccess() {
        return accountLinkRedirectOnSuccess;
    }

    public String getAccountLinkRedirectOnFail() {
        return accountLinkRedirectOnFail;
    }

    public String getEmailLinkRedirectOnSuccess() {
        return emailLinkRedirectOnSuccess;
    }

    public String getEmailLinkRedirectOnFail() {
        return emailLinkRedirectOnFail;
    }
}
