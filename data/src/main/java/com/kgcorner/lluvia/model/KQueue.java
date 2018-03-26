package com.kgcorner.lluvia.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class KQueue {
    private String id;
    private QUEUE_TYPE type;
    //private List<Event> events;
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

    public static KQueue createQueue(String id, QUEUE_TYPE type, List<String> tags, String authString) {
        KQueue queue = null;
        switch (type) {

            case FIFO:
                queue = new FifoKQueue();
                break;
            case IMMIDIATE:
                queue = new ImmediateKQueue();
                break;
            case FIFO_ADJUST:
                queue = new AdjustableKQueue();
                break;
        }
        queue.type = type;
        queue.setId(id);
        queue.setAuthString(authString);
        queue.setCreatedAt(new Date());
        queue.setLastUpdatedAt(new Date());
        for(String tag : tags) {
            Event event = new Event(tag);
            queue.addEvent(event);
        }
        return queue;
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

    /**
     * returns type of the queue
     * @return
     */
    public abstract QUEUE_TYPE getType();

    /**
     * get list of events in the queue
     * @return
     */
    public abstract Collection<Event> getEvents();

    /**
     * returns head of the queue. Should be used with FIFO and FIFO Adjust
     * @return
     */
    public Event getHead() {
        return null;
    }


    /**
     * Rearranges items of the FIFO Adjust Queue by placing completed event in front. Does nothing in other two queues
     * @param tag
     * @return
     */
    public void reArrange() {
        return;
    }

    /**
     * Add event into queue
     * @param event
     */
    public abstract void addEvent(Event event);

    public Boolean removeEvent(String tag) {
        Event event = new Event(tag);
        return this.getEvents().remove(event);
    }

    static class FifoKQueue extends KQueue {

        private Queue<Event> events;
        private Map<String, Event> cashMap;

        public FifoKQueue() {
            this.events = new ConcurrentLinkedQueue<Event>();
            this.cashMap = new ConcurrentHashMap<String, Event>();
        }

        public QUEUE_TYPE getType() {
            return QUEUE_TYPE.FIFO;
        }

        public Queue<Event> getEvents() {
            return events;
        }

        public void addEvent(Event event) {
            this.events.add(event);
            this.cashMap.put(event.getTag(), event);
        }

        @Override
        public Event getHead() {
            return this.events.peek();
        }
    }

    static class AdjustableKQueue extends KQueue {

        private List<Event> events;


        public AdjustableKQueue() {
            this.events = new CopyOnWriteArrayList<Event>();
        }

        public QUEUE_TYPE getType() {
            return QUEUE_TYPE.FIFO_ADJUST;
        }

        public List<Event> getEvents() {
            return events;
        }

        public void addEvent(Event event) {
            this.events.add(event);
        }


        @Override
        public void reArrange() {
            List<Event> done = new ArrayList<>();
            List<Event> waiting = new ArrayList<>();
            for(Event e : events) {
                if(e.isDoneAtSource()) {
                    done.add(e);
                }
                else {
                    waiting.add(e);
                }
            }
            events = new CopyOnWriteArrayList<>();
            events.removeAll(done);
            events.removeAll(waiting);
        }

        @Override
        public Event getHead() {
            if(events.size()>0) {
                return events.get(0);
            }
            else{
                return null;
            }
        }
    }

    static class ImmediateKQueue extends KQueue {

        private Map<String, Event> events;


        public ImmediateKQueue() {
            this.events = new ConcurrentHashMap<String, Event>();
        }

        public QUEUE_TYPE getType() {
            return QUEUE_TYPE.IMMIDIATE;
        }

        public Collection<Event> getEvents() {
            return events.values();
        }

        public void addEvent(Event event) {
            this.events.put(event.getTag(), event);
        }

        @Override
        public Boolean removeEvent(String tag) {
            return events.remove(tag) != null;
        }
    }

}
