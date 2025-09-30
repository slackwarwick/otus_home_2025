package ru.otus.crm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Table("client")
public class Client implements Cloneable {

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("login")
    private String login;

    @Column("password")
    private String password;

    @MappedCollection(idColumn = "client_id")
    private Address address;

    @MappedCollection(idColumn = "client_id")
    private Set<Phone> phones;

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(Long id, String name, String login, Address address, Set<Phone> phones) {
        this(id, name);
        this.login = login;
        this.address = address;
        this.phones = phones;
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        Client c = new Client(
                this.id,
                this.name,
                this.login, this.address == null ? null : this.address.clone(),
                this.phones == null ? null : this.phones.stream().map(Phone::clone).collect(Collectors.toSet()));
        c.password = this.password;
        return c;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
