package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.response.jsonResponse.DataTablesResult;

public interface DatatablesService<RESPONSE extends DataTablesResult> {
    RESPONSE getDatatablesData(DataTablesRequest request);
}
