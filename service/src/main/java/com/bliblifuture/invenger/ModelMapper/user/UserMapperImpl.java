package com.bliblifuture.invenger.ModelMapper.user;

import com.bliblifuture.invenger.entity.user.Position;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.response.jsonResponse.UserDataTableResponse;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchItem;
import com.bliblifuture.invenger.response.viewDto.PositionDTO;
import com.bliblifuture.invenger.response.viewDto.UserDTO;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .position(user.getPosition().getName())
                .positionId(user.getPosition().getId())
                .positionLevel(user.getPosition().getLevel())
                .pictureName(user.getPictureName())
                .superior((user.getSuperior() != null)?user.getSuperior().getFullName():"")
                .superiorId((user.getSuperior() != null)?user.getSuperior().getId():null)
                .telp(user.getTelp())
                .build();
    }

    @Override
    public List<UserDTO> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).collect(Collectors.toList());
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

    @Override
    public List<UserDataTableResponse> toDataTablesDtoList(List<User> users) {

        List<UserDataTableResponse> responses = new LinkedList<>();

        for(User user : users){
            responses.add(UserDataTableResponse.builder()
                    .id(user.getId())
                    .rowId("row_"+user.getId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .position(user.getPosition().getName())
                    .build()
            );
        }

        return responses;
    }

    @Override
    public Path getPathFrom(Root root, String field) {
        switch (field){
            case "position":
                return root.get("position").get("name");
        }
        return root.get(field);
    }
}
