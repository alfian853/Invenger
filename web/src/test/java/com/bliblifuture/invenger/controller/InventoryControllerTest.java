package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.entity.inventory.Inventory;
import com.bliblifuture.invenger.entity.inventory.InventoryType;
import com.bliblifuture.invenger.request.formRequest.InventoryEditRequest;
import com.bliblifuture.invenger.response.jsonResponse.*;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.response.viewDto.CategoryDTO;
import com.bliblifuture.invenger.response.viewDto.InventoryDTO;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class InventoryControllerTest {

    private MockMvc mvc;

    @Mock
    private ItemCategoryService itemCategoryService;

    @Mock
    private UserService userService;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController controller;

    @Before
    public void init(){

        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new DefaultControllerAdvice())
                .build();

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //public InventoryCreateResponse addNewInventory(@Valid @ModelAttribute InventoryCreateRequest request)//
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void addNewInventory_test() throws Exception {
        Category category = new Category().builder().id(1).name("/all").build();
        Inventory inventory = new Inventory().builder()
                .id(1)
                .name("name")
                .category(category)
                .type(InventoryType.Consumable.toString())
                .quantity(4)
                .build();

        System.out.println(inventory);

        InventoryCreateResponse response = new InventoryCreateResponse();
        response.setInventory_id(inventory.getId());
        response.setStatusToSuccess();

        when(inventoryService.createInventory(any())).thenReturn(response);

        mvc.perform(post("/inventory/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name","name")
                .param("category_id", "1")
                .param("type","Consumable")
                .param("quantity","4")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //public RequestResponse editInventory(@Valid @ModelAttribute InventoryEditRequest request)//
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void editInventory_test() throws Exception {
        InventoryEditRequest request = new InventoryEditRequest();
        request.setId(1);
        request.setName("name2");
        request.setCategory_id(1);
        request.setType(InventoryType.Consumable);
        request.setQuantity(4);

        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();
        when(inventoryService.updateInventory(request)).thenReturn(response);

        mvc.perform(post("/inventory/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("name","name2")
                .param("category_id", "1")
                .param("type","Consumable")
                .param("quantity","4")
        )
                .andExpect(status().isOk());

    }

    //////////////////////////////////////////////////////////////////////////
    //public RequestResponse removeInventory(@PathVariable("id") Integer id)//
    //////////////////////////////////////////////////////////////////////////

    @Test
    public void deleteInventory_test() throws Exception {
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        when(inventoryService.deleteInventory(anyInt())).thenReturn(response);

        mvc.perform(post("/inventory/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(inventoryService,times(1)).deleteInventory(anyInt());

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //public SearchResponse searchInventory(@RequestParam("search")String query, @RequestParam("page")Integer page, @RequestParam("length")Integer length)//
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void searchInventory_test() throws Exception {
        SearchResponse response = new SearchResponse();
        response.setRecordsFiltered(4000);
        response.setResults(new LinkedList<>());

        when(inventoryService.getSearchResult(any())).thenReturn(response);

        mvc.perform(get("/inventory/search")
                .param("search","all")
                .param("page","2")
                .param("length","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").value(response.getResults()))
                .andExpect(jsonPath("$.recordsFiltered").value(response.getRecordsFiltered()));

        verify(inventoryService,times(1)).getSearchResult(any());
    }

    ///////////////////////////////////////////////////////////////////////////
    //public String downloadInventoryDocument(@PathVariable("id") Integer id)//
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void downloadInventoryDocument_test() throws Exception {
        Category category = Category.builder().id(1).name("/all").build();
        InventoryDTO inventory = InventoryDTO.builder()
                .id(1)
                .name("name")
                .quantity(2)
                .type(InventoryType.Consumable.toString())
                .category(category.getName())
                .category_id(category.getId())
                .build();

        System.out.println(inventory);

        InventoryDocDownloadResponse response = new InventoryDocDownloadResponse();
        response.setInventoryDocUrl("some_url");

        when(inventoryService.downloadItemDetail(1)).thenReturn(response);

        mvc.perform(get("/inventory/detail/{id}/download", 1)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:"+response.getInventoryDocUrl()));

    }

    //////////////////////////////////////////////////////////////////////////////////////
    //public RequestResponse uploadInventories(@RequestParam("file") MultipartFile file)//
    //////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void uploadInventories_test() throws Exception {
        String content = "name,price,quantity,type,description\n" +
                "barang1,999,100,Stockable\n" +
                "barang2,200,59,Consumable\n" +
                "barang3,100,300,Consumable\n";

        MockMultipartFile file = new MockMultipartFile("file", "orig", null, content.getBytes());

        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        when(inventoryService.insertInventories(file)).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.multipart("/inventory/upload")
                .file(file))
                .andExpect(status().isOk());
    }

    /////////////////////////////////////////////////////////////////////////////////
    //public String getInventoryDetail(Model model, @PathVariable("id") Integer id)//
    /////////////////////////////////////////////////////////////////////////////////

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getInventoryDetail_test() throws Exception{
        Category category = new Category().builder().id(1).name("/all").build();
        InventoryDTO inventory = InventoryDTO.builder()
                .id(1)
                .name("name")
                .quantity(2)
                .type(InventoryType.Consumable.toString())
                .category(category.getName())
                .category_id(category.getId())
                .build();

        System.out.println(inventory);

        when(inventoryService.getById(1)).thenReturn(inventory);
        when(userService.currentUserIsAdmin()).thenReturn(true);

        mvc.perform(get("/inventory/detail/{id}", 1)
        )
                .andExpect(status().isOk());

        verify(inventoryService, times(1)).getById(1);
    }

    ////////////////////////////////////////////////
    //public String getInventoryTable(Model model)//
    ////////////////////////////////////////////////

    @Test
    public void getInventoryTable_test() throws Exception {
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        categoryDTOS.add(CategoryDTO.builder().id(1).name("/all").build());
        List<InventoryDTO> list = new ArrayList<>();
        list.add(InventoryDTO.builder()
                .id(1)
                .name("name")
                .quantity(2)
                .type(InventoryType.Consumable.toString())
                .category("/all")
                .category_id(1)
                .build());

        System.out.println(list);

        when(inventoryService.getAll()).thenReturn(list);
        when(itemCategoryService.getAllItemCategory(anyBoolean())).thenReturn(categoryDTOS);

        mvc.perform(get("/inventory/all")
        )
                .andExpect(status().isOk());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //public DataTablesResult<InventoryDataTableResponse> getPaginatedInventories(HttpServletRequest servletRequest)//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetPaginatedInventories_test() throws Exception {
    }

}
