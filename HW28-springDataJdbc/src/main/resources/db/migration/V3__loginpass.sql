alter table client add column login text;
alter table client add column password text;

insert into client (id, name, login, password) values (1, 'Admin', 'admin', '123');
alter sequence client_SEQ restart with 2;
