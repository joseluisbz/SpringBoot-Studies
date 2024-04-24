package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.dtos.AuthorityDTO;
import org.bz.app.mspeople.dtos.RoleDTO;
import org.bz.app.mspeople.security.entities.AuthoritySecurity;
import org.bz.app.mspeople.security.entities.RoleSecurity;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoleDTOGenerator {
    public static RoleDTO userGenerate() {
        return RoleDTO
                .builder()
                .name("USER")
                .build();
    }

    public static RoleDTO adminGenerate() {
        return RoleDTO
                .builder()
                .name("ADMIN")
                .build();
    }

    public static RoleDTO userWithAuthoritiesGenerate() {
        RoleDTO roleDTO = userGenerate();
        RoleSecurity roleSecurity = RoleSecurityGenerator.userWithAuthoritiesGenerate();
        return RoleDTO
                .builder()
                .id(UUID.randomUUID())
                .name(roleDTO.getName())
                .authorities(maps(roleSecurity.getAuthoritySecurities()))
                .build();
    }

    public static RoleDTO adminWithAuthoritiesGenerate() {
        RoleDTO roleDTO = adminGenerate();
        RoleSecurity roleSecurity = RoleSecurityGenerator.adminWithAuthoritiesGenerate();
        return RoleDTO
                .builder()
                .id(UUID.randomUUID())
                .name(roleDTO.getName())
                .authorities(maps(roleSecurity.getAuthoritySecurities()))
                .build();
    }

    private static Set<AuthorityDTO> maps(Set<AuthoritySecurity> authoritySecurities) {
        return authoritySecurities
                .stream()
                .map(AuthorityDTOGenerator::maps)
                .collect(Collectors.toSet());
    }
}
