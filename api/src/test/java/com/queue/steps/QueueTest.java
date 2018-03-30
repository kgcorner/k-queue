package com.queue.steps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.queue.*;
import com.kgcorner.util.HttpUtil;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


@ConfigTest
public class QueueTest {
    private static final Logger LOGGER = Logger.getLogger(QueueTest.class);
    private static String dataPayload = null;
    private String eventTags = null;
    private static final String QUEUE_URL_CREATE_QUEUE ="http://localhost:9000/queues";
    private static final String QUEUE_URL_SUBSCRIBE_EVENT ="http://localhost:9000/queues/%s/events/%s/subscribe";
    private static final String QUEUE_URL_MARK_COMPLETE ="http://localhost:9000/queues/%s/events/%s";
    private static String queueId = null;
    private static String authHeader = null;
    private static String event_to_test = null;
    private static final String QUEUE_AUTH_HEADER = "X-QUEUE-AUTH";
    private String MOCKED_URL = "http://localhost:8000/receive";


    @Before
    public void stepUp() {
        MockServer.createServer(8000);
        String url = "/receive";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data","value");
            dataPayload = jsonObject.toString();
            MockServer.mockNewUrl(url, null, dataPayload, "POST");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @When("^a set of events like \"([^\"]*)\" \"([^\"]*)\" and \"([^\"]*)\" used for creating a queue$")
    public void aSetOfEventsLikeAndUsedForCreatingAQueue(String arg0, String arg1, String arg2) throws Throwable {
        eventTags = arg0+","+arg1+","+arg2;
        event_to_test = arg0;
    }

    @Then("^api should return '(\\d+)' as status$")
    public void apiShouldReturnAsStatus(int status) throws Throwable {
        Map<String, String> body = new HashMap<>();
        body.put("type","3");
        body.put("events",eventTags);
        try {
            Response response = HttpUtilForTests.getPostResponse(QUEUE_URL_CREATE_QUEUE, null,
                    HttpUtil.getPostDataString(body));

            Assert.assertNotNull("Response is  null", response);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm").create();
            com.queue.KQueue queue = gson.fromJson(response.getData(), com.queue.KQueue.class);
            queueId = queue.getId();
            authHeader = queue.getAuthString();
            Assert.assertNotNull("Response data is  null", response.getData());
            Assert.assertEquals("status is different", status, response.getStatus());
        } catch (NetworkException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @When("^events is subscribed with given queue id$")
    public void eventsIsSubscribedWithGivenQueueId() throws Throwable {
        Assert.assertNotNull(queueId);
    }


    @Then("^subscriber endpoint should return '(\\d+)' as status$")
    public void subscriberEndpointShouldReturnAsStatus(int status) throws Throwable {
        String url = String.format(QUEUE_URL_SUBSCRIBE_EVENT, queueId, event_to_test);
        Map<String, String> body = new HashMap<>();
        body.put("queueId", queueId);
        body.put("tag", "feed-liked");
        body.put("endpoint", MOCKED_URL);
        body.put("method", "POST");
        body.put("content-type", "application/json");
        try {
            Response response = HttpUtilForTests.getPostResponse(url, null, HttpUtil.getPostDataString(body));
            Assert.assertNotNull("Response is  null", response);
            Assert.assertNotNull("Response data is  null", response.getData());
            Assert.assertEquals("status is different", status, response.getStatus());
        } catch (NetworkException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    @Then("^it should be bradcasted to subscriber$")
    public void itShouldBeBradcastedToSubscriber() throws Throwable {
        LOGGER.info("Waiting for 2000ms for event to be broadcasted");
        Thread.sleep(2000);
        Assert.assertTrue(MockServer.receivedBraodCast);
    }

    @When("^Subscribed event is marked as completed and server returns '(\\d+)' as status$")
    public void subscribedEventIsMarkedAsCompletedAndServerReturnsAsStatus(int status) throws Throwable {
        String url = QUEUE_URL_MARK_COMPLETE;
        url = String.format(url, queueId, event_to_test);
        Map<String, String> header = new HashMap<>();
        header.put(QUEUE_AUTH_HEADER, authHeader);
        header.put("content-type", "application/x-www-form-urlencoded");
        Map<String, String> body = new HashMap<>();
        body.put("status", "1");
        body.put("data", dataPayload);
        try {
            Response response = HttpUtilForTests.getPutResponse(url, header, HttpUtil.getPostDataString(body));
            Assert.assertNotNull("Response is  null", response);
            Assert.assertNotNull("Response data is  null", response.getData());
            Assert.assertEquals("status is different", status, response.getStatus());
            LOGGER.info("Feed create event sent");
        } catch (NetworkException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
