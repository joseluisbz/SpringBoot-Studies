
Hibernate:
    drop table if exists authority_securities cascade
Hibernate:
    drop table if exists phone_entities cascade
Hibernate:
    drop table if exists role_authority_mapping cascade
Hibernate:
    drop table if exists role_securities cascade
Hibernate:
    drop table if exists user_entities cascade
Hibernate:
    drop table if exists user_securities cascade
Hibernate:
    create table authority_securities (
        authority varchar(16) not null unique,
        id uuid not null,
        primary key (id)
    )
Hibernate:
    create table phone_entities (
        city_code integer,
        country_code integer,
        number bigint,
        id uuid not null,
        user_id uuid not null,
        primary key (id),
        unique (country_code, city_code, number)
    )
Hibernate:
    create table role_authority_mapping (
        authority_id uuid not null,
        role_id uuid not null,
        primary key (authority_id, role_id)
    )
Hibernate:
    create table role_securities (
        id uuid not null,
        name varchar(16) not null unique,
        primary key (id)
    )
Hibernate:
    create table user_entities (
        isactive boolean not null,
        created timestamp(6),
        last_login timestamp(6),
        modified timestamp(6),
        id uuid not null,
        username varchar(24) not null unique,
        token varchar(1024),
        email varchar(255) not null unique,
        name varchar(255),
        password varchar(255) not null,
        primary key (id)
    )
Hibernate:
    create table user_securities (
        account_non_expired boolean not null,
        account_non_locked boolean not null,
        credentials_non_expired boolean not null,
        enabled boolean not null,
        id uuid not null,
        role_id uuid not null,
        username varchar(24) not null unique,
        email varchar(255) not null unique,
        password varchar(255) not null,
        primary key (id)
    )
Hibernate:
    alter table phone_entities
       add constraint FKptljul84lcel8q8wcm9nnoosi
       foreign key (user_id)
       references user_entities
Hibernate:
    alter table role_authority_mapping
       add constraint FK55sxwpj6bis781mqstwjfqyxd
       foreign key (authority_id)
       references authority_securities
Hibernate:
    alter table role_authority_mapping
       add constraint FK63tt4pemb3pyen38wkbqxiaq
       foreign key (role_id)
       references role_securities
Hibernate:
    alter table user_securities
       add constraint FKq7ijybs14sxukrpuyos8au1d
       foreign key (role_id)
       references role_securities
Hibernate: INSERT INTO ROLE_SECURITIES (id, name) VALUES(UUID(), 'ADMIN')
Hibernate: INSERT INTO ROLE_SECURITIES (id, name) VALUES(UUID(), 'USER')
Hibernate: INSERT INTO ROLE_SECURITIES (id, name) VALUES(UUID(), 'GUEST')
Hibernate: INSERT INTO AUTHORITY_SECURITIES (id, authority) VALUES(UUID(), 'READ_ALL')
Hibernate: INSERT INTO AUTHORITY_SECURITIES (id, authority) VALUES(UUID(), 'EDIT_ALL')
Hibernate: INSERT INTO AUTHORITY_SECURITIES (id, authority) VALUES(UUID(), 'DELETE_ALL')
Hibernate: INSERT INTO AUTHORITY_SECURITIES (id, authority) VALUES(UUID(), 'READ_SELF')
Hibernate: INSERT INTO AUTHORITY_SECURITIES (id, authority) VALUES(UUID(), 'EDIT_SELF')
Hibernate: INSERT INTO AUTHORITY_SECURITIES (id, authority) VALUES(UUID(), 'CREATE')
Hibernate: INSERT INTO ROLE_AUTHORITY_MAPPING (authority_id, role_id) VALUES ( (SELECT id FROM AUTHORITY_SECURITIES WHERE authority = 'READ_ALL'),(SELECT id FROM ROLE_SECURITIES WHERE name = 'ADMIN'))
Hibernate: INSERT INTO ROLE_AUTHORITY_MAPPING (authority_id, role_id) VALUES ( (SELECT id FROM AUTHORITY_SECURITIES WHERE authority = 'EDIT_ALL'),(SELECT id FROM ROLE_SECURITIES WHERE name = 'ADMIN'))
Hibernate: INSERT INTO ROLE_AUTHORITY_MAPPING (authority_id, role_id) VALUES ( (SELECT id FROM AUTHORITY_SECURITIES WHERE authority = 'DELETE_ALL'),(SELECT id FROM ROLE_SECURITIES WHERE name = 'ADMIN'))
Hibernate: INSERT INTO ROLE_AUTHORITY_MAPPING (authority_id, role_id) VALUES ( (SELECT id FROM AUTHORITY_SECURITIES WHERE authority = 'READ_SELF'),(SELECT id FROM ROLE_SECURITIES WHERE name = 'ADMIN'))
Hibernate: INSERT INTO ROLE_AUTHORITY_MAPPING (authority_id, role_id) VALUES ( (SELECT id FROM AUTHORITY_SECURITIES WHERE authority = 'EDIT_SELF'),(SELECT id FROM ROLE_SECURITIES WHERE name = 'ADMIN'))
Hibernate: INSERT INTO ROLE_AUTHORITY_MAPPING (authority_id, role_id) VALUES ( (SELECT id FROM AUTHORITY_SECURITIES WHERE authority = 'READ_SELF'),(SELECT id FROM ROLE_SECURITIES WHERE name = 'USER'))
Hibernate: INSERT INTO ROLE_AUTHORITY_MAPPING (authority_id, role_id) VALUES ( (SELECT id FROM AUTHORITY_SECURITIES WHERE authority = 'EDIT_SELF'),(SELECT id FROM ROLE_SECURITIES WHERE name = 'USER'))
Hibernate: INSERT INTO ROLE_AUTHORITY_MAPPING (authority_id, role_id) VALUES ( (SELECT id FROM AUTHORITY_SECURITIES WHERE authority = 'CREATE'),(SELECT id FROM ROLE_SECURITIES WHERE name = 'GUEST'))
Hibernate: SELECT id FROM AUTHORITY_SECURITIES WHERE authority = 'READ_ALL'
Hibernate: SELECT id FROM ROLE_SECURITIES WHERE name = 'ADMIN'
Hibernate: SELECT ue.id, us.id FROM USER_ENTITIES ue JOIN USER_SECURITIES us ON us.id = us.id