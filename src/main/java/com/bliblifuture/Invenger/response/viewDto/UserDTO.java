package com.bliblifuture.Invenger.response.viewDto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDTO {

    private String fullName;

    private Integer id;

    private String username;

    private String email;

    private String telp;

    private String pictureName;

    private String position;

    private String superior;

}
