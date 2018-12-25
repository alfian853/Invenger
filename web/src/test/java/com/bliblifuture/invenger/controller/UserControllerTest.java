package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.jsonResponse.UserCreateResponse;
import com.bliblifuture.invenger.response.viewDto.UserDTO;
import com.bliblifuture.invenger.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private MockMvc mvc;

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @Before
    public void init() {
        mvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new DefaultControllerAdvice())
                .build();
    }

    @Test
    public void getAll() throws Exception {
        List<UserDTO> users = new ArrayList<>();
        users.add(UserDTO.builder().id(1).build());

        when(userService.getAll()).thenReturn(users);

        MockHttpServletResponse response = mvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("users",users)).andReturn().getResponse();

    }

    @Test
    public void createUser_success() throws Exception {
        UserCreateResponse createResponse = new UserCreateResponse();
        createResponse.setUser_id(1);

        when(userService.createUser(any())).thenReturn(createResponse);

        MockHttpServletResponse response = mvc.perform(
                post("/user/create")
                        .accept(MediaType.APPLICATION_JSON).content("")
        ).andReturn().getResponse();

    }

    @Test
    public void createUser_invalidRequest() throws Exception {
        when(userService.createUser(any())).thenThrow(new InvalidRequestException());

        mvc.perform(
                post("/user/create")
                        .accept(MediaType.APPLICATION_JSON).content("")
        ).andExpect(status().isInternalServerError());

    }

    @Test
    public void editUser_success() throws Exception {
        RequestResponse editResponse = new RequestResponse();
        editResponse.setStatusToSuccess();

        when(userService.updateUser(any())).thenReturn(editResponse);

        MockHttpServletResponse response = mvc.perform(
                post("/user/edit")
                        .accept(MediaType.APPLICATION_JSON).content("")
        ).andReturn().getResponse();

    }

    @Test
    public void editUser_invalidRequest() throws Exception {
        when(userService.updateUser(any())).thenThrow(new InvalidRequestException());

        mvc.perform(
                post("/user/edit")
                        .accept(MediaType.APPLICATION_JSON).content("")
        ).andExpect(status().isBadRequest());

    }

    @Test
    public void deleteUser_success() throws Exception {
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        when(userService.deleteUser(anyInt())).thenReturn(response);

        mvc.perform(post("/user/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService,times(1)).deleteUser(anyInt());

    }

    @Test
    public void deleteUser_invalidRequest() throws Exception {
        when(userService.deleteUser(anyInt())).thenThrow(new InvalidRequestException());

        mvc.perform(post("/user/delete/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(userService,times(1)).deleteUser(anyInt());

    }

    @Test
    public void deleteUser_userNotFound() throws Exception {
        when(userService.deleteUser(anyInt())).thenThrow(new DataNotFoundException());

        mvc.perform(post("/user/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(userService,times(1)).deleteUser(anyInt());

    }

}