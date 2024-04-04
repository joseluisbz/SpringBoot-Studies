package org.bz.app.mspeople.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "USER_ENTITIES")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class UserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 264879308518981680L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userEntity", orphanRemoval = true)
    private Set<PhoneEntity> phoneEntities;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    private boolean isactive;

    private String token;

    @PrePersist
    public void prePersist() {
        this.created = new Date();
        this.lastLogin = new Date();
    }

    public UserEntity() {
        this.phoneEntities = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PhoneEntity> getPhoneEntities() {
        return phoneEntities;
    }

    public void setPhoneEntities(Set<PhoneEntity> phoneEntities) {
        this.phoneEntities = phoneEntities;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isIsactive() {
        return isactive;
    }

    public void setIsactive(boolean isactive) {
        this.isactive = isactive;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
