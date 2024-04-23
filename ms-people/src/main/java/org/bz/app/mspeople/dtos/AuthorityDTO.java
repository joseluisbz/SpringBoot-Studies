package org.bz.app.mspeople.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3555198096660243462L;

    private UUID id;

    @NotEmpty
    @Size(max = 16)
    private String authority;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
