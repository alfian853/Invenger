package com.bliblifuture.invenger;

import com.bliblifuture.invenger.Utils.MyUtils;
import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.entity.inventory.Inventory;
import com.bliblifuture.invenger.repository.InventoryDocRepository;
import com.bliblifuture.invenger.repository.InventoryRepository;
import com.bliblifuture.invenger.repository.category.CategoryRepository;
import com.bliblifuture.invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.viewDto.InventoryDTO;
import com.bliblifuture.invenger.service.FileStorageService;
import com.bliblifuture.invenger.service.InventoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class InventoryServiceTest {

    @Spy
    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private InventoryDocRepository inventoryDocRepository;

    @Mock
    private MyUtils myUtils;

    @Mock
    private RequestResponse requestResponse;

    private static String NAME = "dummy";
    private static Integer QUANTITY = 3;
    private static Integer PRICE = 30;
    private static Integer ID = 40;
    private static String DESCRIPTION = "dummy description";
    private static String TYPE = "dummies";
    private static String IMAGE = "dummy.jpg";
    private static Category CATEGORY = Category.builder()
            .id(1)
            .name("default")
            .build();

    private static Inventory INVENTORY = Inventory.builder()
            .id(ID)
            .name(NAME)
            .quantity(QUANTITY)
            .price(PRICE)
            .category(CATEGORY)
            .description(DESCRIPTION)
            .image(IMAGE)
            .type(TYPE)
            .build();

    @Before
    public void setUp() throws Exception {

    }

    @Test(expected = NullPointerException.class)
    public void deleteInventoryTest() {
        RequestResponse response = inventoryService.deleteInventory(ID);
        Assert.assertTrue(response.isSuccess());
    }

    @Test(expected = NullPointerException.class)
    public void getByIdTest() {
        when(inventoryRepository.findInventoryById(ID)).thenReturn(INVENTORY);
        InventoryDTO inventory = inventoryService.getById(ID);
        Assert.assertEquals(inventory,INVENTORY);
    }

}