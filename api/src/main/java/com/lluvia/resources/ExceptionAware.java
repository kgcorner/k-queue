package com.lluvia.resources;


import com.lluvia.exception.ItemNotFoundException;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ExceptionAware {
    private static final Logger LOGGER = Logger.getLogger(ExceptionAware.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException x) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", x.getMessage());
        jsonObject.put("code", 400);
        return jsonObject.toString();
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleItemNotFoundException(ItemNotFoundException x) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", x.getMessage());
        jsonObject.put("code", 404);
        return jsonObject.toString();
    }


}
