// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime startTime;
    private String title;
    private String description;
    private int maxParticipants;
    private int maxWaitingQueue;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "activity", orphanRemoval = true)
    private SortedSet<ActivityParticipant> participants = new TreeSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Event event;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getMaxWaitingQueue() {
        return maxWaitingQueue;
    }

    public void setMaxWaitingQueue(int maxWaitingQueue) {
        this.maxWaitingQueue = maxWaitingQueue;
    }

    public Set<ActivityParticipant> getParticipants() {
        return participants;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setParticipants(Set<ActivityParticipant> participants) {
        if (participants instanceof SortedSet<ActivityParticipant> sortedParticipants) {
            this.participants = sortedParticipants;
        } else {
            this.participants = new TreeSet<>(participants);
        }
    }

    @Transient
    public List<ActivityParticipant> getNonWaitingParticipants() {
        // ensure that `ActivityParticipant` are lazy-load if needed
        return this.getParticipants().stream().limit(this.maxParticipants).toList();
    }

    @Transient
    public List<ActivityParticipant> getWaitingParticipants() {
        // ensure that `ActivityParticipant` are lazy-load if needed
        return this.getParticipants().stream().skip(this.maxParticipants).limit(this.maxWaitingQueue).toList();
    }

    public enum RegistrationStatus {
        OPEN,
        WAITING_LIST,
        CLOSED,
    }

    @Transient
    public RegistrationStatus getRegistrationStatus() {
        // ensure that we will lazy-load ActivityParticipant if needed
        var nbParticipants = getParticipants().size();
        final RegistrationStatus registrationStatus;
        if (nbParticipants <= this.maxParticipants) {
            registrationStatus = RegistrationStatus.OPEN;
        } else if (nbParticipants <= this.maxParticipants + this.maxWaitingQueue) {
            registrationStatus = RegistrationStatus.WAITING_LIST;
        } else {
            registrationStatus = RegistrationStatus.CLOSED;
        }
        return registrationStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id == activity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
