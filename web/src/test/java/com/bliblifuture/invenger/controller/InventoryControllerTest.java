package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.invenger.response.jsonResponse.InventoryCreateResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.service.InventoryService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class InventoryControllerTest {

    private MockMvc mvc;

    @Mock
    public InventoryService inventoryService;

    @InjectMocks
    private InventoryController controller;

    @Before
    public void init(){

        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new DefaultControllerAdvice())
                .build();

    }

      ////////////////////////////////////////////////
     //public String getInventoryTable(Model model)//
    ////////////////////////////////////////////////

    @Test
    public void getInventoryTable() throws Exception{
    }

      /////////////////////////////////////////////////////////////////////////////////////////////////////////
     //public InventoryCreateResponse addNewInventory(@Valid @ModelAttribute InventoryCreateRequest request)//
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void addNewInventory_success() throws Exception {
        InventoryCreateResponse createResponse = new InventoryCreateResponse();
        createResponse.setInventory_id(1);

        InventoryCreateRequest request = new InventoryCreateRequest();
        request.setCategory_id(1);

        when(inventoryService.createInventory(any())).thenReturn(createResponse);
        MockHttpServletResponse response = mvc.perform(
                post("/inventory/create")
                        .accept(MediaType.APPLICATION_JSON).content("")
                ).andReturn().getResponse();


        System.out.println(response);

    }

    @Test
    public void addNewInventory_invalidRequest() throws Exception {
        when(inventoryService.createInventory(any())).thenThrow(new InvalidRequestException());

        mvc.perform(
                post("/inventory/create")
                        .accept(MediaType.APPLICATION_JSON).content("")
        ).andExpect(status().isInternalServerError());

    }

    @Test
    public void editInventory_success() throws Exception {
        RequestResponse editResponse = new RequestResponse();
        editResponse.setStatusToSuccess();

        when(inventoryService.updateInventory(any())).thenReturn(editResponse);

        MockHttpServletResponse response = mvc.perform(
                post("/inventory/edit")
                        .accept(MediaType.APPLICATION_JSON).content("")
        ).andReturn().getResponse();

    }

    @Test
    public void editInventory_invalidRequest() throws Exception {
        when(inventoryService.updateInventory(any())).thenThrow(new InvalidRequestException());

        mvc.perform(
                post("/inventory/edit")
                        .accept(MediaType.APPLICATION_JSON).content("")
        ).andExpect(status().isBadRequest());

    }

    @Test
    public void deleteInventory_success() throws Exception {
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        when(inventoryService.deleteInventory(anyInt())).thenReturn(response);

        mvc.perform(post("/inventory/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(inventoryService,times(1)).deleteInventory(anyInt());

    }

    @Test
    public void deleteInventory_invalidRequest() throws Exception {
        when(inventoryService.deleteInventory(anyInt())).thenThrow(new InvalidRequestException());

        mvc.perform(post("/inventory/delete/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(inventoryService,times(1)).deleteInventory(anyInt());

    }

    @Test
    public void deleteInventory_dataNotFound() throws Exception {
        when(inventoryService.deleteInventory(anyInt())).thenThrow(new DataNotFoundException());

        mvc.perform(post("/inventory/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(inventoryService,times(1)).deleteInventory(anyInt());

    }

}
