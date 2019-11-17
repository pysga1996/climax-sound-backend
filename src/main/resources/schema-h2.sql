create table country
(
    id   int          not null
        primary key,
    name varchar(255) not null,
    constraint country_name_uindex
        unique (name),
    constraint name_UNIQUE
        unique (name)
);