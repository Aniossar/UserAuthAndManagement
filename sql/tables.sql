-- DROP TABLE IF EXISTS public.online_user_table;
-- DROP TABLE IF EXISTS public.users_receipt_summary;
-- DROP TABLE IF EXISTS public.user_table;
-- DROP TABLE IF EXISTS public.role_table;
-- DROP TABLE IF EXISTS public.activity_table;

CREATE TABLE IF NOT EXISTS public.role_table
(
    id int NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL
);

insert into role_table(name) values ('ROLE_ADMIN');
insert into role_table(name) values ('ROLE_MODERATOR');
insert into role_table(name) values ('ROLE_STORAGE');
insert into role_table(name) values ('ROLE_USER');

CREATE TABLE IF NOT EXISTS public.activity_table
(
    id int NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    activity_time timestamp with time zone NOT NULL,
    activity_type varchar(50) NOT NULL,
    login varchar(100) NOT NULL,
    activity_message varchar(3000) NOT NULL
);

CREATE TABLE IF NOT EXISTS public.user_table
(
    id int NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    login varchar(100) NOT NULL,
    password varchar(500) NOT NULL,
    email varchar(100),
    full_name varchar(100),
    company_name varchar(100),
    phone_number varchar(100),
    address varchar(500),
    role_id  integer
        constraint user_table_role_table_id_fk
            references role_table,
    enabled boolean
);

create unique index user_table_login_uindex
    on user_table (login);

CREATE TABLE IF NOT EXISTS public.online_user_table
(
    user_id int NOT NULL PRIMARY KEY
        constraint online_user_id_fk
            references user_table(id),
    user_login varchar(100) NOT NULL,
    last_ping_time timestamp with time zone NOT NULL
);