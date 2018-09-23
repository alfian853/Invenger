package com.bliblifuture.Invenger.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String name;

    @JsonBackReference
    @Getter(AccessLevel.NONE)
    @JoinColumn(name="parent_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    Category parent;

    @Override
    public String toString(){
        return "{id:"+id+",name:"+name+"}";
    }
}
