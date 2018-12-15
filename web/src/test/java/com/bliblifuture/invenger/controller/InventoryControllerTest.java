package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.invenger.response.jsonResponse.InventoryCreateResponse;
import com.bliblifuture.invenger.service.InventoryService;
import com.bliblifuture.invenger.service.ItemCategoryService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(MockitoJUnitRunner.class)
public class InventoryControllerTest {

    private MockMvc mvc;

    @Mock
    public InventoryService inventoryService;

    @Mock
    public ItemCategoryService categoryService;

    @InjectMocks
    private InventoryController controller;

    @Mock
    public UserService userService;

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
    public void getInventoryTable(){
        //nothing to test
    }

      /////////////////////////////////////////////////////////////////////////////////////////////////////////
     //public InventoryCreateResponse addNewInventory(@Valid @ModelAttribute InventoryCreateRequest request)//
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void addNewInventory() throws Exception {
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





}
