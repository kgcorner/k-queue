package com.kgcorner.kqueue.model;

import java.util.List;

public class Event {
    private final String tag;
    private List<Subscriber> subscribers;
    private boolean isDoneAtSource;
    private boolean isProcessed;
    private boolean broadCastingFailed;
    private String data;
    private boolean processing;

    public Event(String tag) {
        this.tag = tag;
    }

    /**
     * returns Tags to recognize the event
     * @return
     */
    public String getTag() {
        return tag;
    }


    /**
     * returns List of Subscribers who has subscribed to this event
     * @return
     */
    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    /**
     * sets List of subscribers
     * @param subscribers
     */
    public void setSubscribers(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }

    /**
     * returns flag that denotes if the event is completed at source
     * @return
     */
    public boolean isDoneAtSource() {
        return isDoneAtSource;
    }

    /**
     * sets flag to denote if the event is completed at source
     * @param doneAtSource
     */
    public void setDoneAtSource(boolean doneAtSource) {
        isDoneAtSource = doneAtSource;
    }

    /**
     * Returns flag that denotes if the event is processed in k-Queue
     * @return
     */
    public boolean isProcessed() {
        return isProcessed;
    }

    /**
     * Sets flag that denotes if the event is processed in k-Queue
     * @return
     */
    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    @Override
    public String toString() {
        return "Event{" +
                "tag='" + tag + '\'' +
                ", isDoneAtSource=" + isDoneAtSource +
                ", isProcessed=" + isProcessed +
                ", data=" + data +
                '}';
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isBroadCastingFailed() {
        return broadCastingFailed;
    }

    public void setBroadCastingFailed(boolean broadCastingFailed) {
        this.broadCastingFailed = broadCastingFailed;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if(obj instanceof Event) {
            Event e = (Event) obj;
            result = e.getTag().equals(this.tag);
        }
        return result;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }
}
