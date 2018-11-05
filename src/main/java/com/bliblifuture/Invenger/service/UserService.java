package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.Utils.MyUtils;
import com.bliblifuture.Invenger.annotation.imp.PasswordValdator;
import com.bliblifuture.Invenger.annotation.imp.PhoneValidator;
import com.bliblifuture.Invenger.model.Position;
import com.bliblifuture.Invenger.model.user.User;
import com.bliblifuture.Invenger.model.user.RoleType;
import com.bliblifuture.Invenger.repository.PositionRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import com.bliblifuture.Invenger.request.formRequest.UserCreateRequest;
import com.bliblifuture.Invenger.request.formRequest.UserEditRequest;
import com.bliblifuture.Invenger.request.jsonRequest.ProfileRequest;
import com.bliblifuture.Invenger.response.jsonResponse.FormFieldResponse;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.Invenger.response.jsonResponse.UploadProfilePictResponse;
import com.bliblifuture.Invenger.response.jsonResponse.UserCreateResponse;
import com.bliblifuture.Invenger.response.viewDto.ProfileDTO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    FileStorageService fileStorageService;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    MyUtils myUtils;

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public User getById(Integer id){
        return userRepository.findUserById(id);
    }

    public UserCreateResponse createUser(UserCreateRequest request){
        UserCreateResponse response = new UserCreateResponse();

        String imgName = UUID.randomUUID().toString().replace("-","")+
                "."+ FilenameUtils.getExtension(request.getProfile_photo().getOriginalFilename());

        Position newPosition = new Position();
        newPosition.setId(request.getPosition_id());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(myUtils.getBcryptHash(request.getPassword()));
        newUser.setTelp(request.getTelp());
        newUser.setPictureName(imgName);
        newUser.setPosition(newPosition);
        newUser.setRole(RoleType.ROLE_ADMIN.toString());

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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse updateUser(UserEditRequest request){

        User user = userRepository.getOne(request.getId());
        System.out.println(request);
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        if(request.getUsername() != null){
            user.setUsername(request.getUsername());
        }
        if(request.getEmail() != null){
            user.setEmail(request.getEmail());
        }
        if(request.getPassword() != null){
            user.setPassword(myUtils.getBcryptHash(request.getPassword()));
        }
        if(request.getTelp() != null){
            user.setTelp(request.getTelp());
        }
        if(request.getPosition_id() != null){
            Position position = positionRepository.getOne(request.getPosition_id());
            user.setPosition(position);
        }
        if(request.getRole_id() != null){
//            Role role = roleRepository.getOne(request.getRole_id());
//            user.setRole(role);
        }
        if(request.getPict() != null){

            String newFileName = myUtils.getRandomFileName(request.getPict());

            if(fileStorageService.storeFile(request.getPict(),newFileName,
                    FileStorageService.PathCategory.PROFILE_PICT) ){

                String oldFileName = user.getPictureName();
                fileStorageService.deleteFile(oldFileName, FileStorageService.PathCategory.PROFILE_PICT);
                user.setPictureName(newFileName);
            }
            else {
                response.setStatusToFailed();
                response.setMessage("Internal server error");
            }

        }

        userRepository.save(user);
        return response;
    }

    public RequestResponse deleteUser(Integer id){
        RequestResponse response = new RequestResponse();
        userRepository.deleteById(id);
        response.setStatusToSuccess();
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

    public boolean currentUserIsAdmin(){
        boolean isAdmin = RoleType.isEqual(RoleType.ROLE_ADMIN, this.getSessionUser().getRole());
        return isAdmin;
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
