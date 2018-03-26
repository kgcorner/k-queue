package com.lluvia;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockHandler {
    private static final Map<String, ReentrantReadWriteLock> LOCKS = new HashMap<>();

    public static ReadWriteLock getLock(String queueId) {
        if(LOCKS.containsKey(queueId)) {
            return LOCKS.get(queueId);
        } else {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            LOCKS.put(queueId, lock);
            return lock;
        }
    }

}
