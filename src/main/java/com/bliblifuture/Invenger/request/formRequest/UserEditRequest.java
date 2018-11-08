package com.bliblifuture.Invenger.request.formRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserEditRequest {

    Integer id;

    String username;

    @JsonProperty("full_name")
    String fullName;

    String email;

    String telp;

    String password;

    MultipartFile pict;

    Integer position_id;

    Integer superior_id;

    Integer role_id;

}