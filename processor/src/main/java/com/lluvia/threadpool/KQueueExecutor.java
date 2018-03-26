package com.lluvia.threadpool;

import com.kgcorner.lluvia.model.Event;
import com.kgcorner.lluvia.model.KQueue;
import com.lluvia.BroadCaster;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Queue Executer
 */
public class KQueueExecutor {

    private static final Logger LOGGER = Logger.getLogger(KQueueExecutor.class);
    private ExecutorService fifoExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService fifoAdjustExecutor = Executors.newFixedThreadPool(5);
    private ExecutorService immediateExecutor = Executors.newCachedThreadPool();
    private static KQueueExecutor INSTANCE = new KQueueExecutor();
    private KQueueExecutor() {

    }

    public static KQueueExecutor getInstance() {
        return INSTANCE;
    }
    /**
     * Broadcasts the event. How the event will be broadcasted will be defined by queue type
     * @param event {@link Event} to broardcast
     * @param type {@link com.kgcorner.lluvia.model.KQueue.QUEUE_TYPE} containing this event
     */
    public void broadCastEvent(Event event, KQueue.QUEUE_TYPE type) {
        BroadCaster broadCaster = new BroadCaster(event);
        switch (type) {
            case FIFO:
                fifoExecutor.submit(broadCaster);
                break;
            case IMMIDIATE:
                immediateExecutor.submit(broadCaster);
                break;
            case FIFO_ADJUST:
                fifoAdjustExecutor.submit(broadCaster);
                break;
        }

    }




    class DefaultUnCaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        private String queueType;
        public DefaultUnCaughtExceptionHandler(String queueType) {
            this.queueType = queueType;
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
              LOGGER.info("Broadcasting failed for queue type:"+ queueType);
        }
    }
}
