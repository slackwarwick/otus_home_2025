alter table address add column client_id bigint;

update address set client_id = c.id
from client c
where c.address_id = address.id;

alter table address add constraint address_fk1 foreign key (client_id) references client(id);
alter table client drop column address_id;
