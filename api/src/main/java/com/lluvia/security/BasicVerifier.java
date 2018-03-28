package com.lluvia.security;

/**
 * Created by admin on 3/28/2018.
 */
public class BasicVerifier implements Verifier{
    private static final BasicVerifier INSTANCE = new BasicVerifier();
    private BasicVerifier() {
    }

    public static BasicVerifier getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean verify(String token) {
        return false;
    }
}
