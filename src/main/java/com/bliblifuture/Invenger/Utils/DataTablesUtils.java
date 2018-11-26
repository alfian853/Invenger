package com.bliblifuture.Invenger.Utils;

import com.bliblifuture.Invenger.ModelMapper.CriteriaPathMapper;
import com.bliblifuture.Invenger.request.datatables.DataTablesColumnSpecs;
import com.bliblifuture.Invenger.request.datatables.DataTablesRequest;
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

    public QuerySpec<T> getQuerySpec(DataTablesRequest request){

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
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            querySpec.setSpecification(specification);
        }


        return querySpec;
    }


}
