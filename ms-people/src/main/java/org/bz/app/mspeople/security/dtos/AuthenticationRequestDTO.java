package org.bz.app.mspeople.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5757759477622470172L;

    private String username;

    private String password;
}
