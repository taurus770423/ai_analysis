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

create table customer_details (
    id integer generated always as identity,
    line_user_id varchar(50) not null,
    display_name varchar(30) not null default '',
    picture_url varchar(500),
    email varchar(100),
    create_time timestamp not null,
    last_login_time timestamp,
    primary key (id)
);

select * from customer_details