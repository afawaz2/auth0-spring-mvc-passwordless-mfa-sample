package com.auth0.example;

import com.auth0.NonceUtils;
import com.auth0.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppConfig appConfig;


    @RequestMapping(value="/login", method = RequestMethod.GET)
    protected String login(final Map<String, Object> model, final HttpServletRequest req) throws ServletException, IOException {
        logger.debug("Performing login");
        detectError(model);
        // add Nonce to storage
        NonceUtils.addNonceToStorage(req);
        model.put("clientId", appConfig.getClientId());
        model.put("domain", appConfig.getDomain());
        model.put("state", SessionUtils.getState(req));
        return "login";
    }

    private void detectError(final Map<String, Object> model) {
        if (model.get("error") != null) {
            model.put("error", true);
        } else {
            model.put("error", false);
        }
    }

}
