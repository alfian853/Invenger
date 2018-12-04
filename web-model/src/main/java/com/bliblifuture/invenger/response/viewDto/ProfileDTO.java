package com.bliblifuture.invenger.response.viewDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDTO {
    Integer id;
    String name;
    String username;
    String position;
    String email;
    String telp;
    String pictureName;
}
