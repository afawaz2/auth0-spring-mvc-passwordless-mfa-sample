package com.auth0.example;

import com.auth0.Auth0User;
import com.auth0.NonceFactory;
import com.auth0.NonceUtils;
import com.auth0.SessionUtils;
import com.auth0.authentication.result.UserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    public static final String MFA_NONCE = "mfaNonce";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AppConfig appConfig;

    @Autowired
    protected Auth0 auth0;

    @Autowired
    public HomeController(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @RequestMapping(value="/portal/home", method = RequestMethod.GET)
    protected String home(final Map<String, Object> model, final HttpServletRequest req) throws ServletException, IOException {
        logger.info("Home page");
        final Auth0User user = SessionUtils.getAuth0User(req);
        model.put("user", user);
        model.put("clientId", appConfig.getClientId());
        model.put("domain", appConfig.getDomain());
        handleMfaLink(user, req);
        handleLinkDropbox(user, req);
        return "home";
    }

    protected boolean isMfaEnabled(final Auth0User auth0User) {
        final Map<String, Object> appMetadata = auth0User.getAppMetadata();
        final Boolean mfa = (Boolean) appMetadata.get("use_mfa");
        return mfa != null && mfa;
    }

    protected void handleMfaLink(final Auth0User user, final HttpServletRequest req) {
        final boolean hasMfa = isMfaEnabled(user);
        final HttpSession session = req.getSession(true);
        if (!hasMfa) {
            String mfaNonce = (String) session.getAttribute(MFA_NONCE);
            if (mfaNonce == null) {
                mfaNonce = NonceFactory.create();
                session.setAttribute(MFA_NONCE, mfaNonce);
            }
        } else {
            session.removeAttribute(MFA_NONCE);
        }
        req.setAttribute("hasMfa", hasMfa);
    }

    protected void handleLinkDropbox(final Auth0User auth0User, final HttpServletRequest req) {
        boolean linkDropbox = true;
        final List<UserIdentity> identities = auth0User.getIdentities();
        for(final UserIdentity userIdentity: identities) {
            if ("dropbox".equals(userIdentity.getProvider())) {
                linkDropbox = false;
                final Map<String, Object> profileInfo = userIdentity.getProfileInfo();
                if (profileInfo != null && profileInfo.get("email") != null) {
                    req.setAttribute("dropboxEmail", profileInfo.get("email"));
                }
            }
        }
        if (linkDropbox) {
            NonceUtils.addNonceToStorage(req);
        }
        req.setAttribute("linkDropbox", linkDropbox);
    }

}
