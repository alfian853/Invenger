package com.bliblifuture.Invenger.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String name;
    Integer child_count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    Category parent;

}
