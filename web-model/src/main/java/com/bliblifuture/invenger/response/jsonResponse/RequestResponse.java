package com.bliblifuture.invenger.response.jsonResponse;

import lombok.Data;
@Data
public class RequestResponse {
    Boolean success = false;
    String message="";

    public void setStatusToFailed(){
        success = false;
    }
    public void setStatusToSuccess(){
        success = true;
    }
    public boolean isSuccess(){
        return this.success;
    }

}
