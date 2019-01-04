package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.ModelMapper.lendment.LendmentMapper;
import com.bliblifuture.invenger.entity.inventory.Inventory;
import com.bliblifuture.invenger.entity.lendment.Lendment;
import com.bliblifuture.invenger.entity.lendment.LendmentDetail;
import com.bliblifuture.invenger.entity.lendment.LendmentDetailIdentity;
import com.bliblifuture.invenger.entity.lendment.LendmentStatus;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.repository.InventoryRepository;
import com.bliblifuture.invenger.repository.LendmentDetailRepository;
import com.bliblifuture.invenger.repository.LendmentRepository;
import com.bliblifuture.invenger.repository.UserRepository;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.request.jsonRequest.LendmentCreateRequest;
import com.bliblifuture.invenger.request.jsonRequest.LendmentReturnRequest;
import com.bliblifuture.invenger.response.viewDto.LendmentDTO;
import com.bliblifuture.invenger.service.impl.LendmentServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LendmentServiceTest {

    @InjectMocks
    LendmentServiceImpl lendmentService;

    @Mock
    LendmentRepository lendmentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    LendmentDetailRepository lendmentDetailRepository;

    @Mock
    InventoryRepository inventoryRepository;

    @Mock
    AccountService accountService;

    private final LendmentMapper mapper = Mappers.getMapper(LendmentMapper.class);

    private static User USER = User.builder().id(1).username("invenger").build();
    private static List<Lendment> LENDMENTS;
    private static List<LendmentDTO> LENDMENTS_DTO_WITHOUT_DETAIL;
    private static List<LendmentDTO> LENDMENTS_DTO_WITH_DETAIL;
    private static Lendment LENDMENT;

    private User borrower = User.builder().id(2).build();

    @Before
    public void init(){
        LendmentDetail detail = LendmentDetail.builder()
                .cmpId(new LendmentDetailIdentity(1,1))
                .inventory(Inventory.builder().id(1).quantity(1).price(1).build())
                .quantity(1)
                .build();

        LENDMENT = Lendment.builder()
                .id(1)
                .user(USER)
                .lendmentDetails(new LinkedList<LendmentDetail>(){{this.push(detail);}})
                .notReturnedCount(1)
                .status(LendmentStatus.InLending.getDesc())
                .build();

        LENDMENTS = new LinkedList<>();

        LENDMENTS.add(LENDMENT);

        LENDMENTS_DTO_WITH_DETAIL = mapper.toDtoList(LENDMENTS,true);
        LENDMENTS_DTO_WITHOUT_DETAIL = mapper.toDtoList(LENDMENTS,false);
    }


      ////////////////////////////////////////////////////////////////////////
     //public RequestResponse createLendment(LendmentCreateRequest request)//
    ////////////////////////////////////////////////////////////////////////

    private LendmentCreateRequest mock_lendmentCreateRequest(){
        LendmentCreateRequest request = new LendmentCreateRequest();
        request.setItems(new LinkedList<>());
        request.getItems().add(LendmentCreateRequest.Item.builder().id(1).quantity(5).build());
        request.getItems().add(LendmentCreateRequest.Item.builder().id(2).quantity(10).build());
        request.setUserId(borrower.getId());

        return request;
    }

    private Lendment mock_lendmentBuilder(LendmentCreateRequest request, boolean isAdminRequest){
        return Lendment.builder()
                .user(borrower)
                .status((isAdminRequest?LendmentStatus.WaitingForPickUp:LendmentStatus.WaitingForApproval).getDesc())
                .notReturnedCount(request.getItems().size())
                .build();
    }

    @Captor
    ArgumentCaptor<Lendment> captor = ArgumentCaptor.forClass(Lendment.class);

    @Test
    public void createLendment_adminRequest(){
        when(accountService.currentUserIsAdmin()).thenReturn(true);

        when(inventoryRepository.findInventoryById(1)).thenReturn(Inventory.builder().id(1).quantity(5).build());
        when(inventoryRepository.findInventoryById(2)).thenReturn(Inventory.builder().id(2).quantity(10).build());

        when(userRepository.getOne(borrower.getId())).thenReturn(borrower);

        LendmentCreateRequest request = this.mock_lendmentCreateRequest();

        Assert.assertTrue(lendmentService.createLendment(request).isSuccess());

        verify(lendmentRepository).save(captor.capture());
        Assert.assertEquals(captor.getValue(),this.mock_lendmentBuilder(request,true));
    }

    @Test
    public void createLendment_userRequest(){
        when(accountService.currentUserIsAdmin()).thenReturn(false);

        when(inventoryRepository.findInventoryById(1)).thenReturn(Inventory.builder().id(1).quantity(5).build());
        when(inventoryRepository.findInventoryById(2)).thenReturn(Inventory.builder().id(2).quantity(10).build());

        when(userRepository.getOne(borrower.getId())).thenReturn(borrower);

        LendmentCreateRequest request = this.mock_lendmentCreateRequest();

        Assert.assertTrue(lendmentService.createLendment(request).isSuccess());

        verify(lendmentRepository).save(captor.capture());
        Assert.assertEquals(captor.getValue(),this.mock_lendmentBuilder(request,false));
    }

    @Test(expected = InvalidRequestException.class)
    public void createLendment_invalidRequest(){
        when(accountService.currentUserIsAdmin()).thenReturn(true);

        when(inventoryRepository.findInventoryById(1)).thenReturn(Inventory.builder().id(1).quantity(0).build());

        when(userRepository.getOne(borrower.getId())).thenReturn(borrower);

        LendmentCreateRequest request = this.mock_lendmentCreateRequest();

        lendmentService.createLendment(request);
    }

      /////////////////////////////////////
     //public List<LendmentDTO> getAll()//
    /////////////////////////////////////

    @Test
    public void getAll_test(){
        when(lendmentRepository.findAll()).thenReturn(LENDMENTS);
        Assert.assertEquals(
                lendmentService.getAll().get(0),
                mapper.toDto(LENDMENTS.get(0))
        );
    }


      ///////////////////////////////////////////
     //public List<LendmentDTO> getAllByUser()//
    ///////////////////////////////////////////

    @Test
    public void getAllByUser_test(){
        when(accountService.getSessionUser()).thenReturn(USER);
        when(lendmentRepository.findAllByUserId(any())).thenReturn(LENDMENTS);

        Assert.assertEquals(lendmentService.getAllByUser(),LENDMENTS_DTO_WITHOUT_DETAIL);
    }


      ////////////////////////////////////////////////////////
     //public LendmentDTO getLendmentDetailById(Integer id)//
    ////////////////////////////////////////////////////////

    @Test(expected = DataNotFoundException.class)
    public void getLendmentDetailById_notFound(){
        when(lendmentRepository.findLendmentById(any())).thenReturn(null);
        lendmentService.getLendmentDetailById(2);
    }

    @Test
    public void getLendmentDetailById_found() {
        when(lendmentRepository.findLendmentById(LENDMENT.getId())).thenReturn(LENDMENT);

        Assert.assertEquals(
                lendmentService.getLendmentDetailById(LENDMENT.getId()),
                mapper.toLendmentWithDetailDTO(LENDMENT)
        );
    }


      //////////////////////////////////////////////////////////////
     //public List<LendmentDTO> getAllLendmentRequestOfSuperior()//
    //////////////////////////////////////////////////////////////

    @Test
    public void getAllLendmentRequestOfSuperior_test(){
        when(accountService.getSessionUser()).thenReturn(USER);
        when(lendmentRepository.findAllBySuperiorIdAndStatus(any(),any())).thenReturn(LENDMENTS);

        Assert.assertEquals(
                lendmentService.getAllLendmentRequestOfSuperior(),
                LENDMENTS_DTO_WITHOUT_DETAIL
        );
    }


      /////////////////////////////////////////////////////////////////////////
     //public RequestResponse returnInventory(LendmentReturnRequest request)//
    /////////////////////////////////////////////////////////////////////////

    private LendmentReturnRequest mock_lendmentReturnRequest(){
        LendmentReturnRequest request = new LendmentReturnRequest();
        request.setLendmentId(LENDMENT.getId());
        request.setInventoriesId(
                Collections.singletonList( LENDMENT.getLendmentDetails().get(0).getCmpId().getInventoryId() )
        );
        return request;
    }

    @Test(expected = InvalidRequestException.class)
    public void returnInventory_invalidRequest(){
        LendmentReturnRequest request = this.mock_lendmentReturnRequest();
        LENDMENT.setNotReturnedCount(1);
        LENDMENT.setStatus(LendmentStatus.WaitingForApproval.getDesc());
        when(lendmentRepository.findLendmentById(request.getLendmentId())).thenReturn(LENDMENT);

        lendmentService.returnInventory(request);

    }

    @Test
    public void returnInventory_allReturned_success(){
        LendmentReturnRequest request = this.mock_lendmentReturnRequest();
        LENDMENT.setNotReturnedCount(1);
        LENDMENT.setStatus(LendmentStatus.InLending.getDesc());
        when(lendmentRepository.findLendmentById(request.getLendmentId())).thenReturn(LENDMENT);

        when(lendmentDetailRepository.getOne(any())).thenReturn(LENDMENT.getLendmentDetails().get(0));

        Assert.assertTrue(lendmentService.returnInventory(request).isSuccess());

        Assert.assertEquals(LENDMENT.getNotReturnedCount(),new Integer(0));
        Assert.assertEquals(LENDMENT.getStatus(),LendmentStatus.Finished.getDesc());

    }

    @Test
    public void returnInventory_partialReturned_success(){
        LendmentReturnRequest request = this.mock_lendmentReturnRequest();
        LENDMENT.setNotReturnedCount(2);
        LENDMENT.setStatus(LendmentStatus.InLending.getDesc());
        when(lendmentRepository.findLendmentById(request.getLendmentId())).thenReturn(LENDMENT);

        when(lendmentDetailRepository.getOne(any())).thenReturn(LENDMENT.getLendmentDetails().get(0));

        Assert.assertTrue(lendmentService.returnInventory(request).isSuccess());

        Assert.assertEquals(LENDMENT.getNotReturnedCount(),new Integer(1));
        Assert.assertEquals(LENDMENT.getStatus(),LendmentStatus.InLending.getDesc());
    }


    @Test(expected = InvalidRequestException.class)
    public void returnInventory_returnReturnedItem(){
        LendmentReturnRequest request = this.mock_lendmentReturnRequest();
        LENDMENT.setNotReturnedCount(2);
        LENDMENT.setStatus(LendmentStatus.InLending.getDesc());
        when(lendmentRepository.findLendmentById(request.getLendmentId())).thenReturn(LENDMENT);

        when(lendmentDetailRepository.getOne(any())).thenReturn(LendmentDetail.builder().isReturned(true).build());

        lendmentService.returnInventory(request);
    }



      /////////////////////////////////////////////////////////////
     //public RequestResponse assignLendmentRequest(Integer id)//
    /////////////////////////////////////////////////////////////

    @Test(expected = DataNotFoundException.class)
    public void approveLendmentRequest_notFound(){
        when(lendmentRepository.findLendmentById(1)).thenReturn(null);
        lendmentService.assignLendmentRequest(1,true);
    }

    @Test(expected = InvalidRequestException.class)
    public void assignLendmentRequest_approve_InvalidRequest(){
        LENDMENT.setStatus(LendmentStatus.InLending.getDesc());
        when(lendmentRepository.findLendmentById(LENDMENT.getId())).thenReturn(LENDMENT);
        lendmentService.assignLendmentRequest(LENDMENT.getId(),true);
    }

    @Test
    public void assignLendmentRequest_approve_Success(){
        LENDMENT.setStatus(LendmentStatus.WaitingForApproval.getDesc());
        when(lendmentRepository.findLendmentById(LENDMENT.getId())).thenReturn(LENDMENT);
        Assert.assertTrue(
                lendmentService.assignLendmentRequest(LENDMENT.getId(),true).isSuccess()
        );
    }

    @Test
    public void assignLendmentRequest_disapprove_Success(){
        LENDMENT.setStatus(LendmentStatus.WaitingForApproval.getDesc());
        when(lendmentRepository.findLendmentById(LENDMENT.getId())).thenReturn(LENDMENT);
        Assert.assertTrue(
                lendmentService.assignLendmentRequest(LENDMENT.getId(),false).isSuccess()
        );
    }



      //////////////////////////////////////////////////////////
     //public HandOverResponse handOverOrderItems(Integer id)//
    //////////////////////////////////////////////////////////

    @Test(expected = DataNotFoundException.class)
    public void handOverOrderItems_notFound(){
        when(lendmentRepository.findLendmentById(1)).thenReturn(null);
        lendmentService.handOverOrderItems(1);
    }

    @Test
    public void handOverOrderItems_success(){
        LENDMENT.setStatus(LendmentStatus.WaitingForPickUp.getDesc());
        USER.setSuperior(User.builder().id(40).build());
        when(lendmentRepository.findLendmentById(LENDMENT.getId())).thenReturn(LENDMENT);
        when(accountService.getSessionUser()).thenReturn(User.builder().id(40).build());
        Assert.assertTrue(
                lendmentService.handOverOrderItems(LENDMENT.getId()).isSuccess()
        );
    }

    @Test(expected = InvalidRequestException.class)
    public void handOverOrderItems_assignOtherInferiorRequest(){
        LENDMENT.setStatus(LendmentStatus.WaitingForPickUp.getDesc());
        USER.setSuperior(User.builder().id(2).build());
        when(lendmentRepository.findLendmentById(LENDMENT.getId())).thenReturn(LENDMENT);
        when(accountService.getSessionUser()).thenReturn(User.builder().superior(USER).build());
        lendmentService.handOverOrderItems(LENDMENT.getId());
    }



      /////////////////////////////////////////////////////////////
     //public List<LendmentDTO> getInventoryLendment(Integer id)//
    /////////////////////////////////////////////////////////////

    @Test
    public void getInventoryLendment_test(){
        when(lendmentRepository.findLendmentContainInventory(1)).thenReturn(LENDMENTS);
        Assert.assertEquals(
                lendmentService.getInventoryLendment(LENDMENT.getId()),
                LENDMENTS_DTO_WITH_DETAIL);
    }



      ////////////////////////////////////////////////////////////////////////////////////////////
     //DataTablesResult<LendmentDataTableResponse> getDatatablesData(DataTablesRequest request)//
    ////////////////////////////////////////////////////////////////////////////////////////////

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
    public void getDatatablesData_onlySortByColumn() {

        DataTablesRequest request = new DataTablesRequest(this.mock_datatableServletRequest(false));
        Page<Lendment> page = new PageImpl<>(new ArrayList<>());

        when(lendmentRepository.findAll(any(PageRequest.class))).thenReturn(page);

        when(accountService.currentUserIsAdmin()).thenReturn(true);
        lendmentService.getDatatablesData(request);

        verify(lendmentRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    public void getDatatablesData_sortAndSearchByColumn() {

        MockHttpServletRequest servletRequest = this.mock_datatableServletRequest(true);
        DataTablesRequest request = new DataTablesRequest(servletRequest);

        Page<Lendment> page = new PageImpl<>(new ArrayList<>());
        when(lendmentRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        lendmentService.getDatatablesData(request);

        verify(lendmentRepository, times(1))
                .findAll(any(Specification.class), any(PageRequest.class));

    }





}
