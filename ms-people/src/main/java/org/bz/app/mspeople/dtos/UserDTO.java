package org.bz.app.mspeople.dtos;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
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

}
