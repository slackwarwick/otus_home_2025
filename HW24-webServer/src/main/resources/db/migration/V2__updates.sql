create table address
(
    id   bigserial not null primary key,
    street varchar(50)
);

alter table client add column address_id bigint;
alter table if exists client add constraint client_fk1 foreign key (address_id) references address;

create table phone
(
    id   bigserial not null primary key,
    client_id bigint not null,
    number varchar(50),
    CONSTRAINT phone_fk1 FOREIGN KEY (client_id) REFERENCES client(id)
);

