package com.lluvia;

import com.kgcorner.lluvia.model.Event;
import com.kgcorner.lluvia.model.Subscriber;
import com.util.HttpUtil;
import com.util.Response;
import com.util.exception.NetworkException;
import org.apache.log4j.Logger;

import java.util.Date;

public class BroadCaster implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(BroadCaster.class);

    private final Event event;

    public BroadCaster(Event event) {
        this.event = event;
    }

    public void run() {
        if(event.isDoneAtSource()) {
            LOGGER.debug("Broadcasting Event");
            //Map<String, String> headers = new HashMap<String, String>();
            for(Subscriber subscriber : event.getSubscribers()) {
                Response response = null;
                try {
                    switch (subscriber.getMethod().toLowerCase()) {
                        case "post":
                            response = HttpUtil.sendPostRequest(subscriber.getEndpointAddress(), null, event.getData());
                            break;
                        case "get":
                            response = HttpUtil.sendGetRequest(subscriber.getEndpointAddress(), null);
                            break;
                    }
                    subscriber.setInformed(true);
                    subscriber.setInformedAt(new Date());
                    LOGGER.debug("Subscriber informed about event :"+event.getTag()+" received response :" +response.getData());
                    event.setProcessed(true);
                    event.setProcessing(false);
                    event.setDoneAtSource(false);
                }
                catch (NetworkException x) {
                    LOGGER.error(x.getMessage(), x);
                    event.setBroadCastingFailed(true);
                    throw new RuntimeException(x.getMessage());
                }
            }
        }
        else {
            LOGGER.error("Rejecting event broadcast as event is not completed at source");
            event.setBroadCastingFailed(true);
            throw new IllegalArgumentException("Rejecting event broadcast as event is not completed at source");
        }

    }
}
