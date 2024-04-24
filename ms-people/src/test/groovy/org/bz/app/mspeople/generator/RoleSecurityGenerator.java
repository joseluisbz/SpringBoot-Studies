package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.dtos.RoleDTO;
import org.bz.app.mspeople.security.entities.AuthoritySecurity;
import org.bz.app.mspeople.security.entities.RoleSecurity;

import java.util.Set;
import java.util.UUID;

public class RoleSecurityGenerator {

    public static RoleSecurity userWithAuthoritiesGenerate() {
        RoleDTO roleDTO = RoleDTOGenerator.userGenerate();
        return RoleSecurity
                .builder()
                .id(UUID.randomUUID())
                .name(roleDTO.getName())
                .authoritySecurities(Set.of(
                        AuthoritySecurityGenerator.readSelfGenerator(),
                        AuthoritySecurityGenerator.editSelfGenerator()
                        )
                )
                .build();
    }

    public static RoleSecurity adminWithAuthoritiesGenerate() {
        RoleDTO roleDTO = RoleDTOGenerator.adminGenerate();
        return RoleSecurity
                .builder()
                .id(UUID.randomUUID())
                .name(roleDTO.getName())
                .authoritySecurities(Set.of(
                        AuthoritySecurityGenerator.readAllGenerator(),
                        AuthoritySecurityGenerator.editAllGenerator(),
                        AuthoritySecurityGenerator.deleteAllGenerator(),
                        AuthoritySecurityGenerator.readSelfGenerator(),
                        AuthoritySecurityGenerator.editSelfGenerator()
                        )
                )
                .build();
    }
}
