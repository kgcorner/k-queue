package com.lluvia.security;

/**
 * Created by admin on 3/28/2018.
 */
public class BearerScheme implements Scheme {
    private static final Verifier verifier;
    static {
        verifier = BearerVerifier.getInstance();
    }

    @Override
    public String getScheme() {
        return "BEARER";
    }

    @Override
    public Verifier getVerifier() {
        return verifier;
    }
}
