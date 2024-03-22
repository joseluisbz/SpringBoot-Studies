package org.bz.app.mspeople.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2826599699776225415L;

    private Long id;

    private Long number;

    private Integer cityCode;

    private Integer countryCode;

    @JsonBackReference
    private UserDTO user;
}
