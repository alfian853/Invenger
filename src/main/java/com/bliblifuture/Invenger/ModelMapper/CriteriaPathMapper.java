package com.bliblifuture.Invenger.ModelMapper;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

public interface CriteriaPathMapper {
    Path getPathFrom(Root root,String field);
}
