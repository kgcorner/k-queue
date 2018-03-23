package com.kqueue.resources;

import com.kgcorner.kqueue.model.Event;
import com.kgcorner.kqueue.model.KQueue;
import com.kqueue.KService;
import com.kqueue.KServiceFactory;
import com.kqueue.exception.ItemNotFoundException;
import com.kqueue.exception.UnauthorisedAccessException;
import com.util.Strings;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
public class QueueResource extends ExceptionAware {

    private KService service;

    public QueueResource() {
        service = KServiceFactory.getService();
    }

    @ApiOperation("Creates a queue of given type, use 1 for FIFO, 2 for FIFO_ADJUST, 3 for IMMEDIATE")
    @PostMapping("queues")
    public KQueue createQueue(
            @ApiParam(value = "Authorization Header", required = false)
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @ApiParam("Queue Type")
            @RequestParam("type") int type,
            @ApiParam("Comma Separated eventTags")
            @RequestParam("events") String tags
    ) {

        if(Strings.isNullOrEmpty(tags)) {
            List eventTags = Arrays.asList(tags.split(","));
            return service.addQueue(type, eventTags);
        } else {
            throw new IllegalArgumentException("events can't be empty");
        }
    }

    @ApiOperation("Add event to queue")
    @PostMapping("queues/{queueId}/events")
    public ResponseObject addEvents(
            @ApiParam("Authorization Header")
            @RequestHeader("Authorization") String authHeader,
            @ApiParam("Queue Auth code")
            @RequestHeader("X-QUEUE-AUTH") String queueAuthCode,
            @ApiParam("Queue id")
            @PathVariable("queueId") String queueId,
            @ApiParam("Tag to identify event")
            @RequestParam("event") String tag
    ) {
        ResponseObject responseObject = null;
        KQueue queue = service.getQueue(queueId);
        if(queue == null) {
            throw new ItemNotFoundException("No Queue found with id:"+queueId);
        }
        if(!Strings.isNullOrEmpty(tag)) {
            throw new IllegalArgumentException("tag name can't be empty");
        }
        if(!Strings.isNullOrEmpty(queue.getAuthString()) && queue.getAuthString().equals(queueAuthCode)) {
            service.addEvent(queueId, tag);
            responseObject = new ResponseObject();
            responseObject.setMessage("Event created successfully");
            responseObject.setStatus(201);
        }
        else {
            throw new UnauthorisedAccessException("Invalid Queue authcode provided");
        }
        return responseObject;
    }


    @ApiOperation("Update queue status")
    @PostMapping("queues/{queueId}/events/{tag}")
    public ResponseObject updateEventStatus(
            @ApiParam("Authorization Header")
            @RequestHeader("Authorization") String authHeader,
            @ApiParam("Queue Auth code")
            @RequestHeader("X-QUEUE-AUTH") String queueAuthCode,
            @ApiParam("Queue id")
            @PathVariable("queueId") String queueId,
            @ApiParam("Tag to identify event")
            @PathVariable("tag") String tag,
            @ApiParam("Status 1 for completed, 0 for inprogress")
            @RequestParam("status") int status,
            @ApiParam(value = "Data in json format if status is completed", required = false)
            @RequestParam(value = "data", required = false) String data
    ) {
        ResponseObject responseObject = null;
        KQueue queue = service.getQueue(queueId);
        if(queue == null) {
            throw new ItemNotFoundException("No Queue found with id:"+queueId);
        }
        if(!Strings.isNullOrEmpty(tag)) {
            throw new IllegalArgumentException("tag name can't be empty");
        }
        if(!Strings.isNullOrEmpty(queue.getAuthString()) && queue.getAuthString().equals(queueAuthCode)) {

        }
        else {
            throw new UnauthorisedAccessException("Invalid Queue authcode provided");
        }
        return responseObject;
    }
}
