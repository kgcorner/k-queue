package com.queue;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.PostServeAction;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.queue.steps.QueueTest;
import org.apache.log4j.Logger;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MockServer {
    private static final Logger LOGGER = Logger.getLogger(MockServer.class);
    private static WireMockServer mockServer;
    private static WireMockConfiguration wireMockConfiguration;
    public static boolean receivedBraodCast = false;
    public static void createServer(int port) {
        if(mockServer == null) {
            WireMockConfiguration configuration = WireMockConfiguration.options().port(port);
            configuration.extensions(new PostServeAction() {
                @Override
                public String getName() {
                    return "post serve action";
                }

                @Override
                public void doGlobalAction(ServeEvent serveEvent, Admin admin) {
                    LOGGER.info("PostServeAction invoked");
                    receivedBraodCast = true;
                }
            });
            mockServer = new WireMockServer(configuration);
            mockServer.start();
            configureFor(port);
        }
    }
    public static void mockNewUrl(String urlPath, Map<String, String> queries, String responseBody) {
        MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath));
        if(queries != null) {
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                mappingBuilder.withQueryParam(entry.getKey(), matching("^.+$"));
            }
        }
        mappingBuilder.willReturn(aResponse().withBody(responseBody));
        StubMapping stubMapping = stubFor(mappingBuilder);
        mockServer.addStubMapping(stubMapping);
    }


    public static void mockNewUrl(String urlPath, Map<String, String> queries, String responseBody, String method) {
        MappingBuilder mappingBuilder = null;
        switch (method) {
            case "GET":
                mappingBuilder = get(urlPathEqualTo(urlPath));
                break;
            case "POST":
                mappingBuilder = post(urlPathEqualTo(urlPath));
                break;
        }
        if(queries != null) {
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                mappingBuilder.withQueryParam(entry.getKey(), matching("^.+$"));
            }
        }
        mappingBuilder.willReturn(aResponse().withBody(responseBody));
        StubMapping stubMapping = stubFor(mappingBuilder);
        mockServer.addStubMapping(stubMapping);
        //mockServer.givenThat()
    }

    public static void mockNewUrl(String urlPath, Map<String, String> queries, String responseBody, String method, int status, String requestBody) {
        MappingBuilder mappingBuilder = null;
        switch (method) {
            case "GET":
                mappingBuilder = get(urlPathEqualTo(urlPath));
                break;
            case "POST":
                mappingBuilder = post(urlPathEqualTo(urlPath));
                break;
        }
        if(queries != null) {
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                mappingBuilder.withQueryParam(entry.getKey(), matching("^.+$"));
            }
        }
        if(requestBody != null) {
            mappingBuilder.withRequestBody(equalTo(requestBody));
        }
        mappingBuilder.willReturn(aResponse().withBody(responseBody).withStatus(status));
        StubMapping stubMapping = stubFor(mappingBuilder);
        mockServer.addStubMapping(stubMapping);
    }

    public static void mockNewUrl(String urlPath, Map<String, String> queries, String responseBody, int responseCode) {
        MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath));
        if(queries != null) {
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                mappingBuilder.withQueryParam(entry.getKey(), matching("^.+$"));
            }
        }
        mappingBuilder.willReturn(aResponse().withBody(responseBody).withStatus(responseCode));
        StubMapping stubMapping = stubFor(mappingBuilder);
        mockServer.addStubMapping(stubMapping);
    }

    public static void mockNewUrl(String urlPath, Map<String, String> queries, String responseBody, int responseCode, String method) {
        MappingBuilder mappingBuilder = null;
        switch (method) {
            case "GET":
                mappingBuilder = get(urlPathEqualTo(urlPath));
                break;
            case "POST":
                mappingBuilder = post(urlPathEqualTo(urlPath));
                break;
        }
        if(queries != null) {
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                mappingBuilder.withQueryParam(entry.getKey(), matching("^.+$"));
            }
        }

        mappingBuilder.willReturn(aResponse().withBody(responseBody).withStatus(responseCode));
        StubMapping stubMapping = stubFor(mappingBuilder);
        mockServer.addStubMapping(stubMapping);
    }



    public static void shutdown() {
        if(mockServer.isRunning())
            mockServer.shutdown();
    }

    public static void registerExtention() {

    }
}