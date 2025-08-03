create table tasks
(
    id          bigserial primary key,
    external_id varchar(128) not null,
    title       varchar(255) not null,
    description text,
    status      varchar(30)  not null,
    priority    varchar(30)  not null,
    due_date    date,
    created_at  timestamp default now(),
    updated_at  timestamp default now(),
    unique (external_id)
)
