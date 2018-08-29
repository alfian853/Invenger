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
@Table(name = "users")
public class User extends AuditModel{

    @Id
    Integer id;

    String name,email;

    @Column(columnDefinition = "numeric")
    String telp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id",nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="superior", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    User superior;

    //debugging purpose
    @Override
    public String toString(){
        return "{["+name+"],"+"["+getSuperior().getName()+"]}";
    }

}
