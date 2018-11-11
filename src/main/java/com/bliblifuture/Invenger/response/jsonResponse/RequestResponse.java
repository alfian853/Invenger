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
    public boolean isSuccess(){
        return status.equals("success");
    }

    public boolean isFailed(){
        return status.equals("failed");
    }

}
