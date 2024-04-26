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
        rs1_0.id,
        as1_0.role_id,
        as1_1.id,
        as1_1.authority,
        rs1_0.name
    from
        role_securities rs1_0
    left join
        role_authority_mapping as1_0
            on rs1_0.id=as1_0.role_id
    left join
        authority_securities as1_1
            on as1_1.id=as1_0.authority_id
    where
        rs1_0.id=?
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
        upper(ue1_0.username)=upper(?)
    fetch
        first ? rows only
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