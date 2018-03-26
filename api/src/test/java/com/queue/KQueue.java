package com.queue;

import java.util.Date;

public class KQueue {
    private String id;
    private QUEUE_TYPE type;
    private Date createdAt;
    private Date lastUpdatedAt;
    private String authString;

    /**
     * returns Auth String corresponding to {@link KQueue} for authorizing usage
     * @return
     */
    public String getAuthString() {
        return authString;
    }

    /**
     * Sets Auth String corresponding to {@link KQueue} for authorizing usage
     * @return
     */
    public void setAuthString(String authString) {
        this.authString = authString;
    }

    /**
     * returns when Queue was created
     * @return
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /** Sets when queue was created     *
     * @param createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns when queue was last updated
     * @return
     */
    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    /**
     * Sets when queue was last updated
     * @param lastUpdatedAt
     */
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    /**
     * Defines Type of the QUEUE
     */
    public enum QUEUE_TYPE {
        /**
         * events will be processed in FIFO Order
         */
        FIFO,
        /**
         * Events will be processed as soon as its completed
         */
        IMMIDIATE,
        /**
         * Event will be adjusted. When one event completes, it'll replace uncompleted event closest to head
         */
        FIFO_ADJUST
    }

    /**
     * returns id of the Queue
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Sets ID of the queue
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }
}

