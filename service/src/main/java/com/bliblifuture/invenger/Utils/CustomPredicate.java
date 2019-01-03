package com.bliblifuture.invenger.Utils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;

@FunctionalInterface
public interface CustomPredicate {
    Predicate getPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder);
}
