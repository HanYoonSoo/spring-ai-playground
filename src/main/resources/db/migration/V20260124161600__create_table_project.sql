create table if not exists project (
    id bigserial primary key,
    name varchar(255),
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    deleted_at timestamp
);