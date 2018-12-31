package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.Utils.MyUtils;
import com.bliblifuture.invenger.annotation.imp.PhoneValidator;
import com.bliblifuture.invenger.entity.user.Position;
import com.bliblifuture.invenger.entity.user.RoleType;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.repository.PositionRepository;
import com.bliblifuture.invenger.repository.UserRepository;
import com.bliblifuture.invenger.request.jsonRequest.EditProfileRequest;
import com.bliblifuture.invenger.response.jsonResponse.FormFieldResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.jsonResponse.UploadProfilePictResponse;
import com.bliblifuture.invenger.response.viewDto.ProfileDTO;
import com.bliblifuture.invenger.service.impl.AccountServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @Spy
    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private FileStorageService storageService;

    @Mock
    private MyUtils myUtils;
    private static Position POSITION = Position.builder()
            .id(1)
            .level(2)
            .name("admin")
            .build();

    private static Integer ID = 14;
    private static String FULL_NAME = "full dummy";
    private static String USERNAME = "dummy";
    private static String EMAIL = "dummy@dumail.com";
    private static String PASSWORD = new BCryptPasswordEncoder().encode("password");
    private static String TELP = "12345678910";

    private static User USER = User.builder()
            .id(ID)
            .email(EMAIL)
            .fullName(FULL_NAME)
            .username(USERNAME)
            .password(PASSWORD)
            .telp(TELP)
            .position(POSITION)
            .build();

    @Before
    public void init(){
        when(myUtils.getRandomFileName(any(MultipartFile.class))).thenReturn("abcdefg");
    }


      ////////////////////////////////////////////
     //public User loadUserByUsername(String s)//
    ////////////////////////////////////////////

    @Test
    public void loadUserByUsername_usernameFound() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(USER);

        User user = accountService.loadUserByUsername(USERNAME);

        verify(userRepository, never()).findByEmail(USERNAME);
        Assert.assertEquals(user, USER);
    }

    @Test
    public void loadUserByUsername_usernameNotFoundButEmailFound() {
        when(userRepository.findByUsername(EMAIL)).thenReturn(null);
        when(userRepository.findByEmail(EMAIL)).thenReturn(USER);

        User user = accountService.loadUserByUsername(EMAIL);

        verify(userRepository, times(1)).findByEmail(EMAIL);
        Assert.assertEquals(user, USER);
    }

    @Test
    public void loadUserByUsername_usernameAndEmailNotFound(){

        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
        when(userRepository.findByUsername(EMAIL)).thenReturn(null);

        when(userRepository.findByEmail(USERNAME)).thenReturn(null);
        when(userRepository.findByEmail(EMAIL)).thenReturn(null);

        User user = accountService.loadUserByUsername(USERNAME);

        verify(userRepository, times(1)).findByEmail(USERNAME);
        Assert.assertNull(user);

        user = accountService.loadUserByUsername(EMAIL);

        verify(userRepository,times(1)).findByEmail(EMAIL);
        Assert.assertNull(user);
    }


      /////////////////////////////////
     // public User getSessionUser()//
    /////////////////////////////////

    @Test
    public void getThisUser_getAuthNotNull(){

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(USER);

        Object user = accountService.getSessionUser();

        Assert.assertThat(user,instanceOf(User.class));
    }

    @Test
    public void getThisUser_getAuthNotInstanceOfUser(){

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(USERNAME);

        Object user = accountService.getSessionUser();

        Assert.assertNotEquals(instanceOf(User.class),user);
    }

    @Test
    public void getThisUser_getAuthIsNull(){

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(null);

        User user = accountService.getSessionUser();

        Assert.assertNull(user);
    }




      //////////////////////////////////////////////////////////////////////////
     //public UploadProfilePictResponse changeProfilePict(MultipartFile file)//
    //////////////////////////////////////////////////////////////////////////

    private MultipartFile mock_getMultipart(){
        return new MockMultipartFile
                ("data", "filename.png",
                        "image/png", "".getBytes()
                );
    }

    @Test
    public void changeProfilePict_fileIsNull(){
        UploadProfilePictResponse response = accountService.changeProfilePict(null);
        RequestResponse test = new RequestResponse();
        test.setStatusToSuccess();
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getMessage(), test.getMessage());
        Assert.assertNull(response.getNew_pict_src());

    }

    @Test
    public void changeProfilePict_storeFileFailed(){

        when(storageService.storeFile(any(),anyString(), any()))
                .thenReturn(false);

        UploadProfilePictResponse response = accountService.changeProfilePict(mock_getMultipart());
        RequestResponse test = new RequestResponse();
        test.setStatusToFailed();
        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals(response.getMessage(), test.getMessage());
        Assert.assertNull(response.getNew_pict_src());

    }

    @Test
    public void changeProfilePict_storeFileSuccess_defaultOldPic(){
        User user = User.builder().build();
        user.setPictureName("default-pict.png");
        when(storageService.storeFile(any(),anyString(), any()))
                .thenReturn(true);

        doReturn(user).when(accountService).getSessionUser();

        UploadProfilePictResponse response = accountService.changeProfilePict(mock_getMultipart());
        RequestResponse test = new RequestResponse();
        test.setStatusToSuccess();
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getMessage(), test.getMessage());
    }

    @Test
    public void changeProfilePict_storeFileSuccess_replaceSuccess(){
        User user = User.builder().build();
        user.setPictureName("filename.png");
        when(storageService.storeFile(any(),anyString(), any()))
                .thenReturn(true);

        doReturn(user).when(accountService).getSessionUser();

        when(storageService.deleteFile(user.getPictureName(), FileStorageService.PathCategory.PROFILE_PICT))
                .thenReturn(true);

        UploadProfilePictResponse response = accountService.changeProfilePict(mock_getMultipart());
        RequestResponse test = new RequestResponse();
        test.setStatusToSuccess();
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getMessage(), test.getMessage());
    }


    @Test
    public void changeProfilePict_storeFileSuccess_replaceFailed(){
        User user = User.builder().build();
        user.setPictureName("filename.png");
        when(storageService.storeFile(any(),anyString(), any()))
                .thenReturn(true);

        doReturn(user).when(accountService).getSessionUser();

        when(storageService.deleteFile(user.getPictureName(), FileStorageService.PathCategory.PROFILE_PICT))
                .thenReturn(false);

        UploadProfilePictResponse response = accountService.changeProfilePict(mock_getMultipart());
        RequestResponse test = new RequestResponse();
        test.setStatusToFailed();
        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals(response.getMessage(), test.getMessage());
    }



      //////////////////////////////////
     //public ProfileDTO getProfile()//
    //////////////////////////////////

    @Test
    public void getProfile_test() {

        doReturn(USER).when(accountService).getSessionUser();

        ProfileDTO profileDTO = ProfileDTO.builder()
                .id(USER.getId())
                .name(USER.getFullName())
                .username(USER.getUsername())
                .email(USER.getEmail())
                .position(USER.getPosition().getName())
                .pictureName(USER.getPictureName())
                .telp(USER.getTelp())
                .build();

        Assert.assertEquals(profileDTO, accountService.getProfile());

    }



      ///////////////////////////////////////
     //public boolean currentUserIsAdmin()//
    ///////////////////////////////////////

    @Test
    public void currentUserIsAdmin_test() {
        USER.setRole(RoleType.ROLE_ADMIN.name());

        when(accountService.getSessionUser()).thenReturn(USER);

        Assert.assertTrue(accountService.currentUserIsAdmin());
    }


      ///////////////////////////////////////////
     //editProfile(EditProfileRequest request)//
    ///////////////////////////////////////////

    private EditProfileRequest mock_editProfileRequest(String telp, String pwd, String nPwd1, String nPwd2){
        EditProfileRequest request = new EditProfileRequest();
        request.setNewTelp(telp);
        request.setOldPwd(pwd);
        request.setNewPwd1(nPwd1);
        request.setNewPwd2(nPwd2);
        return request;
    }

    @Test
    public void editProfile_changePhone_inValid(){

        EditProfileRequest request = this.mock_editProfileRequest(
                "+628-199141-1822", "","",""
        );

        User user = User.builder().telp("081991411822").build();
        when(accountService.getSessionUser()).thenReturn(user);

        Map<String, FormFieldResponse> responseMap =
                accountService.editProfile(request);

        FormFieldResponse formFieldResponse = new FormFieldResponse("new-telp");
        formFieldResponse.setStatusToFailed();
        formFieldResponse.setMessage(PhoneValidator.getErrorMessage());
        Assert.assertEquals(responseMap.get("new-telp"),formFieldResponse);
    }

    @Test
    public void editProfile_changePhone_valid(){

        EditProfileRequest request = this.mock_editProfileRequest(
                "+6281991411822", "","",""
        );

        User user = User.builder().telp("081991411822").build();
        doReturn(user).when(accountService).getSessionUser();

        Map<String, FormFieldResponse> responseMap = accountService.editProfile(request);

        FormFieldResponse formFieldResponse = new FormFieldResponse("new-telp");
        formFieldResponse.setStatusToSuccess();
        formFieldResponse.setMessage("Phone number updated successfuly");
        Assert.assertEquals(responseMap.get("new-telp"),formFieldResponse);

    }

    @Test
    public void editProfile_editPassword_sameAsOld(){
        EditProfileRequest request = this.mock_editProfileRequest(
                "", "oldpwd","oldpwd","oldpwd"
        );

        Map<String, FormFieldResponse> responseMap = accountService.editProfile(request);

        Assert.assertEquals(0,responseMap.size());

        verify(myUtils,times(0)).matches(anyString(),anyString());

    }

    @Test
    public void editProfile_editPassword_PWdConfirmationEmpty(){
        EditProfileRequest request = this.mock_editProfileRequest(
                "", "oldpwd","",""
        );

        Map<String, FormFieldResponse> responseMap = accountService.editProfile(request);
        System.out.println(responseMap);
        Assert.assertFalse(responseMap.get("password").getSuccess());
        Assert.assertEquals(responseMap.get("password").getMessage(),"password field can't be empty");
        verify(myUtils,times(0)).matches(anyString(),anyString());
    }

    @Test
    public void editProfile_editPassword_PWdConfirmationNotMatch(){
        EditProfileRequest request = this.mock_editProfileRequest(
                "", "oldpwd","passwordbaru","passwordbaru123"
        );

        Map<String, FormFieldResponse> responseMap = accountService.editProfile(request);

        Assert.assertFalse(responseMap.get("password").getSuccess());
        Assert.assertEquals(responseMap.get("password").getMessage(),"new password doesn't match");
        Assert.assertEquals(responseMap.get("password").getField_name(),"new-pwd2");
        verify(myUtils,times(0)).matches(anyString(),anyString());
    }

    @Test
    public void editProfile_weakPassword(){
        EditProfileRequest request = this.mock_editProfileRequest(
                "", "oldpwd","passwordbaru","passwordbaru"
        );

        Map<String, FormFieldResponse> responseMap = accountService.editProfile(request);

        Assert.assertFalse(responseMap.get("password").getSuccess());
        Assert.assertEquals(responseMap.get("password").getMessage(),"Weak password");
        Assert.assertEquals(responseMap.get("password").getField_name(),"new-pwd1");
        verify(myUtils,times(0)).matches(anyString(),anyString());

    }

    @Test
    public void editProfile_wrongPassword(){
        EditProfileRequest request = this.mock_editProfileRequest(
                "", "oldpwd","passwordBaru123","passwordBaru123"
        );

        User user = User.builder().telp("081991411822").password("bcrypted-pwd").build();
        doReturn(user).when(accountService).getSessionUser();

        when(myUtils.matches(anyString(),anyString())).thenReturn(false);

        Map<String, FormFieldResponse> responseMap = accountService.editProfile(request);
        Assert.assertFalse(responseMap.get("password").getSuccess());
        Assert.assertEquals(responseMap.get("password").getMessage(),"Wrong password");
        Assert.assertEquals(responseMap.get("password").getField_name(),"old-pwd");
        verify(myUtils,times(1)).matches(anyString(),anyString());
    }

    @Test
    public void editProfile_success(){
        EditProfileRequest request = this.mock_editProfileRequest(
                "+6281991411822", "oldpwd","passwordBaru123","passwordBaru123"
        );
        User user = User.builder().telp("081991411822").password("bcrypted-pwd").build();
        doReturn(user).when(accountService).getSessionUser();

        when(myUtils.matches(anyString(),anyString())).thenReturn(true);

        Map<String, FormFieldResponse> responseMap = accountService.editProfile(request);
        Assert.assertTrue(responseMap.get("password").getSuccess());
        Assert.assertTrue(responseMap.get("new-telp").getSuccess());
        verify(myUtils,times(1)).matches(anyString(),anyString());
    }





}
