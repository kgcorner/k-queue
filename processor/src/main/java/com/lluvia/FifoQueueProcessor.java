package com.lluvia;

import com.kgcorner.lluvia.data.QueueStore;
import com.kgcorner.lluvia.model.Event;
import com.kgcorner.lluvia.model.KQueue;
import com.lluvia.exception.IncompatibleQueueException;
import com.lluvia.threadpool.KQueueExecutor;

import java.util.concurrent.locks.ReadWriteLock;

public class FifoQueueProcessor implements Processor {

    private KQueue queue = null;
    public void process(String queueId) throws IncompatibleQueueException {
        KQueue queue = QueueStore.getInstance().getKQueue(queueId);
        queue.reArrange();
        if(queue.getType() == KQueue.QUEUE_TYPE.FIFO) {
            Thread thread = new Thread(this);
            thread.start();
        }
        else {
            throw new IncompatibleQueueException("Expected queue type KQueue.QUEUE_TYPE.FIFO");
        }
    }

    @Override
    public void run() {
        ReadWriteLock lock = LockHandler.getLock(this.queue.getId());
        lock.writeLock().lock();
        Event e = queue.getHead();
        if(e.isDoneAtSource() && !e.isProcessed() && !e.isProcessing()) {
            e.setProcessing(true);
            KQueueExecutor.getInstance().broadCastEvent(e, KQueue.QUEUE_TYPE.FIFO);
            queue.removeEvent(e.getTag());
        }
        lock.writeLock().unlock();
    }
}
