package com.bliblifuture.invenger.entity.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "position_generator")
    @SequenceGenerator(name="position_generator", sequenceName = "position_seq")
    Integer id;

    @Column(unique = true,nullable = false)
    String name;

    @Column(nullable = false)
    Integer level;
}
