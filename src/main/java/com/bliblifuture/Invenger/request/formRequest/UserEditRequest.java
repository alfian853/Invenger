package com.bliblifuture.Invenger.request.formRequest;

import lombok.Data;

@Data
public class UserEditRequest {

    Integer id;

    String password;

    Integer position_id;

    Integer superior_id;

}