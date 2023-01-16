package com.alex.security.user;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // provee todos los setter/getter, constructores, tostrings, etc
@Builder // construir mi obj facilmente con el Builder Pattern - requiere las sig 2
         // annotations
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user") // postgresql tiene 1 tabla llamada users, asi q hay q renombrala
public class Usuario implements UserDetails {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincremental
    @GeneratedValue // hibernate escogera la mejor opcion para la DB q estemos utilizando
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    // 1 user solo puede tener 1 role
    @Enumerated(EnumType.STRING) // todo enum necesita esta @A. x defaul es ordinal (numbers)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name())); // role.name() propio del enum
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
