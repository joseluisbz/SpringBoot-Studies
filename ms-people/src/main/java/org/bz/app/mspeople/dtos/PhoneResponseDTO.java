package org.bz.app.mspeople.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PhoneResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2826599699776225415L;

    @EqualsAndHashCode.Exclude
    private UUID id;

    private Long number;

    private Integer cityCode;

    private Integer countryCode;

    @JsonBackReference
    @EqualsAndHashCode.Exclude
    private UserResponseDTO user;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public Integer getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Integer countryCode) {
        this.countryCode = countryCode;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }
}
