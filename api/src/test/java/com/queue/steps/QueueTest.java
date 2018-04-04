package com.queue.steps;

import com.google.gson.*;
import com.kgcorner.lluvia.model.Application;
import com.queue.*;
import com.kgcorner.util.HttpUtil;
import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.it.Ma;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@ConfigTest
public class QueueTest {
    private static final Logger LOGGER = Logger.getLogger(QueueTest.class);
    private static String dataPayload = null;
    private String eventTags = null;
    private static final String APPLICATION_CREATE_URL ="http://localhost:9000/applications";
    private static final String GET_TOKEN_URL ="http://localhost:9000/tokens";
    private static final String QUEUE_URL_CREATE_QUEUE ="http://localhost:9000/queues";
    private static final String QUEUE_URL_SUBSCRIBE_EVENT ="http://localhost:9000/queues/%s/events/%s/subscribers";
    private static final String QUEUE_URL_MARK_COMPLETE ="http://localhost:9000/queues/%s/events/%s";
    private static String queueId = null;
    private static String authHeader = null;
    private static String event_to_test = null;
    private static final String QUEUE_AUTH_HEADER = "X-QUEUE-AUTH";
    private String MOCKED_URL = "http://localhost:8000/receive";
    private String name ="";
    private String validName ="";
    private String bearerToken = null;
    private static Application CREATED_APPLICATION = null;
    private static String BASIC_TOKEN = null;
    private static String BEARER_TOKEN = null;
    private static String INVALID_BEARER_TOKEN = null;


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

    @When("^invalid name is given as \"([^\"]*)\"$")
    public void nameIsGivenAs(String invalidName) throws Throwable {
        this.name = invalidName;
    }

    @When("^valid name is given as \"([^\"]*)\"$")
    public void validNameIsGivenAs(String validName) throws Throwable {
        this.name = validName;
    }

    @Then("^Request for create application should return '(\\d+)' status$")
    public void requestForCreateApplicationShouldReturnStatus(int status) throws Throwable {
        Map<String, String> map = new HashMap<>();
        map.put("name", this.name);
        map.put("description","");

        Response response = HttpUtilForTests.getPostResponse(APPLICATION_CREATE_URL, null,
                HttpUtilForTests.getPostDataString(map));
        Assert.assertEquals("Application creates request with invalid name does not match",
                status, response.getStatus());
        if(response.getStatus() == 201) {
            Gson gson = new Gson();
            CREATED_APPLICATION = gson.fromJson(response.getData(), Application.class);
            if(CREATED_APPLICATION == null) {
                LOGGER.error("FAILED TO RECREATE APPLICATION");
                LOGGER.error("RESPONSE:"+response.getData());
            }
            else {
                LOGGER.info("Application recreated");
            }
        }
    }

    @When("^a set of events like \"([^\"]*)\" \"([^\"]*)\" and \"([^\"]*)\" used for creating a queue$")
    public void aSetOfEventsLikeAndUsedForCreatingAQueue(String arg0, String arg1, String arg2) throws Throwable {
        eventTags = arg0+","+arg1+","+arg2;
        event_to_test = arg0;
    }

    @Then("^api should return '(\\d+)' as status$")
    public void apiShouldReturnAsStatus(int status) throws Throwable {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization","BEARER "+BEARER_TOKEN);
        Map<String, String> body = new HashMap<>();
        body.put("type","3");
        body.put("events",eventTags);
        try {
            Response response = HttpUtilForTests.getPostResponse(QUEUE_URL_CREATE_QUEUE, header,
                    HttpUtil.getPostDataString(body));
            Assert.assertNotNull("Response is  null", response);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm").create();
            com.queue.KQueue queue = gson.fromJson(response.getData(), com.queue.KQueue.class);
            queueId = queue.getId();
            authHeader = queue.getAuthString();
            LOGGER.error("Bearer token BEARER"+BEARER_TOKEN);
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
        header.put("Authorization","BEARER "+BEARER_TOKEN);

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


    @When("^Given invalid application id \"([^\"]*)\" and \"([^\"]*)\"$")
    public void givenInvalidApplicationIdAnd(String applicationId, String applicationSecret) throws Throwable {
        String base64Val = Base64.getEncoder().encodeToString((applicationId+":"+applicationSecret).getBytes());
        BASIC_TOKEN = "BASIC "+base64Val;
    }

    @Then("^Api should return response with status '(\\d+)'$")
    public void apiShouldReturnResponseWithStatus(int status) throws Throwable {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", BASIC_TOKEN);
        Response response = HttpUtilForTests.getGetResponse(GET_TOKEN_URL, header);
        Assert.assertEquals("Status is not matching", status, response.getStatus());
        if(response.getStatus() == 200) {
            LOGGER.info("response:"+response.getData());
            JSONObject jsonObject = new JSONObject(response.getData());
            BEARER_TOKEN = jsonObject.getString("token");
            LOGGER.info("BEARER TOKEN received:"+BEARER_TOKEN);
            //LOGGER.info("Token's response:"+);
        }
    }

    @When("^Given valid application id and secret$")
    public void givenValidApplicationIdAndSecret() throws Throwable {
        String base64Val = Base64.getEncoder().encodeToString((CREATED_APPLICATION.getApplicationId()+":"
                +CREATED_APPLICATION.getApplicationSecret()).getBytes());
        BASIC_TOKEN = "BASIC "+base64Val;
    }
}
