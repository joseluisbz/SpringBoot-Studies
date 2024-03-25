package org.bz.app.mspeople.dtos;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 599213033533076911L;

    private Long id;

    private String name;

    @Pattern(regexp = "[A-Za-z]+@[a-z]+\\.[a-z]+")  // aaaaaaa@dominio.cl (Without Numbers)
    @Email
    private String email;

    @NotEmpty
    private String password;

    @JsonManagedReference
    private Set<PhoneDTO> phones;

    private Date created;

    private Date modified;

    private Date lastLogin;

    private boolean isactive;

    private String token;

    public UserDTO() {
        this.phones = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<PhoneDTO> getPhones() {
        return phones;
    }

    public void setPhones(Set<PhoneDTO> phones) {
        this.phones = phones;
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
