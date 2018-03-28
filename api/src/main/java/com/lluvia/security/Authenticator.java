package com.lluvia.security;

/**
 * Created by admin on 3/28/2018.
 */
public class Authenticator {
    public static Boolean authenticate(String token) {
        boolean result = false;
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
        result = verifier.verify(tokens[1]);

        return result;
    }
}
