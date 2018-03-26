package com.lluvia;

import com.kgcorner.lluvia.model.KQueue;

public class ProcessorFactory  {
    private static final Processor fifoProcessor = new FifoQueueProcessor();
    private static final Processor fifoAdjustProcessor = new AdjustableQueueProcessor();
    private static final Processor immediateProcessor = new ImmediateQueueProcessor();

    public static Processor getProcessor(KQueue.QUEUE_TYPE type) {
        switch (type) {
            case FIFO:
                return fifoProcessor;
            case IMMIDIATE:
                return immediateProcessor;
            case FIFO_ADJUST:
                return fifoAdjustProcessor;
        }
        return null;
    }
}
