package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.ModelMapper.lendment.LendmentMapper;
import com.bliblifuture.Invenger.exception.DataNotFoundException;
import com.bliblifuture.Invenger.exception.InvalidRequestException;
import com.bliblifuture.Invenger.model.inventory.Inventory;
import com.bliblifuture.Invenger.model.lendment.Lendment;
import com.bliblifuture.Invenger.model.lendment.LendmentDetail;
import com.bliblifuture.Invenger.model.lendment.LendmentDetailIdentity;
import com.bliblifuture.Invenger.model.lendment.LendmentStatus;
import com.bliblifuture.Invenger.repository.InventoryRepository;
import com.bliblifuture.Invenger.repository.LendmentDetailRepository;
import com.bliblifuture.Invenger.repository.LendmentRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import com.bliblifuture.Invenger.request.jsonRequest.LendmentCreateRequest;
import com.bliblifuture.Invenger.request.jsonRequest.LendmentReturnRequest;
import com.bliblifuture.Invenger.response.jsonResponse.HandOverResponse;
import com.bliblifuture.Invenger.response.jsonResponse.LendmentReturnResponse;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.Invenger.response.viewDto.LendmentDTO;
import com.bliblifuture.Invenger.response.viewDto.LendmentDetailDTO;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LendmentService {

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

    private final LendmentMapper mapper = Mappers.getMapper(LendmentMapper.class);


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse createLendment(LendmentCreateRequest request) throws Exception {
        RequestResponse response = new RequestResponse();
        Lendment lendment = Lendment
                .builder()
                .user( userRepository.getOne(request.getUserId()) )
                .status(LendmentStatus.WaitingForApproval.getDesc())
                .notReturnedCount(request.getItems().size())
                .build();

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
        return mapper.toLendmentDtoList(lendmentRepository.findAll());
    }

    public List<LendmentDTO> getAllByUser(){
        return mapper.toLendmentDtoList(lendmentRepository.findAllByUserId(userService.getSessionUser().getId()) );
    }

    public LendmentDTO getById(Integer id) throws Exception {
        Lendment lendment = lendmentRepository.findLendmentById(id);
        if(lendment == null){
            throw new DataNotFoundException("Lendment Not Found");
        }
        return mapper.toLendmentDTO(lendment);
    }

    public List<LendmentDetailDTO> getLendmentDetailById(Integer id) throws Exception {
        List<LendmentDetail> details = lendmentDetailRepository.findAllByLendmentId(id);
        if(details.size() == 0){
            throw new DataNotFoundException("Lendment not Found");
        }
        return mapper.toLendmentDetailDtoList(details);
    }

    public List<LendmentDTO> getAllLendmentRequest(){

        return mapper.toLendmentDtoList(
                lendmentRepository.findAllBySuperiorIdAndStatus(
                        userService.getSessionUser().getId(),
                        LendmentStatus.WaitingForApproval.getDesc()
                )
        );

    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse returnInventory(LendmentReturnRequest request) throws Exception{

        LendmentReturnResponse response = new LendmentReturnResponse();

        LocalDateTime localTime = LocalDateTime.now(ZoneId.of("GMT+7"));
        Date date = Date.from(localTime.atZone(ZoneId.systemDefault()).toInstant());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Lendment lendment = lendmentRepository.findLendmentById(request.getLendmentId());
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
                throw new Exception();
            }
            detail.setReturnDate(date);
            detail.setReturned(true);
            lendmentDetailRepository.save(detail);
        }
        response.setReturnDate(format.format(date));
        response.setStatusToSuccess();
        return response;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse approveLendmentRequest(Integer id) throws Exception {
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
    public HandOverResponse handOverOrderItems(Integer id) throws Exception {
        Lendment lendment = lendmentRepository.findLendmentById(id);
        if(!lendment.getStatus().equals(LendmentStatus.WaitingForPickUp.getDesc())){
//            throw new Exception();
        }

        lendment.setStatus(LendmentStatus.InLending.getDesc());
        lendmentRepository.save(lendment);

        HandOverResponse response = new HandOverResponse();
        response.setStatusToSuccess();
        response.setLendmentStatus(LendmentStatus.InLending.getDesc());

        if(lendment.getId().equals(id)){
            throw new DataNotFoundException("huhuhuhuhu");
        }

        return response;

    }
}
