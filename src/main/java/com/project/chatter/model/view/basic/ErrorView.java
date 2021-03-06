package com.project.chatter.model.view.basic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorView extends ResponseView {

    private final String error;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("info")
    private Object additionalInfo;

    public ErrorView(int status, String error, String message, String path, Object additionalInfo) {
        super(status, message, path);
        this.error = error;
        this.additionalInfo = additionalInfo;
    }

    public ErrorView(int status, String error, String message, String path) {
        super(status, message, path);
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public Object getAdditionalInfo() {
        return additionalInfo;
    }
}
