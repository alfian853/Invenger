package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.response.jsonResponse.FormFieldResponse;
import com.bliblifuture.invenger.response.jsonResponse.UploadProfilePictResponse;
import com.bliblifuture.invenger.response.viewDto.ProfileDTO;
import com.bliblifuture.invenger.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

    private MockMvc mvc;

    @Mock
    AccountService accountService;

    @InjectMocks
    AccountController accountController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        mvc = MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice(new DefaultControllerAdvice())
                .build();
    }


      /////////////////////////////////////////
     //public String getProfile(Model model)//
    /////////////////////////////////////////

    @Test
    public void getProfile_test() throws Exception {
        ProfileDTO user = ProfileDTO.builder().id(1).build();

        when(accountService.getProfile()).thenReturn(user);

        MockHttpServletResponse response = mvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attribute("user",user)).andReturn().getResponse();
    }

      /////////////////////////////////////////////////////////////////////////////////////////////
     //public Map<String,FormFieldResponse> postProfile(@RequestBody EditProfileRequest request)//
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void postProfile_test() throws Exception {
        HashMap<String, Object> request = new HashMap<>();
        request.put("new-telp", "12345678910");
        request.put("old-pwd", "old");
        request.put("new-pwd1", "new");
        request.put("new-pwd2", "new");

        Map<String,FormFieldResponse> formResponses = new HashMap<>();
        FormFieldResponse formResponse = new FormFieldResponse();
        formResponse.setField_name("new-telp");
        formResponse.setStatusToSuccess();
        formResponses.put("new-telp", formResponse);
        formResponse = new FormFieldResponse();
        formResponse.setField_name("new-pwd2");
        formResponse.setStatusToSuccess();
        formResponses.put("password", formResponse);


        when(accountService.editProfile(any())).thenReturn(formResponses);

        mvc.perform(post("/profile")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(accountService,times(1)).editProfile(any());
    }



      ///////////////////////////////////////////////////////////////////
     //public String getLogin(Model model, HttpServletRequest request)//
    ///////////////////////////////////////////////////////////////////

    @Test
    public void getLogin_sessionIsNotNull() throws Exception {
        User user = User.builder().id(1).build();
        when(accountService.getSessionUser()).thenReturn(user);
        mvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/profile"));
    }

    @Test
    public void getLogin_sessionIsNull() throws Exception {
        when(accountService.getSessionUser()).thenReturn(null);
        User user = new User();
        user.setUsername("myUserName");

        mvc.perform(get("/login")
                .sessionAttr("status","failed")
                .sessionAttr("username","myUserName")
        ).andExpect(status().isOk())
                .andExpect(view().name("user/login"))
                .andExpect(model().attribute("user",user));
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
     //public UploadProfilePictResponse uploadProfilePict(@RequestParam("file") MultipartFile file)//
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void uploadProfilePict_test() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.png","image/png", "".getBytes());

        UploadProfilePictResponse response = new UploadProfilePictResponse();
        response.setNew_pict_src(file.getOriginalFilename());
        response.setStatusToSuccess();

        when(accountService.changeProfilePict(file)).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.multipart("/profile/upload-pict")
                .file(file))
                .andExpect(status().isOk());
    }



}
