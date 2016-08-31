package com.auth0.example;


import com.auth0.Auth0User;
import com.auth0.Tokens;

public interface Auth0 {

    Auth0User linkAccount(Auth0User user, Tokens tokens, Tokens existingTokens);

    boolean isDropBoxAuth0User(Auth0User auth0User);

    boolean isEmailAuth0User(Auth0User auth0User);

    void signupMfa(String accountUserId);

}
