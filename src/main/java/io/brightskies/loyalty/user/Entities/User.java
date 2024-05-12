package io.brightskies.loyalty.user.Entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "users")
@ToString
public class User {
    @Id
    @GeneratedValue
    private   Long id;
    private String name;

    @Column(unique = true)
    private String email;

    @Column()
    private String password;





    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles ;//new ArrayList<Role>(Arrays.asList(new Role("ROLE_USER")));








    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User() {

    }

    public void addRole(Role role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(role);
    }

    public Collection<? extends GrantedAuthority> mapRolesToAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

    }



}
