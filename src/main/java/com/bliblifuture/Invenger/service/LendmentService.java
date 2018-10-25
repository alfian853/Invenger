package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.model.Inventory;
import com.bliblifuture.Invenger.model.Lendment;
import com.bliblifuture.Invenger.model.LendmentDetails;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.repository.InventoryRepository;
import com.bliblifuture.Invenger.repository.LendmentRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import com.bliblifuture.Invenger.request.jsonRequest.LendmentCreateRequest;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class LendmentService {

    @Autowired
    LendmentRepository lendmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    public RequestResponse createLendment(LendmentCreateRequest request){
        RequestResponse response = new RequestResponse();
        try{
            Lendment lendment = new Lendment();
            lendment.setUser( userRepository.getOne(request.getUserId()) );

            List<LendmentDetails> detailsList = new LinkedList<>();

            int itemSize = request.getItems().size();
            for(int i = 0;i < itemSize; ++i){
                Inventory inventory = inventoryRepository.findInventoryById( request.getChildIdAt(i) );
                if(request.getChildQuantityAt(i) > inventory.getQuantity()){
                    throw new Exception();
                }
                detailsList.add(
                  LendmentDetails.builder()
                          .inventory(
                            inventory
                          )
                          .lendment(lendment)
                          .quantity(request.getChildQuantityAt(i))
                          .build()
                );
            }

            lendment.setLendment_details(detailsList);

            lendmentRepository.save(lendment);
            response.setStatusToSuccess();
            return response;
        }
        catch (Exception e){
            e.printStackTrace();
            response.setStatusToFailed();
            return response;
        }


    }



}
