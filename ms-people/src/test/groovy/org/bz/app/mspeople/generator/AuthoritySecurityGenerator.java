package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.security.entities.AuthoritySecurity;

import java.util.UUID;

public class AuthoritySecurityGenerator {

    public static AuthoritySecurity readAllGenerator() {
        return AuthoritySecurity
                .builder()
                .id(UUID.randomUUID())
                .authority("READ_ALL")
                .build();
    }

    public static AuthoritySecurity editAllGenerator() {
        return AuthoritySecurity
                .builder()
                .id(UUID.randomUUID())
                .authority("EDIT_ALL")
                .build();
    }

    public static AuthoritySecurity deleteAllGenerator() {
        return AuthoritySecurity
                .builder()
                .id(UUID.randomUUID())
                .authority("DELETE_ALL")
                .build();
    }

    public static AuthoritySecurity readSelfGenerator() {
        return AuthoritySecurity
                .builder()
                .id(UUID.randomUUID())
                .authority("READ_SELF")
                .build();
    }

    public static AuthoritySecurity editSelfGenerator() {
        return AuthoritySecurity
                .builder()
                .id(UUID.randomUUID())
                .authority("EDIT_SELF")
                .build();
    }
}
