package com.bliblifuture.Invenger.response.jsonResponse;

import com.bliblifuture.Invenger.model.Category;
import lombok.Data;

import java.util.List;

@Data
public class CategoryEditResponse extends RequestResponse {
    List<Category> categories;
}
