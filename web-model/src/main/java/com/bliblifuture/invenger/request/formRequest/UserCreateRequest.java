package com.bliblifuture.invenger.request.formRequest;

import com.bliblifuture.invenger.annotation.PhoneConstraint;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserCreateRequest {

    @NotEmpty
    String fullName;

    @NotEmpty
    String username;

    @Email
    String email;

    @PhoneConstraint
    String telp;

    @NotEmpty
    String password;

    @NotNull
    MultipartFile profile_photo;

    @NotNull
    Integer position_id;

    @NotNull
    Integer superior_id;

}

