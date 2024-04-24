package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.dtos.AuthorityDTO;
import org.bz.app.mspeople.security.entities.AuthoritySecurity;

public class AuthorityDTOGenerator {
    /*
    public static AuthorityDTO readAllGenerator() {
        return maps(AuthoritySecurityGenerator.readAllGenerator());
    }

    public static AuthorityDTO editAllGenerator() {
        return maps(AuthoritySecurityGenerator.editAllGenerator());
    }

    public static AuthorityDTO deleteAllGenerator() {
        return maps(AuthoritySecurityGenerator.deleteAllGenerator());
    }

    public static AuthorityDTO readSelfGenerator() {
        return maps(AuthoritySecurityGenerator.readSelfGenerator());
    }

    public static AuthorityDTO editSelfGenerator() {
        return maps(AuthoritySecurityGenerator.editSelfGenerator());
    }
*/
    public static AuthorityDTO maps(AuthoritySecurity authoritySecurity) {
        return AuthorityDTO
                .builder()
                .id(authoritySecurity.getId())
                .authority(authoritySecurity.getAuthority())
                .build();
    }
}
