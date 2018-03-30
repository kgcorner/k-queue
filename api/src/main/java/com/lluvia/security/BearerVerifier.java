package com.lluvia.security;


import com.kgcorner.lluvia.model.Application;
import com.lluvia.AuthServices;
import com.lluvia.exception.InvalidTokenException;

/**
 * Created by admin on 3/28/2018.
 */
public class BearerVerifier implements Verifier{
    private static final BearerVerifier INSTANCE = new BearerVerifier();
    private final AuthServices authService;
    private BearerVerifier() {
        authService = AuthServices.getInstance();
    }

    public static BearerVerifier getInstance() {
        return INSTANCE;
    }

    @Override
    public Application verify(String token) throws InvalidTokenException {
        return authService.verifyAndReturn(token);
    }
}
