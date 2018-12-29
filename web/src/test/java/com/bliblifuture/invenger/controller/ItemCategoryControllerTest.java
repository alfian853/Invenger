package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.response.jsonResponse.CategoryCreateResponse;
import com.bliblifuture.invenger.response.jsonResponse.CategoryEditResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.response.viewDto.CategoryDTO;
import com.bliblifuture.invenger.service.impl.ItemCategoryServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@RunWith(MockitoJUnitRunner.class)
public class ItemCategoryControllerTest {

    private MockMvc mvc;

    @Mock
    ItemCategoryServiceImpl categoryService;

    @InjectMocks
    ItemCategoryController controller;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Before
    public void init() {

        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new DefaultControllerAdvice())
                .build();

    }

      /////////////////////////////////////////////////////////////////////////////////////
     //public RequestResponse createCategory(@RequestBody CategoryCreateRequest request)//
    /////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void createCategory_success() throws Exception {

        Category category = Category.builder().id(1).name("/all").build();

        HashMap<String, Object> content = new HashMap<>();
        content.put("name", "yeah");
        content.put("parent_id", 23);

        CategoryCreateResponse mockedResponse = new CategoryCreateResponse();
        mockedResponse.setCategory(category);
        mockedResponse.setStatusToSuccess();


        when(categoryService.createCategory(any())).thenReturn(mockedResponse);

        mvc.perform(post("/category/create")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(content))
            ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.new-category").value(category));
        verify(categoryService,times(1)).createCategory(any());

    }

    @Test
    public void createCategory_invalidRequest() throws Exception {

        HashMap<String,Object> request = new HashMap<>();
        request.put("name","furniture");
        request.put("parent_id",1);

        when(categoryService.createCategory(any())).thenThrow(new InvalidRequestException());

         mvc.perform(post("/category/create")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(request))
         ).andExpect(status().isBadRequest())
         .andExpect(jsonPath("$.success").value(false))
         .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(categoryService,times(1)).createCategory(any());

    }


      /////////////////////////////////////////////////////////////////////////
     //public CategoryEditResponse editCategory(CategoryEditRequest request)//
    /////////////////////////////////////////////////////////////////////////

    @Test
    public void editCategory_success() throws Exception {

        HashMap<String, Object> request = new HashMap<>();
        request.put("id", 2);
        request.put("new_name", "furniture");
        request.put("new_parent_id", 1);

        CategoryEditResponse mockedResponse = new CategoryEditResponse();
        mockedResponse.addCategoryData(2, "/all/furniture", 1);
        mockedResponse.setStatusToSuccess();

        when(categoryService.updateCategory(any())).thenReturn(mockedResponse);

        mvc.perform(post("/category/edit")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.categories[0].name").value("/all/furniture"));

        verify(categoryService,times(1)).updateCategory(any());
    }

    @Test
    public void editCategory_failed() throws Exception {

        HashMap<String, Object> request = new HashMap<>();
        request.put("id", 2);
        request.put("new_name", "furniture");
        request.put("new_parent_id", 1);

        when(categoryService.updateCategory(any())).thenThrow(new InvalidRequestException());

        mvc.perform(post("/category/edit")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(categoryService, times(1)).updateCategory(any());

    }

      /////////////////////////////////////////////////////////////////////////
     //public RequestResponse deleteCategory(@PathVariable("id") Integer id)//
    /////////////////////////////////////////////////////////////////////////

    @Test
    public void deleteCategory_success() throws Exception {

        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        when(categoryService.deleteCategory(anyInt())).thenReturn(response);

        mvc.perform(post("/category/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(categoryService,times(1)).deleteCategory(anyInt());

    }

    @Test
    public void deleteCategory_notFound() throws Exception {

        when(categoryService.deleteCategory(anyInt())).thenThrow(new DataNotFoundException());

        mvc.perform(post("/category/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(categoryService,times(1)).deleteCategory(anyInt());

    }


    @Test
    public void deleteCategory_invalidRequest() throws Exception {

        when(categoryService.deleteCategory(anyInt())).thenThrow(new InvalidRequestException());

        mvc.perform(post("/category/delete/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(categoryService,times(1)).deleteCategory(anyInt());

    }


      //////////////////////////////////////////////
     //public String getCategoryList(Model model)//
    //////////////////////////////////////////////

    @Test
    public void getCategoryList_test() throws Exception {
        List<CategoryDTO> list = new ArrayList<>();
        list.add(CategoryDTO.builder().id(1).build());
        when(categoryService.getAllItemCategory(anyBoolean())).thenReturn(list);

        mvc.perform(get("/category/all"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("categories",list)).andReturn().getResponse();

    }

        ////////////////////////////////////////////////////////////////////////////////
       //public SearchResponse searchCategory(@RequestParam("search")String query,/////
      //                                      @RequestParam("page")Integer page,//////
     //                                     @RequestParam("length")Integer length)///
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void searchCategory_test() throws Exception {
        SearchResponse response = new SearchResponse();
        response.setRecordsFiltered(4000);
        response.setResults(new LinkedList<>());

        when(categoryService.getSearchResult(any())).thenReturn(response);

        mvc.perform(get("/category/search")
                .param("search","all")
                .param("page","2")
                .param("length","10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results").value(response.getResults()))
        .andExpect(jsonPath("$.recordsFiltered").value(response.getRecordsFiltered()));

        verify(categoryService,times(1)).getSearchResult(any());
    }


}