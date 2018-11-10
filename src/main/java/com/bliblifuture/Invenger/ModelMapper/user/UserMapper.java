package com.bliblifuture.Invenger.ModelMapper.user;

import com.bliblifuture.Invenger.model.user.User;
import com.bliblifuture.Invenger.response.viewDto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDTO toUserDto(User user);
    List<UserDTO> toUserDtoList(List<User> users);
}
