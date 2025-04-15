package org.montrealjug.billetterie.entity;

import jakarta.persistence.*;

@Entity
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;

    @ManyToOne
    private Booker booker;
}
