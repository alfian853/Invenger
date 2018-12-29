package com.bliblifuture.invenger.ModelMapper.user;

import com.bliblifuture.invenger.ModelMapper.CriteriaPathMapper;
import com.bliblifuture.invenger.ModelMapper.DataTableMapper;
import com.bliblifuture.invenger.ModelMapper.ModelMapper;
import com.bliblifuture.invenger.ModelMapper.SearchResultMapper;
import com.bliblifuture.invenger.entity.user.Position;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.response.jsonResponse.UserDataTableResponse;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchItem;
import com.bliblifuture.invenger.response.viewDto.PositionDTO;
import com.bliblifuture.invenger.response.viewDto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends
        ModelMapper<UserDTO,User>,
        DataTableMapper<UserDataTableResponse,User>,
        SearchResultMapper<User> {

    PositionDTO toPositionDto(Position position);
    List<PositionDTO> toPositionDtoList(List<Position> positions);
}
