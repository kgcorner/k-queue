package com.lluvia;

import com.kgcorner.lluvia.data.ApplicationStore;
import com.kgcorner.lluvia.model.Application;
import com.kgcorner.util.IDGenerator;
import com.kgcorner.util.JWTUtility;
import com.kgcorner.util.Strings;
import com.lluvia.exception.InvalidTokenException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by admin on 3/30/2018.
 */
public class AuthServices {
    private static final AuthServices INSTANCE = new AuthServices();
    private final ApplicationStore APP_STORE;
    private static final int EXPIRES_IN_MIL_SEC = 15*60*1000;
    private static final String APP_ID = "appID";
    private static final String TIME = "time";
    private static final String EXPIRE = "expire";
    private AuthServices() {
        APP_STORE = ApplicationStore.getInstance();
    }

    public static AuthServices getInstance() {
        return INSTANCE;
    }

    /**
     * Adds application to application store after generating appid and secret
     * @param application
     */
    public Application addApplication(Application application) {
        application = generateApplicationCredentials(application);
        APP_STORE.addApplication(application);
        return application;
    }

    /**
     * Get application using application id
     * @param applicationId
     * @return
     */
    public Application getApplication(String applicationId) {
        return APP_STORE.getApplication(applicationId);
    }

    /**
     * Set Bearer token againt application
     * @param bearerToken
     * @param applicationId
     */
    public void setBearer(String bearerToken, String applicationId) {
        APP_STORE.setBearer(bearerToken, applicationId);
    }

    /**
     * Get application token using bearer
     * @param bearerToken
     * @return
     */
    public Application getApplicationByBearer(String bearerToken) {
        return APP_STORE.getApplicationByBearer(bearerToken);
    }

    /**
     * generates and assign credentials to application and return the same
     * @param application
     * @return
     */
    public Application generateApplicationCredentials(Application application) {
        String newId = IDGenerator.generateApplicationIdId();
        String secret = IDGenerator.generateId();
        application.setApplicationId(newId);
        application.setApplicationSecret(secret);
        return application;
    }

    /**
     * Generate Bearer token for the application
     * @param application
     * @return
     */
    public String generateBearerToken(Application application) {
        String token = null;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(APP_ID, application.getApplicationId());
        jsonObject.put(TIME, new Date().getTime());
        jsonObject.put(EXPIRE, EXPIRES_IN_MIL_SEC);
        String payload = jsonObject.toString();
        token = JWTUtility.generateJWT(payload);
        return token;
    }

    /**
     * Verifies and return the application using bearer token
     * @param bearerToken
     * @return
     * @throws InvalidTokenException
     */
    public Application verifyAndReturn(String bearerToken) throws InvalidTokenException {
        Application application = null;
        if(Strings.isNullOrEmpty(bearerToken)) {
            throw new IllegalArgumentException("Token can't be null or empty");
        }
        String payload = JWTUtility.verify(bearerToken);
        if(Strings.isNullOrEmpty(payload)) {
            throw new InvalidTokenException("Invalid bearer token is provided");
        }
        JSONObject jsonObject = new JSONObject(payload);
        String applicationID = jsonObject.getString(APP_ID);
        long createdAt = jsonObject.getLong(TIME);
        long expire = jsonObject.getLong(EXPIRE);
        long now = new Date().getTime();
        if(( now - createdAt ) <= expire ) {
            application = getApplication(applicationID);
        }
        else {
            throw new InvalidTokenException("Timeout expired");
        }
        return application;
    }


}
