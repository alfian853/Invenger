package com.bliblifuture.invenger.ModelMapper.user;

import com.bliblifuture.invenger.ModelMapper.CriteriaPathMapper;
import com.bliblifuture.invenger.model.user.Position;
import com.bliblifuture.invenger.model.user.User;
import com.bliblifuture.invenger.response.jsonResponse.UserDataTableResponse;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchItem;
import com.bliblifuture.invenger.response.viewDto.PositionDTO;
import com.bliblifuture.invenger.response.viewDto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends CriteriaPathMapper {
    UserDTO toUserDto(User user);

    List<UserDTO> toUserDtoList(List<User> users);

    List<SearchItem> toSearchResultList(List<User> users);

    PositionDTO toPositionDto(Position position);

    List<PositionDTO> toPositionDtoList(List<Position> positions);

    List<UserDataTableResponse> toUserDatatables(List<User> users);


}
