package com.bliblifuture.Invenger.request.formRequest;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserEditRequest {

    Integer id;

    String username;

    String email;

    String telp;

    String password;

    MultipartFile pict;

    Integer position_id;

    Integer superior_id;

    Integer role_id;

}