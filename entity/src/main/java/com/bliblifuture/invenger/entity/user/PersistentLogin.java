package com.bliblifuture.invenger.entity.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "persistent_logins")
public class PersistentLogin {
    @Id
    @Column(length = 64)
    String series;

    @Column(nullable = false,length = 64)
    String username;

    @Column(nullable = false,length = 64)
    String token;

    @Column(name = "last_used", nullable = false)
    Date lastUsed;
}
