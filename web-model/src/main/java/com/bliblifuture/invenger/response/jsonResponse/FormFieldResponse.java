package com.bliblifuture.invenger.response.jsonResponse;

public class FormFieldResponse extends RequestResponse {
    String field_name;

    public FormFieldResponse(String field_name) {
        this.field_name = field_name;
    }

    public FormFieldResponse(){

    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }
}
