package com.auth0.example;

import com.auth0.Auth0User;
import com.auth0.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Controller
public class MfaSignupController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AppConfig appConfig;

    @Autowired
    protected Auth0 auth0;

    @Autowired
    public MfaSignupController(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @RequestMapping(value="/portal/mfa", method = RequestMethod.GET)
    protected void mfaSignup(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        final Auth0User user = SessionUtils.getAuth0User(req);
        if (user == null) {
            final String logoutPath = appConfig.getOnLogoutRedirectTo();
            res.sendRedirect(logoutPath);
        }
        req.setAttribute("user", user);
        final boolean hasMfa = isMfaEnabled(user);
        if (hasMfa) {
            req.getRequestDispatcher("/home").forward(req, res);
            return;
        } else {
            final HttpSession session = req.getSession(true);
            final String requestMfaNonce = req.getParameter("mfaNonce");
            final String sessionMfaNonce = (String) session.getAttribute("mfaNonce");
            if (requestMfaNonce == null || sessionMfaNonce == null) {
                req.getRequestDispatcher("/home").forward(req, res);
                return;
            } else if (!sessionMfaNonce.equals(requestMfaNonce)) {
                req.getRequestDispatcher("/home").forward(req, res);
                return;
            }
            auth0.signupMfa(user.getUserId());
            req.getRequestDispatcher("/logout").forward(req, res);
        }
    }

    protected boolean isMfaEnabled(final Auth0User auth0User) {
        final Map<String, Object> appMetadata = auth0User.getAppMetadata();
        final Boolean useMfa = (Boolean) appMetadata.get("use_mfa");
        return useMfa != null && useMfa;
    }

}
