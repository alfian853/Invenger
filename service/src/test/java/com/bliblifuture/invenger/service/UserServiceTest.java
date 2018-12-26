package com.bliblifuture.invenger;


import com.bliblifuture.invenger.Utils.MyUtils;
import com.bliblifuture.invenger.entity.user.Position;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.repository.PositionRepository;
import com.bliblifuture.invenger.repository.UserRepository;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.jsonResponse.UploadProfilePictResponse;
import com.bliblifuture.invenger.service.FileStorageService;
import com.bliblifuture.invenger.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.mockito.Mockito.*;


public class UserServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Spy
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private FileStorageService storageService;

    @Mock
    private MyUtils myUtils;

    private static String USERNAME = "dummy";
    private static String EMAIL = "dummy@dumail.com";
    private static String PASSWORD = new BCryptPasswordEncoder().encode("password");
    private static String TELP = "12345678910";

    private static User USER = User.builder()
            .email(EMAIL)
            .username(USERNAME)
            .password(PASSWORD)
            .telp(TELP)
            .build();

    private static Position POSITION = Position.builder()
            .name("admin")
            .build();

    @Before
    public void init(){
        when(myUtils.getRandomFileName(any())).thenReturn("abcdefg");
    }

    @Test
    public void loadUserByUsername_usernameFound() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(USER);

        User user = userService.loadUserByUsername(USERNAME);

        verify(userRepository, never()).findByEmail(USERNAME);
        Assert.assertEquals(user, USER);
    }

    @Test
    public void loadUserByUsername_usernameNotFoundButEmailFound() {
        when(userRepository.findByUsername(EMAIL)).thenReturn(null);
        when(userRepository.findByEmail(EMAIL)).thenReturn(USER);

        User user = userService.loadUserByUsername(EMAIL);

        verify(userRepository, times(1)).findByEmail(EMAIL);
        Assert.assertEquals(user, USER);
    }

    @Test
    public void loadUserByUsername_usernameAndEmailNotFound(){

        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
        when(userRepository.findByUsername(EMAIL)).thenReturn(null);

        when(userRepository.findByEmail(USERNAME)).thenReturn(null);
        when(userRepository.findByEmail(EMAIL)).thenReturn(null);

        User user = userService.loadUserByUsername(USERNAME);

        verify(userRepository, times(1)).findByEmail(USERNAME);
        Assert.assertNull(user);

        user = userService.loadUserByUsername(EMAIL);

        verify(userRepository,times(1)).findByEmail(EMAIL);
        Assert.assertNull(user);
    }

    @Test
    public void getThisUser_getAuthNotNull(){

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(USER);

        Object user = userService.getSessionUser();

        Assert.assertThat(user,instanceOf(User.class));
    }

    @Test
    public void getThisUser_getAuthIsNull(){

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(null);

        User user = userService.getSessionUser();

        Assert.assertNull(user);
    }

    private MultipartFile mock_getMultipart(){
        return new MockMultipartFile
                ("data", "filename.png",
                        "image/png", "".getBytes()
                );
    }

    @Test
    public void changeProfilePict_storeFileFailed(){

        when(storageService.storeFile(any(),anyString(), any()))
                .thenReturn(false);

        UploadProfilePictResponse response = userService.changeProfilePict(mock_getMultipart());
        RequestResponse test = new RequestResponse();
        test.setStatusToFailed();
        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals(response.getMessage(), test.getMessage());
        Assert.assertNull(response.getNew_pict_src());

    }


    @Test
    public void getProfile_storeFileSuccess_replaceSuccess(){
        User user = User.builder().build();
        user.setPictureName("filename.png");
        when(storageService.storeFile(any(),anyString(), any()))
                .thenReturn(true);

        doReturn(user).when(userService).getSessionUser();

        when(storageService.deleteFile(user.getPictureName(), FileStorageService.PathCategory.PROFILE_PICT))
                .thenReturn(true);

        UploadProfilePictResponse response = userService.changeProfilePict(mock_getMultipart());
        RequestResponse test = new RequestResponse();
        test.setStatusToSuccess();
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getMessage(), test.getMessage());
    }


    @Test
    public void getProfile_storeFileSuccess_replaceFailed(){
        User user = User.builder().build();
        user.setPictureName("filename.png");
        when(storageService.storeFile(any(),anyString(), any()))
                .thenReturn(true);

        doReturn(user).when(userService).getSessionUser();

        when(storageService.deleteFile(user.getPictureName(), FileStorageService.PathCategory.PROFILE_PICT))
                .thenReturn(false);

        UploadProfilePictResponse response = userService.changeProfilePict(mock_getMultipart());
        RequestResponse test = new RequestResponse();
        test.setStatusToFailed();
        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals(response.getMessage(), test.getMessage());
    }



}