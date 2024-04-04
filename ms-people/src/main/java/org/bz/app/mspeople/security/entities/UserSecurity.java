package org.bz.app.mspeople.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bz.app.mspeople.entities.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USER_SECURITIES")
public class UserSecurity extends UserEntity implements UserDetails {

    @Serial
    private static final long serialVersionUID = -1540315403099766750L;

    @Column(nullable = false, unique = true)
    private String username;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    private boolean enabled = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "id")
    private RoleSecurity role;

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RoleSecurity getRole() {
        return role;
    }

    public void setRole(RoleSecurity role) {
        this.role = role;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return role.getSecurityAuthorities()
                .stream()
                .map(s -> (GrantedAuthority) s)
                .collect(Collectors.toSet());
    }
}
