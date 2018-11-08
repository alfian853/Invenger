package com.bliblifuture.Invenger.ModelMapper.user;

import com.bliblifuture.Invenger.model.user.User;
import com.bliblifuture.Invenger.response.viewDto.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapperImpl implements UserMapper {
    @Override
    public UserDTO toUserDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .position(user.getPosition().getName())
                .pictureName(user.getPictureName())
                .superior((user.getSuperior() != null)?user.getSuperior().getFullName():"")
                .telp(user.getTelp())
                .build();
    }

    @Override
    public List<UserDTO> toUserDtoList(List<User> users) {
        return users.stream().map(this::toUserDto).collect(Collectors.toList());
    }
}
