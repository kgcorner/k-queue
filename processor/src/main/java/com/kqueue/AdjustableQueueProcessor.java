package com.kqueue;

import com.kgcorner.kqueue.data.QueueStore;
import com.kgcorner.kqueue.model.Event;
import com.kgcorner.kqueue.model.KQueue;
import com.kqueue.exception.IncompatibleQueueException;
import com.kqueue.threadpool.KQueueExecutor;

import java.util.concurrent.locks.ReadWriteLock;

public class AdjustableQueueProcessor implements Processor {
    private KQueue queue = null;
    public void process(String queueId) throws IncompatibleQueueException {
        KQueue queue = QueueStore.getInstance().getKQueue(queueId);
        if(queue.getType() == KQueue.QUEUE_TYPE.FIFO_ADJUST) {
            Thread thread = new Thread(this);
            thread.start();
        }
        else {
            throw new IncompatibleQueueException("Expected queue type KQueue.QUEUE_TYPE.FIFO_ADJUST");
        }
    }

    @Override
    public void run() {
        ReadWriteLock lock = LockHandler.getLock(this.queue.getId());
        lock.writeLock().lock();
        for(Event e : queue.getEvents()) {
            if(e.isDoneAtSource() && !e.isProcessed() && !e.isProcessing()) {
                e.setProcessing(true);
                KQueueExecutor.getInstance().broadCastEvent(e, KQueue.QUEUE_TYPE.FIFO_ADJUST);
                queue.removeEvent(e.getTag());
            }
        }
    }
}
