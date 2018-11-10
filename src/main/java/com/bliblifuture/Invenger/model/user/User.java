package com.bliblifuture.Invenger.model.user;

import com.bliblifuture.Invenger.annotation.PhoneConstraint;
import com.bliblifuture.Invenger.model.AuditModel;
import com.bliblifuture.Invenger.model.Position;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends AuditModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String fullName;

    @PhoneConstraint
    private String telp;

    @Column(length = 72,nullable = false)
    private String password;

    private String pictureName = "default-pict.png";

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "position_id",referencedColumnName = "id")
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="superior_id", referencedColumnName = "id")
    private User superior;

    private String role;


    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add( new SimpleGrantedAuthority(getRole()) );
        return grantedAuthorities;
    }

}
