package com.auth0.example;

import com.auth0.web.Auth0CallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class EmailLinkCallbackController extends Auth0CallbackHandler
        implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    protected AppConfig appConfig;

    @Autowired
    protected Auth0 auth0;

    /**
     * Override with our values
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.redirectOnSuccess = appConfig.getEmailLinkRedirectOnSuccess();
        this.redirectOnFail = appConfig.getEmailLinkRedirectOnFail();
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    protected void callback(final HttpServletRequest req, final HttpServletResponse res)
            throws ServletException, IOException {
        super.handle(req, res);
    }

}
