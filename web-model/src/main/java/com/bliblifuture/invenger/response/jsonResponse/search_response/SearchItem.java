package com.bliblifuture.invenger.response.jsonResponse.search_response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchItem {
    protected Integer id;
    protected String text;
}
