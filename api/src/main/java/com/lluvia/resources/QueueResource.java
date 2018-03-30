package com.lluvia.resources;

import com.kgcorner.lluvia.model.Application;
import com.kgcorner.lluvia.model.KQueue;
import com.lluvia.AuthServices;
import com.lluvia.KService;
import com.lluvia.KServiceFactory;
import com.lluvia.exception.ItemNotFoundException;
import com.kgcorner.util.Strings;
import com.lluvia.exception.UnauthorisedAccessException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RestController
public class QueueResource extends ExceptionAware {

    private KService service;
    private AuthServices authService;
    @Autowired
    private HttpServletRequest context;

    public QueueResource() {

        service = KServiceFactory.getService();
        authService = AuthServices.getInstance();
    }

    @ApiOperation("Creates a queue of given type, use 1 for FIFO, 2 for FIFO_ADJUST, 3 for IMMEDIATE")
    @PostMapping("queues")
    @ResponseStatus(HttpStatus.CREATED)
    public KQueue createQueue(
            @ApiParam(value = "Authorization Header", required = false)
            @RequestHeader(value = "Authorization", required = true) String authHeader,
            @ApiParam("Queue Type")
            @RequestParam("type") int type,
            @ApiParam("Comma Separated eventTags")
            @RequestParam("events") String tags
    ) {
        Application application = (Application) context.getAttribute("Application");
        if(!Strings.isNullOrEmpty(tags)) {
            List eventTags = Arrays.asList(tags.split(","));
            return service.addQueue(type, eventTags, application);
        } else {
            throw new IllegalArgumentException("events can't be empty");
        }
    }

    @ApiOperation("Add event to queue")
    @PostMapping("queues/{queueId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseObject addEvents(
            @ApiParam(value = "Authorization Header", required = false)
            @RequestHeader(value = "Authorization", required = true) String authHeader,
            @ApiParam("Queue id")
            @PathVariable("queueId") String queueId,
            @ApiParam("Tag to identify event")
            @RequestParam("event") String tag
    ) {
        Application application = (Application) context.getAttribute("Application");
        ResponseObject responseObject = null;
        KQueue queue = service.getQueue(queueId);
        if(queue == null) {
            throw new ItemNotFoundException("No Queue found with id:"+queueId);
        }
        if(Strings.isNullOrEmpty(tag)) {
            throw new IllegalArgumentException("tag name can't be empty");
        }

        if(!service.queueBelongsToApplication(application.getApplicationId(), queueId)) {
            throw new ItemNotFoundException("You don't have any such queue");
        }

        service.addEvent(queueId, tag);
        responseObject = new ResponseObject();
        responseObject.setMessage("Event created successfully");
        responseObject.setStatus(201);
        return responseObject;
    }


    @ApiOperation("Update event status")
    @PutMapping("queues/{queueId}/events/{tag}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject updateEventStatus(
            @ApiParam(value = "Authorization Header", required = false)
            @RequestHeader(value = "Authorization", required = true) String authHeader,
            @ApiParam("Queue id")
            @PathVariable("queueId") String queueId,
            @ApiParam("Tag to identify event")
            @PathVariable("tag") String tag,
            @ApiParam("Status 1 for completed, 0 for inprogress")
            @RequestParam("status") int status,
            @ApiParam(value = "Data in json format if status is completed", required = false)
            @RequestParam(value = "data", required = false) String data
    ) {
        Application application = (Application) context.getAttribute("Application");
        ResponseObject responseObject = null;
        KQueue queue = service.getQueue(queueId);
        if(queue == null) {
            throw new ItemNotFoundException("No Queue found with id:"+queueId);
        }
        if(Strings.isNullOrEmpty(tag)) {
            throw new IllegalArgumentException("tag name can't be empty");
        }

        if(!service.queueBelongsToApplication(application.getApplicationId(), queueId)) {
            throw new ItemNotFoundException("You don't have any such queue");
        }

        if(status == 1) {
            service.markEventCompleted(tag, data, queueId);
        }

        responseObject = new ResponseObject();
        responseObject.setMessage("event's status has been updated");
        responseObject.setStatus(200);
        return responseObject;
    }


    @ApiOperation("Subscribe to an event")
    @PostMapping("queues/{queueId}/events/{tag}/subscribers")
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject subscribeEvent(
            @ApiParam("Queue id")
            @PathVariable("queueId") String queueId,
            @ApiParam("Tag to identify event")
            @PathVariable("tag") String tag,
            @ApiParam(value = "Subscriber's endpoint address", required = true)
            @RequestParam(value = "endpoint", required = false) String endpoint,
            @ApiParam(value = "Subscriber's endpoint method", required = true)
            @RequestParam(value = "method", required = false) String method,
            @ApiParam(value = "Subscriber's endpoint content-type", required = true)
            @RequestParam(value = "content-type", required = false) String contentType

    ) {
        ResponseObject responseObject = null;
        KQueue queue = service.getQueue(queueId);
        if(queue == null) {
            throw new ItemNotFoundException("No Queue found with id:"+queueId);
        }
        if(Strings.isNullOrEmpty(tag)) {
            throw new IllegalArgumentException("tag name can't be empty");
        }
        if(Strings.isNullOrEmpty(endpoint)) {
            throw new IllegalArgumentException("endpoint name can't be empty");
        }
        if(Strings.isNullOrEmpty(method)) {
            throw new IllegalArgumentException("method name can't be empty");
        }
        if(Strings.isNullOrEmpty(contentType)) {
            throw new IllegalArgumentException("content-type name can't be empty");
        }
        service.addSubscriber(queueId, tag, endpoint, method, contentType);
        responseObject = new ResponseObject();
        responseObject.setMessage("Subscriber added successfully");
        responseObject.setStatus(200);
        return responseObject;
    }

    @ApiOperation("Creates or register an application")
    @PostMapping("applications")
    @ResponseStatus(HttpStatus.OK)
    public Application createApplication (
            @ApiParam("Application name")
            @RequestParam("name") String name,
            @ApiParam("Application description")
            @RequestParam("description") String description
    ) {
        Application application = new Application();
        application.setName(name);
        application.setDescription(description);
        return authService.addApplication(application);
    }
}
