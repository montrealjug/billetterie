package org.montrealjug.billetterie.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Participant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String firstName;
	private String lastName;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "booker_email", nullable = false)
	private Booker booker;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Booker getBooker() {
		return booker;
	}

	public void setBooker(Booker booker) {
		this.booker = booker;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Participant that)) {
			return false;
		}
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
