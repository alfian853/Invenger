package com.bliblifuture.Invenger.response.viewDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDTO {
    String name;
    String position;
    String email;
    String telp;
    String pictureName;
}
