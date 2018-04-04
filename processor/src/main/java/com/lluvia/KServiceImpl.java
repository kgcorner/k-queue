package com.lluvia;

import com.kgcorner.lluvia.data.ApplicationStore;
import com.kgcorner.lluvia.data.QueueStore;
import com.kgcorner.lluvia.model.Application;
import com.kgcorner.lluvia.model.Event;
import com.kgcorner.lluvia.model.KQueue;
import com.kgcorner.lluvia.model.Subscriber;
import com.lluvia.exception.IncompatibleQueueException;
import com.kgcorner.util.IDGenerator;
import com.kgcorner.util.JWTUtility;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

public class KServiceImpl implements KService {

    private static final QueueStore STORE = QueueStore.getInstance();
    private static final ApplicationStore APP_STORE = ApplicationStore.getInstance();
    private static KService INSTANCE;
    private static final Logger LOGGER = Logger.getLogger(KServiceImpl.class);

    private KServiceImpl() {}

    public static KService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new KServiceImpl();
        }
        return INSTANCE;
    }

    @Override
    public void addEvent(String queueID, String tag) {
        Event event = new Event(tag);
        KQueue queue = STORE.getKQueue(queueID);
        if(queue != null) {
            queue.addEvent(event);
        }
        else {
            throw new IllegalArgumentException("Can't find queue with id "+queueID);
        }
    }

    @Override
    public Event getEvent(String queueID, String tag) {
        Event event = null;
        KQueue queue = STORE.getKQueue(queueID);
        if(queue != null) {
            for(Event e : queue.getEvents()) {
                if(e.getTag() != null  && e.getTag().equals(tag)) {
                    event = e;
                }
            }
        }
        else {
            throw new IllegalArgumentException("Can't find queue with id "+queueID);
        }
        return event;
    }

    @Override
    public boolean removeEvent(String queueID, String tag) {
        boolean removed = false;
        KQueue queue = STORE.getKQueue(queueID);
        if(queue != null) {
            removed = queue.removeEvent(tag);
        }
        else {
            throw new IllegalArgumentException("Can't find queue with id "+queueID);
        }
        return removed;
    }


    @Override
    public KQueue addQueue(int type, List<String> eventTags, Application application) {
        KQueue.QUEUE_TYPE queueType ;
        switch (type) {
            case 1:
                queueType = KQueue.QUEUE_TYPE.FIFO;
                break;
            case 2:
                queueType = KQueue.QUEUE_TYPE.FIFO_ADJUST;
                break;
            case 3:
                queueType = KQueue.QUEUE_TYPE.IMMIDIATE;
                break;
                default:
                    throw new IllegalArgumentException("Invalid Queue type "+type);
        }
        String queueId = IDGenerator.generateId();
        String authString = JWTUtility.generateJWT(queueId);
        KQueue queue = KQueue.createQueue(queueId, queueType, eventTags, authString);
        queue.setCreatedAt(new Date());
        queue.setLastUpdatedAt(new Date());
        STORE.addKQueue(queueId, queue);
        STORE.mapQueueToApplication(application, queue.getId());
        return queue;
    }

    @Override
    public boolean removeQueue(String id) {
        return STORE.removeKQueue(id) != null;
    }

    @Override
    public KQueue getQueue(String id) {
        return STORE.getKQueue(id);
    }

    @Override
    public String getQueueSummary(String id) {
        return null;
    }

    @Override
    public void markEventCompleted(String tag, String data, String queueId) {
        KQueue queue = STORE.getKQueue(queueId);
        if(queue != null) {
            Event event = null;
            ReadWriteLock lock = LockHandler.getLock(queueId);
            lock.writeLock().lock();
            for(Event e : queue.getEvents()) {
                if(e.getTag().equals(tag)) {
                    e.setDoneAtSource(true);
                    e.setData(data);
                    Processor processor = ProcessorFactory.getProcessor(queue.getType());
                    try {
                        processor.process(queueId);
                    } catch (IncompatibleQueueException e1) {

                    }
                }
            }
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addSubscriber(String queueId, String eventTag, String endpoint, String method, String contentType) {
        KQueue queue = STORE.getKQueue(queueId);
        if(queue != null) {
            for (Event e : queue.getEvents()) {
                if(e.getTag().equals(eventTag)) {
                    Subscriber s = new Subscriber(endpoint, method, contentType);
                    e.getSubscribers().add(s);
                    break;
                }
            }
        }
    }

    @Override
    public boolean queueBelongsToApplication(String applicationId, String queueId) {
        return STORE.queueExistsInApplication(applicationId, queueId);
    }
}
