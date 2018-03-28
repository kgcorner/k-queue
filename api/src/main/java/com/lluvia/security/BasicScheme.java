package com.lluvia.security;

/**
 * Created by admin on 3/28/2018.
 */
public class BasicScheme implements Scheme{
    private static final Verifier verifier;
    static {
        verifier = BasicVerifier.getInstance();
    }

    @Override
    public String getScheme() {
        return "BASIC";
    }

    @Override
    public Verifier getVerifier() {
        return verifier;
    }
}
