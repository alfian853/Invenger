package com.bliblifuture.Invenger.ModelMapper.user;

import com.bliblifuture.Invenger.model.user.Position;
import com.bliblifuture.Invenger.model.user.User;
import com.bliblifuture.Invenger.response.jsonResponse.search_response.SearchItem;
import com.bliblifuture.Invenger.response.viewDto.PositionDTO;
import com.bliblifuture.Invenger.response.viewDto.UserDTO;

import java.util.LinkedList;
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

    @Override
    public List<SearchItem> toSearchResultList(List<User> users) {
        List<SearchItem> responses = new LinkedList<>();
        for(User user : users){
            responses.add(SearchItem.builder()
                    .id(user.getId())
                    .text(user.getUsername()+" ("+user.getFullName()+")")
                    .build());
        }
        return responses;
    }

    @Override
    public PositionDTO toPositionDto(Position position) {
        return PositionDTO.builder()
                .id(position.getId())
                .name(position.getName())
                .level(position.getLevel())
                .build();
    }

    @Override
    public List<PositionDTO> toPositionDtoList(List<Position> positions) {
        return positions.stream().map(this::toPositionDto).collect(Collectors.toList());
    }
}
