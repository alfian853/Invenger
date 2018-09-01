package com.bliblifuture.Invenger.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin")
public class Admin implements Serializable {

    @Id
    Integer Id;

    @Column(unique = true)
    String name;

    @Column(name = "password",length = 72)
    String password;

}
