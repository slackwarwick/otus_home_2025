alter table client alter column id SET DEFAULT nextval('client_SEQ');
ALTER SEQUENCE client_SEQ OWNED BY client.id;
