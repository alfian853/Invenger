package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.ModelMapper.inventory.InventoryMapper;
import com.bliblifuture.invenger.ModelMapper.inventory.InventoryMapperImpl;
import com.bliblifuture.invenger.Utils.MyUtils;
import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.entity.inventory.Inventory;
import com.bliblifuture.invenger.entity.inventory.InventoryDocument;
import com.bliblifuture.invenger.entity.inventory.InventoryType;
import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.DefaultRuntimeException;
import com.bliblifuture.invenger.exception.DuplicateEntryException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.repository.InventoryDocRepository;
import com.bliblifuture.invenger.repository.InventoryRepository;
import com.bliblifuture.invenger.repository.category.CategoryRepository;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.invenger.request.formRequest.InventoryEditRequest;
import com.bliblifuture.invenger.request.jsonRequest.SearchRequest;
import com.bliblifuture.invenger.response.jsonResponse.InventoryCreateResponse;
import com.bliblifuture.invenger.response.jsonResponse.InventoryDocDownloadResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.viewDto.InventoryDTO;
import com.bliblifuture.invenger.service.impl.InventoryServiceImpl;
import org.assertj.core.util.DateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.util.DateUtil.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class InventoryServiceTest {

    @InjectMocks
    private InventoryServiceImpl inventoryService;

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

    private final InventoryMapper mapper = new InventoryMapperImpl();

    private static String NAME = "dummy";
    private static Integer QUANTITY = 3;
    private static Integer PRICE = 30;
    private static Integer ID = 40;
    private static String DESCRIPTION = "dummy description";
    private static String TYPE = "dummies";
    private static String IMAGE = "dummy.jpg";

    private static Inventory INVENTORY;

    private InventoryDTO INVENTORY_DTO;

    @Before
    public void init() {
        Category category = Category.builder()
                .id(1)
                .name("default")
                .build();
        INVENTORY = Inventory.builder()
                .id(ID)
                .name(NAME)
                .quantity(QUANTITY)
                .price(PRICE)
                .category(category)
                .description(DESCRIPTION)
                .image(IMAGE)
                .type(TYPE)
                .build();

        INVENTORY.setCreatedAt(now());
        INVENTORY.setUpdatedAt(INVENTORY.getCreatedAt());

        INVENTORY_DTO = mapper.toDto(INVENTORY);
    }

      ///////////////////////////////////////////
     //public InventoryDTO getById(Integer id)//
    ///////////////////////////////////////////

    @Test(expected = DataNotFoundException.class)
    public void getById_notFound() {
        when(inventoryRepository.findInventoryById(ID)).thenReturn(null);
        inventoryService.getById(ID);
    }

    @Test
    public void getById_found() {
        when(inventoryRepository.findInventoryById(ID)).thenReturn(INVENTORY);
        Assert.assertEquals(INVENTORY_DTO, inventoryService.getById(ID));
    }


      ///////////////////////////////
     //List<InventoryDTO> getAll()//
    ///////////////////////////////

    @Test
    public void getAll_test(){
        Category category = Category.builder().id(1).name("/test").build();
        LinkedList<Inventory> inventories = new LinkedList<>();
        inventories.push(Inventory.builder().category(category).build());
        when(inventoryRepository.findAllFetchCategory())
                .thenReturn(inventories);

        Assert.assertEquals(inventoryService.getAll(),mapper.toDtoList(inventories));
    }




      //////////////////////////////////////////////////////////////////////////////////
     //public InventoryCreateResponse createInventory(InventoryCreateRequest request)//
    //////////////////////////////////////////////////////////////////////////////////

    private InventoryCreateRequest mock_inventoryCreateRequest(boolean fileIsNull) {
        InventoryCreateRequest request = new InventoryCreateRequest();
        request.setName("barang");
        request.setCategory_id(1);
        request.setPrice(200);
        request.setDescription("");
        request.setQuantity(209);
        request.setType(InventoryType.Stockable);
        if (!fileIsNull) {
            request.setPhoto_file(
                    new MockMultipartFile("file", "orig", null, "bar".getBytes())
            );
        }

        return request;
    }

    @Test
    public void createInventory_photoNull_saveSuccess() {
        InventoryCreateRequest request = this.mock_inventoryCreateRequest(true);
        InventoryCreateResponse response = new InventoryCreateResponse();
        response.setStatusToSuccess();

        Assert.assertEquals(
                inventoryService.createInventory(request), response
        );

        verify(fileStorageService, times(0)).storeFile(any(), any(), any());
    }

    @Test(expected = DuplicateEntryException.class)
    public void createInventory_photoNull_saveDuplicateName() {
        when(inventoryRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate"));
        inventoryService.createInventory(this.mock_inventoryCreateRequest(true));
    }


    @Test
    public void createInventory_photoNotNull_saveSuccess() {
        InventoryCreateRequest request = this.mock_inventoryCreateRequest(false);
        InventoryCreateResponse response = new InventoryCreateResponse();
        response.setStatusToSuccess();
        when(fileStorageService.storeFile(any(), any(), any())).thenReturn(true);
        Assert.assertEquals(
                inventoryService.createInventory(request), response
        );
        verify(fileStorageService, times(1)).storeFile(any(), any(), any());
    }

      ////////////////////////////////////////////////////////////////////////
     //public RequestResponse updateInventory(InventoryEditRequest request)//
    ////////////////////////////////////////////////////////////////////////

    private InventoryEditRequest mock_inventoryEditRequest(boolean withFile) {
        InventoryEditRequest request = new InventoryEditRequest();
        request.setId(INVENTORY.getId());
        request.setName(INVENTORY.getName());
        request.setCategory_id(INVENTORY.getCategory().getId());
        request.setPrice(INVENTORY.getPrice());
        request.setQuantity(209);
        request.setType(InventoryType.Stockable);
        request.setDescription(INVENTORY.getDescription());

        if (withFile) {
            request.setPict(new MockMultipartFile("file", "orig", null, "bar".getBytes()));
        }

        return request;
    }

    @Test(expected = DataNotFoundException.class)
    public void updateInventory_inventoryNotFound() {
        when(inventoryRepository.getOne(any())).thenReturn(null);
        inventoryService.updateInventory(this.mock_inventoryEditRequest(true));
    }

    @Test
    public void updateInventory_editPicture_storeFailed() {
        when(inventoryRepository.getOne(any())).thenReturn(INVENTORY);
        when(fileStorageService.storeFile(any(), any(), any())).thenReturn(false);

        try {
            inventoryService.updateInventory(this.mock_inventoryEditRequest(true));
        } catch (Exception e) {
            Assert.assertTrue(e instanceof DefaultRuntimeException);
        }
        verify(fileStorageService, times(1)).storeFile(any(), any(), any());
        verify(inventoryRepository, times(0)).save(any());
    }

    @Test
    public void updateInventory_editPicture_success() {

        when(inventoryRepository.getOne(any())).thenReturn(INVENTORY);
        when(fileStorageService.storeFile(any(), any(), any())).thenReturn(true);


        RequestResponse response = inventoryService.updateInventory(this.mock_inventoryEditRequest(true));

        Assert.assertTrue(response.isSuccess());

        verify(fileStorageService, times(1)).storeFile(any(), any(), any());
        verify(fileStorageService, times(1)).deleteFile(any(), any());

    }

      //////////////////////////////////////////////////
     //public RequestResponse deleteInventory(int id)//
    //////////////////////////////////////////////////

    @Test
    public void deleteInventory_test() {
        RequestResponse response = inventoryService.deleteInventory(1);
        Assert.assertTrue(response.getSuccess());
    }

      //////////////////////////////////////////////////////////////////////
     //public InventoryDocDownloadResponse downloadItemDetail(Integer id)//
    //////////////////////////////////////////////////////////////////////

    @Test(expected = DataNotFoundException.class)
    public void downloadItemDetail_notFound() {
        when(inventoryRepository.findInventoryById(any())).thenReturn(null);
        inventoryService.downloadItemDetail(1);
    }

    @Test
    public void downloadItemDetail_documentDoesNotCreatedYet() {
        when(inventoryRepository.findInventoryById(any())).thenReturn(INVENTORY);
        when(inventoryDocRepository.findInventoryDocumentById(INVENTORY.getId())).thenReturn(null);
        when(fileStorageService.createPdfFromTemplate(any(),any(), any(), any())).thenReturn("filename.pdf");

        InventoryDocDownloadResponse response = inventoryService.downloadItemDetail(INVENTORY.getId());

        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getInventoryDocUrl(), "/inventory/document/" + "filename.pdf");

        verify(inventoryDocRepository, times(1)).save(any());

    }

    @Test
    public void downloadItemDetail_documentAlreadyExist(){
        when(inventoryRepository.findInventoryById(any())).thenReturn(INVENTORY);
        InventoryDocument document = InventoryDocument.builder()
                .fileName("filename.pdf")
                .inventoryLastUpdate(INVENTORY.getUpdatedAt())
                .build();

        when(inventoryDocRepository.findInventoryDocumentById(INVENTORY.getId())).thenReturn(document);

        InventoryDocDownloadResponse response = inventoryService.downloadItemDetail(INVENTORY.getId());

        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getInventoryDocUrl(), "/inventory/document/" + "filename.pdf");

        verify(inventoryDocRepository, times(0)).save(any());
    }

    @Test
    public void downloadItemDetail_documentOldVersion_successCreateNew() {
        when(inventoryRepository.findInventoryById(any())).thenReturn(INVENTORY);
        InventoryDocument document = InventoryDocument.builder()
                .fileName("filename_lama.pdf")
                .inventoryLastUpdate(DateUtil.tomorrow())
                .build();

        when(inventoryDocRepository.findInventoryDocumentById(INVENTORY.getId())).thenReturn(document);
        when(fileStorageService.createPdfFromTemplate(any(), any(), any(), any())).thenReturn("filename_baru.pdf");

        InventoryDocDownloadResponse response = inventoryService.downloadItemDetail(INVENTORY.getId());

        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getInventoryDocUrl(), "/inventory/document/" + "filename_baru.pdf");
        verify(fileStorageService,times(1)).createPdfFromTemplate(any(), any(), any(), any());
        verify(inventoryDocRepository, times(1)).save(any());
    }

    @Test
    public void downloadItemDetail_documentOldVersion_failedCreateNew() {
        when(inventoryRepository.findInventoryById(any())).thenReturn(INVENTORY);
        InventoryDocument document = InventoryDocument.builder()
                .fileName("filename_lama.pdf")
                .inventoryLastUpdate(DateUtil.tomorrow())
                .build();

        when(inventoryDocRepository.findInventoryDocumentById(INVENTORY.getId())).thenReturn(document);
        when(fileStorageService.createPdfFromTemplate(any(), any(), any(), any())).thenReturn(null);

        InventoryDocDownloadResponse response = inventoryService.downloadItemDetail(INVENTORY.getId());

        Assert.assertFalse(response.isSuccess());
        Assert.assertNull(response.getInventoryDocUrl());

        verify(fileStorageService,times(1)).createPdfFromTemplate(any(), any(), any(), any());
    }



      ////////////////////////////////////////////////////////////////////////////////////////////////////
     //public DataTablesResult<InventoryDataTableResponse> getDatatablesData(DataTablesRequest request)//
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private MockHttpServletRequest mock_datatableServletRequest(boolean hasSearchValue) {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addParameter("columns[0][data]", "id");
        servletRequest.addParameter("columns[0][name]", "id");
        servletRequest.addParameter("columns[0][orderable]", "true");
        servletRequest.addParameter("columns[0][search][regex]", "false");
        servletRequest.addParameter("columns[0][search][value]", (hasSearchValue) ? "123" : "");
        servletRequest.addParameter("columns[0][searchable]", "true");
        servletRequest.addParameter("draw", "1");
        servletRequest.addParameter("length", "10");
        servletRequest.addParameter("order[0][column]", "0");
        servletRequest.addParameter("order[0][dir]", "asc");
        servletRequest.addParameter("search[regex]", "false");
        servletRequest.addParameter("search[value]", "");
        servletRequest.addParameter("start", "0");
        return servletRequest;
    }

    @Test
    public void getPaginatedDatatablesInventoryList_onlySortByColumn() {

        DataTablesRequest request = new DataTablesRequest(this.mock_datatableServletRequest(false));
        Page<Inventory> page = new PageImpl<>(new ArrayList<>());

        when(inventoryRepository.findAll(any(PageRequest.class))).thenReturn(page);

        inventoryService.getDatatablesData(request);

        verify(inventoryRepository, times(1)).findAll(any(PageRequest.class));

    }

    @Test
    public void getPaginatedDatatablesInventoryList_sortAndSearchByColumn() {

        MockHttpServletRequest servletRequest = this.mock_datatableServletRequest(true);
        DataTablesRequest request = new DataTablesRequest(servletRequest);

        Page<Inventory> page = new PageImpl<>(new ArrayList<>());
        when(inventoryRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        inventoryService.getDatatablesData(request);

        verify(inventoryRepository, times(1))
                .findAll(any(Specification.class), any(PageRequest.class));

    }

      /////////////////////////////////////////////////////////////////////////////////////////////
     //public SearchResponse getSearchedInventory(String query, Integer pageNum, Integer length)//
    /////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void getSearchedInventory_test() {
        Page<Inventory> page = new PageImpl<>(new ArrayList<>());

        when(inventoryRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        inventoryService.getSearchResult(SearchRequest.builder()
                .query("query").pageNum(1).length(10).build()
        );

        verify(inventoryRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
    }


      ////////////////////////////////////////////////////////////////
     //public RequestResponse insertInventories(MultipartFile file)//
    ////////////////////////////////////////////////////////////////

    @Test
    public void insertInventories_validRequest() {
        String content = "name,price,quantity,type,description\n" +
                "barang1,999,100,Stockable\n" +
                "barang2,200,59,Consumable\n" +
                "barang3,100,300,Consumable\n";

        MockMultipartFile file = new MockMultipartFile("file", "orig", null, content.getBytes());

        RequestResponse response = inventoryService.insertInventories(file);

        Assert.assertTrue(response.isSuccess());
    }

    @Test(expected = InvalidRequestException.class)
    public void insertInventories_invalidRequest() {
        String content = "?,?,???,????,?????\n" +
                "barang1,999,100,Stockable\n" +
                "barang2,200,59,Consumable\n" +
                "barang3,100,300,Consumable\n";

        MockMultipartFile file = new MockMultipartFile("file", "orig", null, content.getBytes());
        inventoryService.insertInventories(file);

    }


}