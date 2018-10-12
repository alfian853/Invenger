package com.bliblifuture.Invenger.request.formRequest;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Data
public class UserCreateRequest {

    @NotEmpty
    String username;

    String email;

    String telp;

    String password;

    MultipartFile profile_photo;

    Integer position_id;

    Integer superior_id;

    Integer role_id;

}

