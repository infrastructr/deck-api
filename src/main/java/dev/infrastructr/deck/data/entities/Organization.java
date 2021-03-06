package dev.infrastructr.deck.data.entities;

import javax.persistence.*;

import java.util.UUID;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;

    @Column(unique=true, nullable = false)
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
