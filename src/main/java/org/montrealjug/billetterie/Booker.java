package org.montrealjug.billetterie;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Booker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String firstName;
    private String lastName;

    @OneToMany
    private Set<Participant> participants;


}
