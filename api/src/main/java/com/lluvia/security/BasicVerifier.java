package com.lluvia.security;

import com.kgcorner.lluvia.data.ApplicationStore;
import com.kgcorner.lluvia.model.Application;
import com.lluvia.exception.InvalidTokenException;

import java.util.Base64;

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
    public Application verify(String token) throws InvalidTokenException {
        Application application = null;
        byte[] decodedTokenByte = Base64.getDecoder().decode(token.getBytes());
        String decodedToken = new String(decodedTokenByte);
        String applicationId = decodedToken.split(":")[0];
        String applicationSecret = decodedToken.split(":")[1];
        application = ApplicationStore.getInstance().getApplication(applicationId);

        if(application == null || !application.getApplicationSecret().trim().equals(applicationSecret)) {
            throw new InvalidTokenException("Invalid token provided");
        }
        return application;
    }
}
