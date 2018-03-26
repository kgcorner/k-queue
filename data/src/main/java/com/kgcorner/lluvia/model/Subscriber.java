package com.kgcorner.lluvia.model;

import java.util.Date;

public class Subscriber {
    private String endpointAddress;
    private String method;
    private String contentType;
    private boolean isInformed;
    private Date informedAt;

    public Subscriber(String endpointAddress, String method, String contentType) {
        this.endpointAddress = endpointAddress;
        this.method = method;
        this.contentType = contentType;
    }

    /**
     * Returns endpoint of the subscriber
     * @return
     */
    public String getEndpointAddress() {
        return endpointAddress;
    }

    /**
     * Sets endpoint of the subscriber
     * @param endpointAddress
     */
    public void setEndpointAddress(String endpointAddress) {
        this.endpointAddress = endpointAddress;
    }

    /**
     * Returns HTTP Method corresponding to endpoint address
     * @return
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets HTTP Method corresponding to endpoint address
     * @param method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Returns content type required by the endpoint
     * @return
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets content type required by the endpoint
     * @return
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns flag that denotes if subscriber is informed
     * @return
     */
    public boolean isInformed() {
        return isInformed;
    }

    /**
     * Sets flag that denotes if subscriber is informed
     * @param informed
     */
    public void setInformed(boolean informed) {
        isInformed = informed;
    }

    public Date getInformedAt() {
        return informedAt;
    }

    public void setInformedAt(Date informedAt) {
        this.informedAt = informedAt;
    }
}
