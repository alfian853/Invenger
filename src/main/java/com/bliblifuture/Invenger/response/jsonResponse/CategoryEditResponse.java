package com.bliblifuture.Invenger.response;

import com.bliblifuture.Invenger.model.Category;
import lombok.Data;

import java.util.List;

@Data
public class CategoryEditResponse extends RequestResponse {
    List<Category> categories;
}
