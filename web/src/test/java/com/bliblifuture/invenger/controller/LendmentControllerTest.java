package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.ModelMapper.lendment.LendmentMapper;
import com.bliblifuture.invenger.entity.lendment.Lendment;
import com.bliblifuture.invenger.entity.lendment.LendmentStatus;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.request.jsonRequest.LendmentCreateRequest;
import com.bliblifuture.invenger.response.jsonResponse.DataTablesResult;
import com.bliblifuture.invenger.response.jsonResponse.HandOverResponse;
import com.bliblifuture.invenger.response.jsonResponse.LendmentDataTableResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.viewDto.LendmentDTO;
import com.bliblifuture.invenger.response.viewDto.ProfileDTO;
import com.bliblifuture.invenger.service.AccountService;
import com.bliblifuture.invenger.service.LendmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class LendmentControllerTest {

    private MockMvc mvc;

    @InjectMocks
    LendmentController lendmentController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    LendmentService lendmentService;

    @Mock
    AccountService userService;

    @Before
    public void init() {
        mvc = MockMvcBuilders.standaloneSetup(lendmentController)
                .setControllerAdvice(new DefaultControllerAdvice())
                .build();
    }

      ////////////////////////////////////////////////
     //public String getAssignItemForm(Model model)//
    ////////////////////////////////////////////////

    @Test
    public void getAssignItemForm_userIsAdmin() throws Exception {
        when(userService.currentUserIsAdmin()).thenReturn(true);

        mvc.perform(get("/lendment/create")
        ).andExpect(status().isOk())
                .andExpect(view().name("lendment/lendment_create"));
    }

    @Test
    public void getAssignItemForm_userIsNotAdmin() throws Exception {
        when(userService.currentUserIsAdmin()).thenReturn(false);

        ProfileDTO user = ProfileDTO.builder().id(1).build();
        when(userService.getProfile()).thenReturn(user);

        mvc.perform(get("/lendment/create")
        ).andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(view().name("lendment/lendment_create_basic"));
    }


      ///////////////////////////////////////////////
     //public String getLendmentTable(Model model)//
    ///////////////////////////////////////////////

    @Test
    public void testGetLendmentTable_userIsAdmin() throws Exception {
        when(userService.currentUserIsAdmin()).thenReturn(true);

        List<LendmentDTO> lendments = new ArrayList<>();
        lendments.add(LendmentDTO.builder().id(1).build());

        when(lendmentService.getAll()).thenReturn(lendments);

        mvc.perform(get("/lendment/all")
        ).andExpect(status().isOk())
                .andExpect(model().attribute("lendments", lendments))
                .andExpect(view().name("lendment/lendment_list_admin"));

    }

    @Test
    public void testGetLendmentTable_userIsNotAdmin() throws Exception {
        when(userService.currentUserIsAdmin()).thenReturn(false);

        List<LendmentDTO> lendments = new ArrayList<>();
        lendments.add(LendmentDTO.builder().id(1).build());

        when(lendmentService.getAllByUser()).thenReturn(lendments);

        mvc.perform(get("/lendment/all")
        ).andExpect(status().isOk())
                .andExpect(model().attribute("lendments", lendments))
                .andExpect(view().name("lendment/lendment_list_basic"));

    }

      ////////////////////////////////////////////////////
     //public String getLendmentQueueTable(Model model)//
    ////////////////////////////////////////////////////

    @Test
    public void getLendmentQueueTable_test() throws Exception {
        List<LendmentDTO> lendments = new ArrayList<>();
        lendments.add(LendmentDTO.builder().id(1).build());

        when(lendmentService.getAllLendmentRequestOfSuperior()).thenReturn(lendments);

        mvc.perform(get("/lendment/requests")
        ).andExpect(status().isOk())
                .andExpect(model().attribute("lendments", lendments))
                .andExpect(view().name("lendment/lendment_request_list"));
    }

      ////////////////////////////////////////////////////////////////////////////////
     //public RequestResponse doApprovement(@PathVariable("id") Integer lendmentId)//
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void doApprovement_test() throws Exception {
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        when(lendmentService.assignLendmentRequest(1,true)).thenReturn(response);

        mvc.perform(post("/lendment/approve/{id}", 1)
                .param("approve","true")
        ).andExpect(status().isOk());
    }

      /////////////////////////////////////////////////////////////////////////////
     //public RequestResponse doHandOver(@PathVariable("id") Integer lendmentId)//
    /////////////////////////////////////////////////////////////////////////////

    @Test
    public void doHandOver_test() throws Exception {
        HandOverResponse response = new HandOverResponse();
        response.setLendmentStatus(LendmentStatus.InLending.toString());
        response.setStatusToSuccess();

        when(lendmentService.handOverOrderItems(1)).thenReturn(response);

        mvc.perform(post("/lendment/handover/{id}", 1)
        ).andExpect(status().isOk());
    }

      /////////////////////////////////////////////////////////////////////////////////////////////////
     //public String trackInventory(Model model,@RequestParam("having-item-id") Integer inventoryId)//
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void trackInventory_test() throws Exception {
        List<LendmentDTO.Detail> details = new ArrayList<>();
        details.add(LendmentDTO.Detail.builder().inventoryId(1).build());
        List<LendmentDTO> lendments = new ArrayList<>();
        lendments.add(LendmentDTO.builder().id(1).details(details).build());

        when(lendmentService.getInventoryLendment(1)).thenReturn(lendments);

        System.out.println(lendmentService.getInventoryLendment(1));

        mvc.perform(get("/lendment")
                .param("having-item-id","1")
        ).andExpect(status().isOk())
                .andExpect(model().attribute("lendments", lendments))
                .andExpect(view().name("lendment/lendment_inventory"));

    }

      ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
     //public DataTablesResult<LendmentDataTableResponse> getPaginatedLendments(HttpServletRequest servletRequest)//
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    public void getPaginatedLendments_test() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = this.mock_datatableServletRequest(true);
        DataTablesRequest request = new DataTablesRequest(mockHttpServletRequest);

        LendmentMapper mapper = Mappers.getMapper(LendmentMapper.class);

        Page<Lendment> page = new PageImpl<>(new ArrayList<>());

        DataTablesResult<LendmentDataTableResponse> dataTablesResult = new DataTablesResult<>();
        dataTablesResult.setListOfDataObjects(mapper.toDataTablesDtoList(page.getContent()));
        dataTablesResult.setDraw(Integer.parseInt(request.getDraw()));
        dataTablesResult.setRecordsFiltered(page.getNumberOfElements());
        dataTablesResult.setRecordsTotal((int) page.getTotalElements());

        mvc.perform(get("/lendment/datatables")
        ).andExpect(status().isOk());
    }

      /////////////////////////////////////////////////////////////////////////
     //public String getLendment(@PathVariable("id") Integer id,Model model)//
    /////////////////////////////////////////////////////////////////////////

    @Test
    public void getLendment_userIsAdminNotInLending() throws Exception {
        LendmentDTO lendment = LendmentDTO.builder().id(1).status(LendmentStatus.Finished.getDesc()).build();

        when(lendmentService.getLendmentDetailById(1)).thenReturn(lendment);

        when(userService.currentUserIsAdmin()).thenReturn(true);

        mvc.perform(get("/lendment/detail/{id}",1)
        ).andExpect(status().isOk())
                .andExpect(model().attribute("lendment", lendment))
                .andExpect(view().name("lendment/lendment_detail_basic"));
    }

    @Test
    public void getLendment_userIsAdminInLending() throws Exception {
        LendmentDTO lendment = LendmentDTO.builder().id(1).status(LendmentStatus.InLending.getDesc()).build();

        when(lendmentService.getLendmentDetailById(1)).thenReturn(lendment);

        when(userService.currentUserIsAdmin()).thenReturn(true);

        mvc.perform(get("/lendment/detail/{id}",1)
        ).andExpect(status().isOk())
                .andExpect(model().attribute("lendment", lendment))
                .andExpect(view().name("lendment/lendment_detail_admin"));
    }

    @Test
    public void getLendment_userIsNotAdmin() throws Exception {
        LendmentDTO lendment = LendmentDTO.builder().id(1).build();

        when(lendmentService.getLendmentDetailById(1)).thenReturn(lendment);

        when(userService.currentUserIsAdmin()).thenReturn(false);

        mvc.perform(get("/lendment/detail/{id}",1)
        ).andExpect(status().isOk())
                .andExpect(model().attribute("lendment", lendment))
                .andExpect(view().name("lendment/lendment_detail_basic"));
    }

      //////////////////////////////////////////////////////////////////////////////////////////////
     //public RequestResponse assignItemToUser(@Valid @RequestBody LendmentCreateRequest request)//
    //////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void assignItemToUser_test() throws Exception {

        HashMap<String,Object> content = new HashMap<>();
        content.put("user_id",1);

        content.put("items",Arrays.asList(
                LendmentCreateRequest.Item.builder().id(1).quantity(2).build(),
                LendmentCreateRequest.Item.builder().id(2).quantity(2).build()
        ));

        mvc.perform(post("/lendment/create").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(content))).andExpect(status().isOk());

    }

      /////////////////////////////////////////////////////////////////////////////////////////////
     //public RequestResponse returnInventory(@Valid @RequestBody LendmentReturnRequest request)//
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testReturnInventory_test() throws Exception {
        HashMap<String,Object> content = new HashMap<>();
        content.put("lendment_id","1");
        content.put("inventories_id",Arrays.asList(1,2,3));
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();
        when(lendmentService.returnInventory(any())).thenReturn(response);

        mvc.perform(post("/lendment/return").contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsBytes(content))).andExpect(status().isOk());
    }


}