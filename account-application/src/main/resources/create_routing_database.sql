create table if not exists routing
(
    id           bigint not null
    constraint routing_pkey
    primary key,
    account_id varchar(255) not null,
    shard_id varchar(255) not null
    );
create unique index if not exists routing_account_id_idx
    on routing (account_id);
create sequence if not exists routing_sequesnce start 1;