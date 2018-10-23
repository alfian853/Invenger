package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.Utils.MyUtils;
import com.bliblifuture.Invenger.annotation.imp.PasswordValdator;
import com.bliblifuture.Invenger.annotation.imp.PhoneValidator;
import com.bliblifuture.Invenger.model.Position;
import com.bliblifuture.Invenger.model.Role;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.repository.PositionRepository;
import com.bliblifuture.Invenger.repository.RoleRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import com.bliblifuture.Invenger.request.formRequest.UserCreateRequest;
import com.bliblifuture.Invenger.request.jsonRequest.ProfileRequest;
import com.bliblifuture.Invenger.response.jsonResponse.FormFieldResponse;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.Invenger.response.jsonResponse.UploadProfilePictResponse;
import com.bliblifuture.Invenger.response.jsonResponse.UserCreateResponse;
import com.bliblifuture.Invenger.response.viewDto.ProfileDTO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    MyUtils myUtils;

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public UserCreateResponse createUser(UserCreateRequest request){
        UserCreateResponse response = new UserCreateResponse();

        String imgName = UUID.randomUUID().toString().replace("-","")+
                "."+ FilenameUtils.getExtension(request.getProfile_photo().getOriginalFilename());

        Position newPosition = new Position();
        newPosition.setId(request.getPosition_id());

        Role newRole = new Role();
        newRole.setId(request.getRole_id());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(myUtils.getBcryptHash(request.getPassword()));
        newUser.setTelp(request.getTelp());
        newUser.setPictureName(imgName);
        newUser.setPosition(newPosition);
        newUser.setRole(newRole);

        if(fileStorageService.storeFile(
                request.getProfile_photo(),
                imgName,
                FileStorageService.PathCategory.PROFILE_PICT)
        ) {
            userRepository.save(newUser);
            response.setStatusToSuccess();
        }
        else{
            response.setStatusToFailed();
        }

        if(response.getStatus().equals("success")){
            response.setUser_id(newUser.getId());
        }

        return response;
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

    /*
    * load by username or email for login and auto login purpose
    * */
    @Override
    public User loadUserByUsername(String s) throws UsernameNotFoundException {
        User user =userRepository.findByUsername(s);
        if(user == null){
            return userRepository.findByEmail(s);
        }
        return user;
    }


    public ProfileDTO getProfile(){
        User user = this.getSessionUser();
        Position position = user.getPosition();
        try {
            position.getName();
//            myUtils.log("Sudah init");
        }
        catch (Exception e){
//            myUtils.log("Belum init");
            position = positionRepository.findUserPosition(user.getId());
            user.setPosition(position);
        }
        return ProfileDTO.builder()
                .name(user.getUsername())
                .email(user.getEmail())
                .position(user.getPosition().getName())
                .pictureName(user.getPictureName())
                .telp(user.getTelp())
                .build();
    }

    public Map<String,FormFieldResponse> editProfile(ProfileRequest request){
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
//
        while(!request.getOldPwd().equals("") ){

            String newPassword = request.getNewPwd1();
            formResponse = new FormFieldResponse();

            if(newPassword.equals(request.getOldPwd())){
                break;
            }

            if(request.getNewPwd1().equals("")){
                formResponse.setField_name("new-pwd1");
                formResponse.setStatusToFailed();
                formResponse.setMessage("password field can't be empty");
                formResponses.put("password",formResponse);
                break;
            }
            if(request.getNewPwd2().equals("")){
                formResponse.setField_name("new-pwd2");
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
                user = (User) this.getSessionUser();
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
                formResponse.setMessage("wrong password");
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
                System.out.println("delete fileee");
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
