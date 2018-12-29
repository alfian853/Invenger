package com.bliblifuture.invenger.service.impl;

import com.bliblifuture.invenger.ModelMapper.lendment.LendmentMapper;
import com.bliblifuture.invenger.Utils.DataTablesUtils;
import com.bliblifuture.invenger.Utils.QuerySpec;
import com.bliblifuture.invenger.entity.inventory.Inventory;
import com.bliblifuture.invenger.entity.lendment.Lendment;
import com.bliblifuture.invenger.entity.lendment.LendmentDetail;
import com.bliblifuture.invenger.entity.lendment.LendmentDetailIdentity;
import com.bliblifuture.invenger.entity.lendment.LendmentStatus;
import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.repository.InventoryRepository;
import com.bliblifuture.invenger.repository.LendmentDetailRepository;
import com.bliblifuture.invenger.repository.LendmentRepository;
import com.bliblifuture.invenger.repository.UserRepository;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.request.jsonRequest.LendmentCreateRequest;
import com.bliblifuture.invenger.request.jsonRequest.LendmentReturnRequest;
import com.bliblifuture.invenger.response.jsonResponse.*;
import com.bliblifuture.invenger.response.viewDto.LendmentDTO;
import com.bliblifuture.invenger.service.LendmentService;
import com.bliblifuture.invenger.service.UserService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class LendmentServiceImpl implements LendmentService {

    @Autowired
    LendmentRepository lendmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LendmentDetailRepository lendmentDetailRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    UserService userService;


    private DataTablesUtils<Lendment> dataTablesUtils;

    private final LendmentMapper mapper = Mappers.getMapper(LendmentMapper.class);

    public LendmentServiceImpl(){
        dataTablesUtils = new DataTablesUtils<>(mapper);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse createLendment(LendmentCreateRequest request) {
        RequestResponse response = new RequestResponse();
        Lendment lendment;
        if(userService.currentUserIsAdmin()){
            lendment = Lendment
                    .builder()
                    .user( userRepository.getOne(request.getUserId()) )
                    .status(LendmentStatus.WaitingForPickUp.getDesc())
                    .notReturnedCount(request.getItems().size())
                    .build();
        }
        else{
            lendment = Lendment
                    .builder()
                    .user( userRepository.getOne(request.getUserId()) )
                    .status(LendmentStatus.WaitingForApproval.getDesc())
                    .notReturnedCount(request.getItems().size())
                    .build();
        }
        lendmentRepository.save(lendment);

        List<LendmentDetail> detailsList = new LinkedList<>();

        int itemSize = request.getItems().size();
        for(int i = 0;i < itemSize; ++i){
            Inventory inventory = inventoryRepository.findInventoryById( request.getChildIdAt(i) );

            if(request.getChildQuantityAt(i) > inventory.getQuantity()){
                throw new InvalidRequestException(inventory.getName()+" is not Available");
            }

            inventory.setQuantity(inventory.getQuantity() - request.getChildQuantityAt(i) );
            detailsList.add(
              LendmentDetail.builder()
                      .cmpId(new LendmentDetailIdentity(lendment.getId(),inventory.getId()))
                      .inventory(
                        inventory
                      )
                      .lendment(lendment)
                      .quantity(request.getChildQuantityAt(i))
                      .build()
            );
        }

        lendmentDetailRepository.saveAll(detailsList);
        response.setStatusToSuccess();

        return response;

    }

    public List<LendmentDTO> getAll(){
        return mapper.toDtoList(lendmentRepository.findAll(),false);
    }

    public List<LendmentDTO> getAllByUser(){
        return mapper.toDtoList(
                lendmentRepository.findAllByUserId(userService.getSessionUser().getId()),
                false
        );
    }

    public LendmentDTO getLendmentDetailById(Integer id) {
        Lendment lendment = lendmentRepository.findLendmentById(id);

        if(lendment == null){
            throw new DataNotFoundException("Lendment not Found");
        }

        return mapper.toLendmentWithDetailDTO(lendment);
    }

    public List<LendmentDTO> getAllLendmentRequestOfSuperior(){

        return mapper.toDtoList(
                lendmentRepository.findAllBySuperiorIdAndStatus(
                        userService.getSessionUser().getId(),
                        LendmentStatus.WaitingForApproval.getDesc()
                ),false
        );

    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse returnInventory(LendmentReturnRequest request) {

        LendmentReturnResponse response = new LendmentReturnResponse();

        LocalDateTime localTime = LocalDateTime.now(ZoneId.of("GMT+7"));
        Date date = Date.from(localTime.atZone(ZoneId.systemDefault()).toInstant());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Lendment lendment = lendmentRepository.findLendmentById(request.getLendmentId());

        if(!lendment.getStatus().equals(LendmentStatus.InLending.getDesc())){
            throw new InvalidRequestException("Invalid Request!");
        }

        lendment.setNotReturnedCount(lendment.getNotReturnedCount() - request.getInventoriesId().size());
        if(lendment.getNotReturnedCount() <= 0){
            lendment.setStatus(LendmentStatus.Finished.getDesc());
        }

        lendmentRepository.save(lendment);

        for(Integer item_id : request.getInventoriesId()){
            LendmentDetail detail = lendmentDetailRepository.getOne(
                    new LendmentDetailIdentity(request.getLendmentId(), item_id)
            );
            if(detail.isReturned()){
                throw new InvalidRequestException("Item had been returned!");
            }
            detail.setReturnDate(date);
            detail.setReturned(true);
            Inventory inventory = detail.getInventory();
            inventory.setQuantity(inventory.getQuantity() +
                    detail.getQuantity());
            lendmentDetailRepository.save(detail);
        }

        response.setReturnDate(format.format(date));
        response.setStatusToSuccess();
        return response;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse approveLendmentRequest(Integer id) {
        Lendment lendment = lendmentRepository.findLendmentById(id);

        if(lendment == null){
            throw new DataNotFoundException("Lendment Not Found");
        }

        if(!lendment.getStatus().equals(LendmentStatus.WaitingForApproval.getDesc())){
            throw new InvalidRequestException("Lendment had been approved");
        }

        lendment.setStatus(LendmentStatus.WaitingForPickUp.getDesc());
        lendmentRepository.save(lendment);

        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        return response;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public HandOverResponse handOverOrderItems(Integer id) {
        Lendment lendment = lendmentRepository.findLendmentById(id);

        if(lendment == null){
            throw new DataNotFoundException("Lendment Not Found");
        }

        if(!lendment.getStatus().equals(LendmentStatus.WaitingForPickUp.getDesc())){
            throw new InvalidRequestException("Invalid Request!");
        }

        lendment.setStatus(LendmentStatus.InLending.getDesc());
        lendmentRepository.save(lendment);

        HandOverResponse response = new HandOverResponse();
        response.setStatusToSuccess();
        response.setLendmentStatus(LendmentStatus.InLending.getDesc());

        return response;

    }
    public List<LendmentDTO> getInventoryLendment(Integer id){
        return mapper.toDtoList(
                lendmentRepository.findLendmentContainInventory(id),true
        );
    }

    @Override
    public DataTablesResult<LendmentDataTableResponse> getDatatablesData(DataTablesRequest request) {
        QuerySpec<Lendment> spec = dataTablesUtils.getQuerySpec(request);

        Page<Lendment> page;

        DataTablesResult<LendmentDataTableResponse> result = new DataTablesResult<>();

        if(spec.getSpecification() == null){
            page = lendmentRepository.findAll(spec.getPageRequest());
        }
        else{
            page = lendmentRepository.findAll(spec.getSpecification(),spec.getPageRequest());
        }

        result.setListOfDataObjects(mapper.toDataTablesDtoList(page.getContent()));
        result.setDraw(Integer.parseInt(request.getDraw()));
        result.setRecordsFiltered(page.getNumberOfElements());
        result.setRecordsTotal((int) page.getTotalElements());

        return result;
    }
}
