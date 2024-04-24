package org.bz.app.mspeople.security.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Set;
import java.util.UUID;

@Builder
@ToString(exclude = {"roleSecurities"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "AUTHORITY_SECURITIES")
public class AuthoritySecurity implements GrantedAuthority {

    @Serial
    private static final long serialVersionUID = 1425681467463792708L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 16)
    private String authority;

    @JsonBackReference
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "ROLE_AUTHORITY_MAPPING", joinColumns = @JoinColumn(name = "authority_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleSecurity> roleSecurities;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Set<RoleSecurity> getRoleSecurities() {
        return roleSecurities;
    }

    public void setRoleSecurities(Set<RoleSecurity> roleSecurities) {
        this.roleSecurities = roleSecurities;
    }
}
