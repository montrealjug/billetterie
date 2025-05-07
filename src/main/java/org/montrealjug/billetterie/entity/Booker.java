// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Booker {

    @Id
    private String email;

    private String firstName;
    private String lastName;

    @Column(nullable = false, updatable = false)
    private Instant creationTime = Instant.now();

    @Column(nullable = false)
    private String emailSignature;

    private Instant validationTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booker")
    private Set<Participant> participants = new HashSet<>();

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

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Instant getValidationTime() {
        return validationTime;
    }

    public void setValidationTime(Instant validationTime) {
        this.validationTime = validationTime;
    }

    public String getEmailSignature() {
        return emailSignature;
    }

    public void setEmailSignature(String emailSignature) {
        this.emailSignature = emailSignature;
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
