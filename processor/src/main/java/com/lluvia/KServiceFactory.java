package com.lluvia;

public class KServiceFactory {
    public static KService getService() {
        return KServiceImpl.getInstance();
    }
}
