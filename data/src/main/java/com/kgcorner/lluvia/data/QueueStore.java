package com.kgcorner.lluvia.data;

import com.google.gson.Gson;
import com.kgcorner.lluvia.model.Application;
import com.kgcorner.lluvia.model.KQueue;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Queue Store :D
 */
public class QueueStore {
    private final Map<String, KQueue> kQueueMap;
    private final Map<String, List<String>> applicationQueueMap;
    private static final QueueStore INSTANCE;
    private Date lastUpdate;

    private QueueStore() {
        this.kQueueMap = new ConcurrentHashMap<String, KQueue>();
        this.applicationQueueMap = new ConcurrentHashMap<String, List<String>>();
    }
    static {
        INSTANCE = new QueueStore();
    }

    /**
     * Returns Instance of Queue Store
     * @return
     */
    public static QueueStore getInstance() {
        return INSTANCE;
    }

    /**
     * Adds {@link KQueue} to store
     * @param id
     * @param queue
     */
    public void addKQueue(String id, KQueue queue) {
        this.lastUpdate = new Date();
        kQueueMap.put(id, queue);
    }

    /**
     * Returns KQueue from the Store
      * @param id
     * @return
     */
    public KQueue getKQueue(String id) {
        return kQueueMap.get(id);
    }

    /**
     * Remove and Return {@link KQueue} from store
     * @param id
     * @return removed {@link KQueue}
     */
    public KQueue removeKQueue(String id) {
        this.lastUpdate = new Date();
        return kQueueMap.remove(id);
    }

    public String getSummary() {
        Summary summary = new Summary();
        int fifoQueueCount = 0, adjustableQueueCount = 0, immediateQueueCount = 0;
        for(Map.Entry<String, KQueue> entry : kQueueMap.entrySet()) {
            switch (entry.getValue().getType()) {

                case FIFO:
                    fifoQueueCount++;
                    break;
                case IMMIDIATE:
                    immediateQueueCount++;
                    break;
                case FIFO_ADJUST:
                    adjustableQueueCount++;
                    break;
            }
        }
        summary.setAdjustableQueueCount(adjustableQueueCount);
        summary.setFifoQueueCount(fifoQueueCount);
        summary.setImmediateQueueCount(immediateQueueCount);
        summary.setLastUpdate(this.lastUpdate);
        Gson gson = new Gson();
        return gson.toJson(summary);
    }

    class Summary {
        private int fifoQueueCount;
        private int adjustableQueueCount;
        private int immediateQueueCount;
        private Date lastUpdate;

        public int getFifoQueueCount() {
            return fifoQueueCount;
        }

        public void setFifoQueueCount(int fifoQueueCount) {
            this.fifoQueueCount = fifoQueueCount;
        }

        public int getAdjustableQueueCount() {
            return adjustableQueueCount;
        }

        public void setAdjustableQueueCount(int adjustableQueueCount) {
            this.adjustableQueueCount = adjustableQueueCount;
        }

        public int getImmediateQueueCount() {
            return immediateQueueCount;
        }

        public void setImmediateQueueCount(int immediateQueueCount) {
            this.immediateQueueCount = immediateQueueCount;
        }

        public Date getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
    }

    /**
     * Map queue id to an application
     * @param application
     * @param queueId
     */
    public void mapQueueToApplication(Application application, String queueId) {
        List<String> ids = applicationQueueMap.get(application.getApplicationId());
        if(ids != null) {
            if(ids.contains(queueId)) {
                throw new IllegalArgumentException("Application allready contains this queue");
            }
        }
        else {
            ids = new ArrayList<>();
            applicationQueueMap.put(application.getApplicationId(), ids);
        }
        ids.add(queueId);
    }

    /**
     * Returns all queues added by an application
     * @param applicationId
     * @return
     */
    public List<KQueue> getQueuesOfApplication(String applicationId) {
        List<String> ids = applicationQueueMap.get(applicationId);
        List<KQueue> queues = null;
        if(ids != null) {
            queues = new ArrayList<>();
            for(String id : ids) {
                queues.add(kQueueMap.get(id));
            }
        }
        return queues;
    }

    public boolean queueExistsInApplication(String applicationId, String queueId) {
        boolean result = false;
        List<String> ids = applicationQueueMap.get(applicationId);
        if(ids != null) {
            result =  ids.contains(queueId);
        }
        return result;
    }
}
