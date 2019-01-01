package com.bliblifuture.invenger.Utils;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@Data
public class QuerySpec<T> {

    public Specification<T> getSpecification() {
        return specification;
    }

    public void setSpecification(Specification<T> specification) {
        this.specification = specification;
    }

    private Specification<T> specification;
    private PageRequest pageRequest;


}
