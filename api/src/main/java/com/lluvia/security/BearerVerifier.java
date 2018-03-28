package com.lluvia.security;



/**
 * Created by admin on 3/28/2018.
 */
public class BearerVerifier implements Verifier{
    private static final BearerVerifier INSTANCE = new BearerVerifier();
    private BearerVerifier() {
    }

    public static BearerVerifier getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean verify(String token) {
        return false;
    }
}
