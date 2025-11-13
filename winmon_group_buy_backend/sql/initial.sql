DO $$
    DECLARE
        r RECORD;
    BEGIN
                FOR r IN (
    SELECT tablename
    FROM pg_tables
    WHERE schemaname = 'public'
) LOOP
        EXECUTE format('DROP TABLE IF EXISTS public.%I CASCADE;', r.tablename);
    END LOOP;
END $$;

create table user_details (
    id varchar(50) not null,
    status varchar(10),
    create_time timestamp not null,
    modify_time timestamp not null,
    last_login_time timestamp not null,
    primary key (id)
);

insert into user_details (id, status, create_time, modify_time, last_login_time) values
 ('119608da-562a-4731-9c07-5c594bd1fbe5', 'NORMAL', now(), now(), now());

create table user_line_details (
    user_id varchar(50) not null,
    line_user_id varchar(50) not null,
    display_name varchar(30) not null default '',
    picture_url varchar(500),
    email varchar(100),
    create_time timestamp not null,
    modify_time timestamp,
    primary key (user_id, line_user_id),
    constraint user_line_details_user_id foreign key (user_id) references user_details(id)
);
insert into user_line_details (user_id, line_user_id, display_name, picture_url, email, create_time, modify_time) values
 ('119608da-562a-4731-9c07-5c594bd1fbe5', 'U40ff24b3cd5c8f5fdb24fa5a7f29f5d6', 'Ting Hong', 'https://profile.line-scdn.net/0hyNmVDkIvJmFQLzgOMCpZNi1qKAwnASApKBtrBXV8fgIuHzZjbUluACcuKAN1TWU2bU9tBCB6ell0ABF2aAxscxAvPVUkQhVlGBQvbgp6PyYjazt8LBURUnZYcU0oeBtPHBo9RDdbcQx8YgphAitqbHJqOU0YazE0CRY', 'taurus770423@gmail.com', now(), now())

create table shop_details (
    id varchar(50) not null,
    status varchar(10),
    user_id varchar(50) not null,
    bot_basic_id varchar(10) not null,
    login_channel_id varchar(15) not null,
    login_channel_secret varchar(40) not null,
    create_time timestamp not null,
    primary key (id),
    unique (user_id, bot_basic_id),
    constraint shop_details_user_id foreign key (user_id) references user_details(id)
);
insert into shop_details (id, status, user_id, bot_basic_id, login_channel_id, login_channel_secret, create_time) values
 ('f88054d5-4773-4cf5-9920-f469df595fb8', 'OPEN', '119608da-562a-4731-9c07-5c594bd1fbe5', '@323cbvhx', '2008445940', 'e4e018ad8451a785291e8fafbe13251d', now());