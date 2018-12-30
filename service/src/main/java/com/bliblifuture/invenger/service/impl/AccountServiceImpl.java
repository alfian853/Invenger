package com.bliblifuture.invenger.service.impl;

import com.bliblifuture.invenger.Utils.MyUtils;
import com.bliblifuture.invenger.annotation.imp.PasswordValdator;
import com.bliblifuture.invenger.annotation.imp.PhoneValidator;
import com.bliblifuture.invenger.entity.user.Position;
import com.bliblifuture.invenger.entity.user.RoleType;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.repository.PositionRepository;
import com.bliblifuture.invenger.repository.UserRepository;
import com.bliblifuture.invenger.request.jsonRequest.EditProfileRequest;
import com.bliblifuture.invenger.response.jsonResponse.FormFieldResponse;
import com.bliblifuture.invenger.response.jsonResponse.UploadProfilePictResponse;
import com.bliblifuture.invenger.response.viewDto.ProfileDTO;
import com.bliblifuture.invenger.service.AccountService;
import com.bliblifuture.invenger.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    MyUtils myUtils;
    /*
     * load by username or email for login and auto login purpose
     * */
    @Override
    public User loadUserByUsername(String s) {
        User user =userRepository.findByUsername(s);
        if(user == null){
            return userRepository.findByEmail(s);
        }
        return user;
    }

    public User getSessionUser(){
        if(SecurityContextHolder.getContext().getAuthentication() == null){
            return null;
        }
        else if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User){
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }

    public boolean currentUserIsAdmin(){
        return RoleType.isEqual(RoleType.ROLE_ADMIN, this.getSessionUser().getRole());
    }

    public ProfileDTO getProfile(){
        User user = this.getSessionUser();
        Position position = user.getPosition();
        try {
            position.getName();
        }
        catch (Exception e){
            position = positionRepository.findUserPosition(user.getId());
            user.setPosition(position);
        }
        return ProfileDTO.builder()
                .id(user.getId())
                .name(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .position(user.getPosition().getName())
                .pictureName(user.getPictureName())
                .telp(user.getTelp())
                .build();
    }

    public Map<String,FormFieldResponse> editProfile(EditProfileRequest request) {
        User user = null;
        Map<String,FormFieldResponse> formResponses = new HashMap<>();
        FormFieldResponse formResponse = null;

        if(!request.getNewTelp().equals("") ){

            formResponse = new FormFieldResponse("new-telp");

            if(PhoneValidator.isValid(request.getNewTelp())){
                user = this.getSessionUser();
                if(!request.getNewTelp().equals(user.getTelp())){
                    user.setTelp(request.getNewTelp());
                    userRepository.save(user);
                    formResponse.setStatusToSuccess();
                    formResponse.setMessage("Phone number updated successfuly");
                }
            }
            else{
                formResponse.setStatusToFailed();
                formResponse.setMessage(PhoneValidator.getErrorMessage());
            }
            formResponses.put("new-telp",formResponse);
        }

        while(!request.getOldPwd().equals("") ){

            String newPassword = request.getNewPwd1();
            formResponse = new FormFieldResponse();

            if(newPassword.equals(request.getOldPwd())){
                break;
            }

            if(request.getNewPwd1().equals("") || request.getNewPwd2().equals("")){
                formResponse.setField_name((request.getNewPwd1().equals("")?"new-pwd1":"new-pwd2"));
                formResponse.setStatusToFailed();
                formResponse.setMessage("password field can't be empty");
                formResponses.put("password",formResponse);
                break;
            }

            if(!newPassword.equals(request.getNewPwd2()) ) {
                formResponse.setField_name("new-pwd2");
                formResponse.setStatusToFailed();
                formResponse.setMessage("new password doesn't match");
                formResponses.put("password",formResponse);
                break;
            }

            if(!PasswordValdator.isValid(newPassword)){
                formResponse.setField_name("new-pwd1");
                formResponse.setStatusToFailed();
                formResponse.setMessage("Weak password");
                formResponses.put("password",formResponse);
                break;
            }

            if(user == null){
                user = this.getSessionUser();
            }

            if(myUtils.matches(request.getOldPwd(),user.getPassword() )){
                user.setPassword(myUtils.getBcryptHash(newPassword));
                userRepository.save(user);
                formResponse.setField_name("old-pwd");
                formResponse.setStatusToSuccess();
                formResponse.setMessage("Change password success");

            }
            else{
                formResponse.setField_name("old-pwd");
                formResponse.setStatusToFailed();
                formResponse.setMessage("Wrong password");
            }

            formResponses.put("password",formResponse);

            break;
        }

        return formResponses;
    }

    public UploadProfilePictResponse changeProfilePict(MultipartFile file){
        UploadProfilePictResponse response = new UploadProfilePictResponse();
        if(file == null){
            response.setStatusToSuccess();
            return response;
        }
        String fileName = myUtils.getRandomFileName(file);

        if(fileStorageService.storeFile(file,fileName, FileStorageService.PathCategory.PROFILE_PICT) ){
            User user = this.getSessionUser();
            String oldFileName = user.getPictureName();

            boolean oldFileDeleted = false;
            if(oldFileName.equals("default-pict.png")){
                oldFileDeleted = true;
            }
            else{
                oldFileDeleted = fileStorageService.deleteFile(oldFileName,FileStorageService.PathCategory.PROFILE_PICT);
            }

            if(oldFileDeleted){
                user.setPictureName(fileName);
                userRepository.save(user);
                response.setNew_pict_src("/profile/pict/"+fileName);
                response.setStatusToSuccess();

            }
            else {
                fileStorageService.deleteFile(fileName, FileStorageService.PathCategory.PROFILE_PICT);
                response.setStatusToFailed();
            }

        }
        else{
            response.setStatusToFailed();
        }
        return response;
    }




}
