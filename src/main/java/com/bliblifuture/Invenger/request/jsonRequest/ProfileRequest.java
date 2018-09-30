package com.bliblifuture.Invenger.request.jsonRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProfileRequest {

    @JsonProperty("new-telp")
    String newTelp;

    @JsonProperty("old-pwd")
    String oldPwd;

    @JsonProperty("new-pwd1")
    String newPwd1;

    @JsonProperty("new-pwd2")
    String newPwd2;
}
