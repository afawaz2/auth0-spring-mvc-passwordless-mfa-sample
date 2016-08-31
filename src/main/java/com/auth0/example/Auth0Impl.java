package com.auth0.example;

import com.auth0.Auth0Client;
import com.auth0.Auth0User;
import com.auth0.Tokens;
import okhttp3.*;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import java.net.URLEncoder;

import static us.monoid.web.Resty.content;

/**
 * Wrapper implementation around Auth0 service calls
 * Don't expose internals of Auth0 library
 */
@Service
public class Auth0Impl implements Auth0 {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Auth0Client auth0Client;
    protected AppConfig appConfig;

    @Autowired
    public Auth0Impl(final Auth0Client auth0Client, final AppConfig appConfig) {
        Validate.notNull(auth0Client);
        Validate.notNull(appConfig);
        this.auth0Client = auth0Client;
        this.appConfig = appConfig;
    }

    @Override
    public Auth0User linkAccount(final Auth0User existingUser, final Tokens tokens, final Tokens existingTokens) {
        // link accounts here
        final String primaryAccountJwt = existingTokens.getIdToken();
        final String primaryAccountUserId = existingUser.getUserId();
        final String secondaryAccountJwt = tokens.getIdToken();
        // do account linking
        try {
            final String encodedPrimaryAccountUserId = URLEncoder.encode(primaryAccountUserId, "UTF-8");
            final String linkUri = getUri("/api/v2/users/") + encodedPrimaryAccountUserId + "/identities";
            final Resty resty = new Resty();
            resty.withHeader("Authorization", "Bearer " + primaryAccountJwt);
            final JSONObject json = new JSONObject();
            json.put("link_with", secondaryAccountJwt);
            final JSONResource linkedProfileInfo = resty.json(linkUri, content(json));
            final JSONArray profileArray = linkedProfileInfo.array();
            final JSONObject firstProfileEntry = profileArray.getJSONObject(0);
            final String primaryConnectionType = (String) firstProfileEntry.get("connection");
            if (!"email".equals(primaryConnectionType)) {
                throw new IllegalStateException("Error linking accounts - wrong primary connection type detected: " + primaryConnectionType);
            }
            // Just fetch updated (linked) profile using previously obtained tokens for email profile
            final Auth0User linkedUser = auth0Client.getUserProfile(existingTokens);
            return linkedUser;
        } catch (Exception ex) {
            throw new IllegalStateException("Error retrieving profile information from Auth0", ex);
        }
    }

    public void signupMfa(final String accountUserId) {
        logger.info("Registering user with MFA");
        final String auth0Domain = appConfig.getDomain();
        final String managementToken = appConfig.getManagementToken();
        final String mfaPayload = "{\"app_metadata\":{\"use_mfa\":true}}";

        try {
            final String encodedAccountUserId = URLEncoder.encode(accountUserId, "UTF-8");
            final String endpoint = getUri(auth0Domain, "/api/v2/users/" + encodedAccountUserId);
            final OkHttpClient client = new OkHttpClient();
            final MediaType mediaType = MediaType.parse("application/json");
            final RequestBody body = RequestBody.create(mediaType, mfaPayload);
            final Request clientRequest = new Request.Builder()
                    .url(endpoint)
                    .patch(body)
                    .addHeader("authorization", "Bearer " + managementToken)
                    .addHeader("content-type", "application/json")
                    .build();
            final Response clientResponse = client.newCall(clientRequest).execute();
            if (clientResponse.code() != 200) {
                throw new IllegalStateException("Error occurred setting up MFA signup");
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error occurred setting up MFA signup: ", ex);
        }
    }

    protected String getUri(final String auth0Domain, final String path) {
        return String.format("https://%s%s", auth0Domain, path);
    }

    protected String getUri(String path) {
        return String.format("https://%s%s", appConfig.getDomain(), path);
    }

    public boolean isDropBoxAuth0User(final Auth0User auth0User) {
        Validate.notNull(auth0User);
        return auth0User.getUserId().startsWith("dropbox");
    }

    public boolean isEmailAuth0User(final Auth0User auth0User) {
        Validate.notNull(auth0User);
        return auth0User.getUserId().startsWith("email");
    }

}
