package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.request.jsonRequest.EditProfileRequest;
import com.bliblifuture.invenger.response.jsonResponse.FormFieldResponse;
import com.bliblifuture.invenger.response.jsonResponse.UploadProfilePictResponse;
import com.bliblifuture.invenger.response.viewDto.ProfileDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface AccountService extends UserDetailsService {
    @Override
    User loadUserByUsername(String s);

    User getSessionUser();
    boolean currentUserIsAdmin();
    ProfileDTO getProfile();
    Map<String,FormFieldResponse> editProfile(EditProfileRequest request);
    UploadProfilePictResponse changeProfilePict(MultipartFile file);


}
