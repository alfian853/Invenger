package com.bliblifuture.invenger.Utils;

import com.bliblifuture.invenger.ModelMapper.CriteriaPathMapper;
import com.bliblifuture.invenger.request.datatables.DataTablesColumnSpecs;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Data
public class DataTablesUtils<T> {

    private CriteriaPathMapper criteriaPathMapper;

    public DataTablesUtils(CriteriaPathMapper criteriaPathMapper){
        this.criteriaPathMapper = criteriaPathMapper;
    }

    public QuerySpec<T> getQuerySpec(DataTablesRequest request, CustomPredicate...customPredicates){

        QuerySpec<T> querySpec = new QuerySpec<>();

        int pageNum = request.getStart()/request.getLength();
        PageRequest pageRequest = PageRequest.of(
                pageNum
                ,request.getLength()
        );
        Sort sort = null;
        boolean isSearching = false;
        for(DataTablesColumnSpecs specs : request.getColumns()){
            if(specs.isSortable()){
                sort = new Sort(Sort.Direction.valueOf(specs.getSortDir().toUpperCase()), specs.getName());
            }
            if(specs.isSearchable() && !"".equals(specs.getSearch())){
                isSearching = true;
            }

        }
        if(sort!=null){
            pageRequest = PageRequest.of(pageNum,request.getLength(),sort);
        }

        querySpec.setPageRequest(pageRequest);

        if(isSearching){
            Specification<T> specification = (root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                for(DataTablesColumnSpecs spec : request.getColumns()){
                    if(spec.isSearchable() && !"".equals(spec.getSearch())){
                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(criteriaPathMapper.getPathFrom(root,spec.getName()).as(String.class)),
                                        "%"+spec.getSearch().toLowerCase()+"%")
                        );
                    }
                }

                for(int i=0;i<customPredicates.length;i++){
                    predicates.add(customPredicates[i].getPredicate(root,criteriaQuery,criteriaBuilder));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            querySpec.setSpecification(specification);
        }
        else if(customPredicates.length > 0){
            Specification<T> specification = (root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                for(int i=0;i<customPredicates.length;i++){
                    predicates.add(customPredicates[i].getPredicate(root,criteriaQuery,criteriaBuilder));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            querySpec.setSpecification(specification);
        }

        return querySpec;
    }


}
