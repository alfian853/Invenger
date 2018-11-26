package com.bliblifuture.Invenger.Utils;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

public interface PathMapper {
    Path getPathFrom(Root root,String field);
}
