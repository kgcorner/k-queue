package com.lluvia.security;

import com.kgcorner.lluvia.model.Application;
import com.lluvia.exception.InvalidTokenException;

/**
 * Created by admin on 3/28/2018.
 */
public class Authenticator {
    public static Application authenticate(String token) throws InvalidTokenException {
        Application application = null;
        if(!token.contains(" ")) {
            throw new IllegalArgumentException("Invalid token format");
        }

        String[] tokens = token.split(" ");
        Verifier verifier = null;
        switch (tokens[0]) {
            case "BASIC":
                verifier = BasicVerifier.getInstance();
                break;
            case "BEARER":
                verifier = BearerVerifier.getInstance();
                break;
            default:
                throw new IllegalArgumentException("Invalid token scheme");
        }
        application = verifier.verify(tokens[1]);

        return application;
    }
}
