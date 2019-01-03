package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.request.jsonRequest.LendmentCreateRequest;
import com.bliblifuture.invenger.request.jsonRequest.LendmentReturnRequest;
import com.bliblifuture.invenger.response.jsonResponse.DataTablesResult;
import com.bliblifuture.invenger.response.jsonResponse.HandOverResponse;
import com.bliblifuture.invenger.response.jsonResponse.LendmentDataTableResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.viewDto.LendmentDTO;

import java.util.List;

public interface LendmentService extends
        DatatablesService< DataTablesResult<LendmentDataTableResponse> >
    {

    RequestResponse createLendment(LendmentCreateRequest request);
    List<LendmentDTO> getAll();
    List<LendmentDTO> getAllByUser();
    LendmentDTO getLendmentDetailById(Integer id);
    List<LendmentDTO> getAllLendmentRequestOfSuperior();
    RequestResponse returnInventory(LendmentReturnRequest request);
    RequestResponse assignLendmentRequest(Integer id, boolean isApprove);
    HandOverResponse handOverOrderItems(Integer id);
    List<LendmentDTO> getInventoryLendment(Integer id);
}
