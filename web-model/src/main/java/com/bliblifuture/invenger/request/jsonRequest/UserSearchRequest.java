package com.bliblifuture.invenger.request.jsonRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchRequest extends SearchRequest{
    Integer minLevel;
}
