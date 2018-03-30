package com.kgcorner.util;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

public class IDGenerator {
    private static final int NODE_ID = 0;
    public static String generateId() {
        return new BigInteger(130, new Random()).toString(32)+"."+NODE_ID;
    }

    public static String generateApplicationIdId() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
