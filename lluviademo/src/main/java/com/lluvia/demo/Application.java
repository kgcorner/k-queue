package com.lluvia.demo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kgcorner.util.HttpUtil;
import com.kgcorner.util.Response;
import com.kgcorner.util.Strings;
import com.kgcorner.util.exception.NetworkException;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {
    private static final String EVENT_SUBS_URL = "http://localhost:9000/queues/%s/events/%s/subscribe";
    private static final String EVENT_RECV_URL = "http://localhost:8888/receive";
    private static final String EVENT_DETAIL_URL = "http://localhost:8080/queues";
    private static String queueId = null;
    private static final Logger LOGGER = Logger.getLogger(Application.class);
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
        try {
            Response response = HttpUtil.sendGetRequest(EVENT_DETAIL_URL, null);
            JsonElement jsonElement = new JsonParser().parse(response.getData());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            queueId = jsonObject.get("id").getAsString();
            subscribeEvent();
        } catch (NetworkException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static void subscribeEvent() {
        if(!Strings.isNullOrEmpty(queueId)) {
            Map<String, String> body = new HashMap<>();
            body.put("queueId", queueId);
            body.put("tag", "feed-liked");
            body.put("endpoint", EVENT_RECV_URL);
            body.put("method", "POST");
            body.put("content-type", "application/json");
            String url = String.format(EVENT_SUBS_URL, queueId, "feed-liked");
            try {
                Response response = HttpUtil.sendPostRequest(url, null, HttpUtil.getPostDataString(body));
                if(response.getStatus() == 200) {
                    LOGGER.info("Event subscribed");
                }
            } catch (NetworkException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        else{
            LOGGER.error("Queue is not initialized");
        }

    }
}
