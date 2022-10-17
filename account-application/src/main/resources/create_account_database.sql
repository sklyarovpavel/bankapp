create table if not exists account
(
    id           varchar(255) not null
    constraint account_pkey
    primary key,
    amount  decimal    not null,
    currency varchar(255) not null,
    version integer not null
);