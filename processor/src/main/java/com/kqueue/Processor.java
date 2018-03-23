package com.kqueue;

import com.kqueue.exception.IncompatibleQueueException;

public interface Processor extends Runnable{
    void process(String queueId) throws IncompatibleQueueException;
}
