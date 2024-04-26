Hibernate:
    select
        ue1_0.id,
        ue1_0.created,
        ue1_0.email,
        ue1_0.isactive,
        ue1_0.last_login,
        ue1_0.modified,
        ue1_0.name,
        ue1_0.password,
        ue1_0.token,
        ue1_0.username
    from
        user_entities ue1_0
    where
        upper(ue1_0.email)=upper(?)
    fetch
        first ? rows only
Hibernate:
    select
        us1_0.id,
        us1_0.account_non_expired,
        us1_0.account_non_locked,
        us1_0.credentials_non_expired,
        us1_0.email,
        us1_0.enabled,
        us1_0.password,
        us1_0.role_id,
        us1_0.username
    from
        user_securities us1_0
    where
        upper(us1_0.username)=upper(?)
    fetch
        first ? rows only
Hibernate:
    select
        pe1_0.id,
        pe1_0.city_code,
        pe1_0.country_code,
        pe1_0.number,
        pe1_0.user_id
    from
        phone_entities pe1_0
    where
        pe1_0.country_code=?
        and pe1_0.city_code=?
        and pe1_0.number=?
Hibernate:
    select
        rs1_0.id,
        rs1_0.name
    from
        role_securities rs1_0
    where
        upper(rs1_0.name)=upper(?)
Hibernate:
    select
        as1_0.role_id,
        as1_1.id,
        as1_1.authority
    from
        role_authority_mapping as1_0
    join
        authority_securities as1_1
            on as1_1.id=as1_0.authority_id
    where
        as1_0.role_id=?
Hibernate:
    select
        rs1_0.authority_id,
        rs1_1.id,
        rs1_1.name
    from
        role_authority_mapping rs1_0
    join
        role_securities rs1_1
            on rs1_1.id=rs1_0.role_id
    where
        rs1_0.authority_id=?
Hibernate:
    select
        as1_0.role_id,
        as1_1.id,
        as1_1.authority
    from
        role_authority_mapping as1_0
    join
        authority_securities as1_1
            on as1_1.id=as1_0.authority_id
    where
        as1_0.role_id=?
Hibernate:
    select
        rs1_0.authority_id,
        rs1_1.id,
        rs1_1.name
    from
        role_authority_mapping rs1_0
    join
        role_securities rs1_1
            on rs1_1.id=rs1_0.role_id
    where
        rs1_0.authority_id=?
Hibernate:
    select
        rs1_0.authority_id,
        rs1_1.id,
        rs1_1.name
    from
        role_authority_mapping rs1_0
    join
        role_securities rs1_1
            on rs1_1.id=rs1_0.role_id
    where
        rs1_0.authority_id=?
Hibernate:
    select
        rs1_0.authority_id,
        rs1_1.id,
        rs1_1.name
    from
        role_authority_mapping rs1_0
    join
        role_securities rs1_1
            on rs1_1.id=rs1_0.role_id
    where
        rs1_0.authority_id=?
Hibernate:
    select
        rs1_0.authority_id,
        rs1_1.id,
        rs1_1.name
    from
        role_authority_mapping rs1_0
    join
        role_securities rs1_1
            on rs1_1.id=rs1_0.role_id
    where
        rs1_0.authority_id=?
Hibernate:
    select
        rs1_0.id,
        rs1_0.name
    from
        role_securities rs1_0
    where
        upper(rs1_0.name)=upper(?)
Hibernate:
    select
        as1_0.id,
        as1_0.authority
    from
        authority_securities as1_0
    left join
        role_authority_mapping rs1_0
            on as1_0.id=rs1_0.authority_id
    where
        rs1_0.role_id=?
Hibernate:
    select
        rs1_0.id,
        rs1_0.name
    from
        role_securities rs1_0
    where
        upper(rs1_0.name)=upper(?)
Hibernate:
    select
        ue1_0.id,
        ue1_0.created,
        ue1_0.email,
        ue1_0.isactive,
        ue1_0.last_login,
        ue1_0.modified,
        ue1_0.name,
        ue1_0.password,
        pe1_0.user_id,
        pe1_0.id,
        pe1_0.city_code,
        pe1_0.country_code,
        pe1_0.number,
        ue1_0.token,
        ue1_0.username
    from
        user_entities ue1_0
    left join
        phone_entities pe1_0
            on ue1_0.id=pe1_0.user_id
    where
        ue1_0.id=?
Hibernate:
    select
        us1_0.id,
        us1_0.account_non_expired,
        us1_0.account_non_locked,
        us1_0.credentials_non_expired,
        us1_0.email,
        us1_0.enabled,
        us1_0.password,
        us1_0.role_id,
        r1_0.id,
        as1_0.role_id,
        as1_1.id,
        as1_1.authority,
        r1_0.name,
        us1_0.username
    from
        user_securities us1_0
    join
        role_securities r1_0
            on r1_0.id=us1_0.role_id
    left join
        role_authority_mapping as1_0
            on r1_0.id=as1_0.role_id
    left join
        authority_securities as1_1
            on as1_1.id=as1_0.authority_id
    where
        us1_0.id=?
Hibernate:
    select
        as1_0.id,
        as1_0.authority
    from
        authority_securities as1_0
    left join
        role_authority_mapping rs1_0
            on as1_0.id=rs1_0.authority_id
    where
        rs1_0.role_id=?
Hibernate:
    insert
    into
        user_entities
        (created, email, isactive, last_login, modified, name, password, token, username, id)
    values
        (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
Hibernate:
    insert
    into
        phone_entities
        (city_code, country_code, number, user_id, id)
    values
        (?, ?, ?, ?, ?)
Hibernate:
    insert
    into
        user_securities
        (account_non_expired, account_non_locked, credentials_non_expired, email, enabled, password, role_id, username, id)
    values
        (?, ?, ?, ?, ?, ?, ?, ?, ?)
Hibernate:
    delete
    from
        role_authority_mapping
    where
        role_id=?
Hibernate:
    insert
    into
        role_authority_mapping
        (role_id, authority_id)
    values
        (?, ?)
Hibernate:
    insert
    into
        role_authority_mapping
        (role_id, authority_id)
    values
        (?, ?)
Hibernate:
    insert
    into
        role_authority_mapping
        (role_id, authority_id)
    values
        (?, ?)
Hibernate:
    insert
    into
        role_authority_mapping
        (role_id, authority_id)
    values
        (?, ?)
Hibernate:
    insert
    into
        role_authority_mapping
        (role_id, authority_id)
    values
        (?, ?)
