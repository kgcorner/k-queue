package com.util;

import java.math.BigInteger;
import java.util.Random;

public class IDGenerator {
    private static final int NODE_ID = 0;
    public static String generateQueueId() {
        return new BigInteger(130, new Random()).toString(32)+"."+NODE_ID;
    }
}
