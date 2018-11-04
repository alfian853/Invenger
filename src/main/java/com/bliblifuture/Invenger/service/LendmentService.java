package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.ModelMapper.lendment.LendmentMapper;
import com.bliblifuture.Invenger.model.inventory.Inventory;
import com.bliblifuture.Invenger.model.lendment.Lendment;
import com.bliblifuture.Invenger.model.lendment.LendmentDetail;
import com.bliblifuture.Invenger.model.lendment.LendmentDetailIdentity;
import com.bliblifuture.Invenger.repository.InventoryRepository;
import com.bliblifuture.Invenger.repository.LendmentDetailRepository;
import com.bliblifuture.Invenger.repository.LendmentRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import com.bliblifuture.Invenger.request.jsonRequest.LendmentCreateRequest;
import com.bliblifuture.Invenger.request.jsonRequest.LendmentReturnRequest;
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

    private final LendmentMapper mapper = Mappers.getMapper(LendmentMapper.class);


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse createLendment(LendmentCreateRequest request) throws Exception {
        RequestResponse response = new RequestResponse();
        Lendment lendment = new Lendment();
        lendment.setUser( userRepository.getOne(request.getUserId()) );
        lendmentRepository.save(lendment);

        List<LendmentDetail> detailsList = new LinkedList<>();

        int itemSize = request.getItems().size();
        for(int i = 0;i < itemSize; ++i){
            Inventory inventory = inventoryRepository.findInventoryById( request.getChildIdAt(i) );
            if(request.getChildQuantityAt(i) > inventory.getQuantity()){
                throw new Exception();
            }
            System.out.println(lendment.getId()+" "+inventory.getId());
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

    public LendmentDTO getById(Integer id){
        return mapper.toLendmentDTO(lendmentRepository.findById(id).get());
    }

    public List<LendmentDetailDTO> getLendmentDetailById(Integer id){

        return mapper.toLendmentDetailDtoList(
                lendmentDetailRepository.findAllByLendmentId(id)
        );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse returnInventory(LendmentReturnRequest request) throws Exception{

        LendmentReturnResponse response = new LendmentReturnResponse();

        LocalDateTime localTime = LocalDateTime.now(ZoneId.of("GMT+7"));
        Date date = Date.from(localTime.atZone(ZoneId.systemDefault()).toInstant());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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


}
