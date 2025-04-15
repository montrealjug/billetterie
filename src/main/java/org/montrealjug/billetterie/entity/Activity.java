package org.montrealjug.billetterie.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

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
    @OneToMany
    private Set<Participant> participants;

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

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id == activity.id ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, title, description, maxParticipants, maxWaitingQueue, participants);
    }
}
