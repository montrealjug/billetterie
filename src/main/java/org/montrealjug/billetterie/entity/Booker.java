package org.montrealjug.billetterie.entity;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
public class Booker {
    @Id
    private String email;
    private String firstName;
    private String lastName;

    @OneToMany
    private Set<Participant> participants;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Booker booker = (Booker) o;
        return Objects.equals(email, booker.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
