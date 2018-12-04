package com.bliblifuture.invenger.response.jsonResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertResponse {
    String title;
    String status;
    String message;
}
