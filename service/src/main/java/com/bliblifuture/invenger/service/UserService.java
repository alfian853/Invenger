package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.request.formRequest.UserCreateRequest;
import com.bliblifuture.invenger.request.formRequest.UserEditRequest;
import com.bliblifuture.invenger.request.jsonRequest.EditProfileRequest;
import com.bliblifuture.invenger.request.jsonRequest.UserSearchRequest;
import com.bliblifuture.invenger.response.jsonResponse.*;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.response.viewDto.PositionDTO;
import com.bliblifuture.invenger.response.viewDto.ProfileDTO;
import com.bliblifuture.invenger.response.viewDto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService extends
        DatatablesService <DataTablesResult<UserDataTableResponse> >,
        SearchService<SearchResponse, UserSearchRequest> {

    List<UserDTO> getAll();
    UserDTO getById(Integer id);
    UserCreateResponse createUser(UserCreateRequest request);
    RequestResponse updateUser(UserEditRequest request);
    RequestResponse deleteUser(Integer id);
    List<PositionDTO> getAllPosition();
    PositionCreateResponse createPosition(PositionDTO newPosition);
    RequestResponse editPosition(PositionDTO editedPosition);
    RequestResponse deletePosition(Integer id);
}
