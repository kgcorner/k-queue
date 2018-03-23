package com.kqueue;

import com.kgcorner.kqueue.model.Event;
import com.kgcorner.kqueue.model.KQueue;

import java.util.List;

public interface KService {
    /**
     * Add event to given {@link KQueue}
     * @param queueID id of the queue into which event will be added
     * @param tag
     */
    void addEvent(String queueID, String tag);

    /**
     * Get event
     * @param queueID id of the {@link KQueue} to which event belong
     * @param tag tag of the event
     * @return
     */
    Event getEvent(String queueID, String tag);

    /**
     * Remove and returns the event
     * @param queueID id of the {@link KQueue} to which event belong
     * @param tag tag of the event
     * @return
     */
    boolean removeEvent(String queueID, String tag);

    /**
     * Add Queue to store
     * @param type
     * @param eventTags
     */
    KQueue addQueue(int type, List<String> eventTags);

    /**
     * Removes queue from store
     * @param id
     * @return
     */
    boolean removeQueue(String id);

    /**
     * get Queue
     * @param id
     * @return
     */
    KQueue getQueue(String id);

    /**
     * get Queue Summary
     * @param id
     * @return
     */
    String getQueueSummary(String id);

    /**
     * marks event completed and start the broadcasting thread
     * @param tag event's tag name
     * @param data data needed to send to subscribers
     * @param queueId
     */
    void markEventCompleted(String tag, String data, String queueId);
}
