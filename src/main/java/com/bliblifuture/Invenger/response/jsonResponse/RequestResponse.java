package com.bliblifuture.Invenger.response.jsonResponse;

import lombok.Data;
@Data
public class RequestResponse {
    String status="";
    String message="";

    public void setStatusToFailed(){
        status = "failed";
    }
    public void setStatusToSuccess(){
        status = "success";
    }
}
