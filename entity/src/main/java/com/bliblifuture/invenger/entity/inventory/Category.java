package com.bliblifuture.invenger.entity.inventory;


import com.bliblifuture.invenger.PostgreArrayType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SqlResultSetMapping(
        name = "MyMapping",
        classes = @ConstructorResult(
                targetClass = CategoryWithChildId.class,
                columns = {
                        @ColumnResult(name = "id"),
                        @ColumnResult(name = "name"),
                        @ColumnResult(name = "parent_id"),
                        @ColumnResult(name = "childsId", type = PostgreArrayType.class)
                }

        )
)
@Entity
@Table(name = "categories")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_generator")
    @SequenceGenerator(name="category_generator", sequenceName = "category_seq")
    Integer id;

    @Column(unique = true)
    String name;

    @JsonBackReference
    @JoinColumn(name="parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Category parent;

    @Override
    public String toString(){
        return "{id:"+id+",name:"+name+"}";
    }
}
