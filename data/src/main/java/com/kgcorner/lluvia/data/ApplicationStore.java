package com.kgcorner.lluvia.data;

import com.kgcorner.lluvia.model.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 3/28/2018.
 */
public class ApplicationStore {
    private static final ApplicationStore INSTANCE = new ApplicationStore();
    private final Map<String, Application> APPLICATIONS;

    private ApplicationStore() {
        APPLICATIONS = new HashMap<>();
    }

    public ApplicationStore getInstance() {
        return INSTANCE;
    }

    public void addApplication(Application application) {
        this.APPLICATIONS.put(application.getApplicationId(), application);
    }

    public Application getApplication(String applicationId) {
        return APPLICATIONS.get(applicationId);
    }

}
