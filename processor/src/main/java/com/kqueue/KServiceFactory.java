package com.kqueue;

public class KServiceFactory {
    public static KService getService() {
        return KServiceImpl.getInstance();
    }
}
