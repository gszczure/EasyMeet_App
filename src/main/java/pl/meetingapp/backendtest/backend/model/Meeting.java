package pl.meetingapp.backendtest.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "meeting")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Meeting name is mandatory")
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(unique = true)
    private String code;

    @ManyToMany
    @JoinTable(
            name = "meeting_participants",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();

    @Column(name = "meeting_date")
    private String meetingDate;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DateRange> dateRanges = new ArrayList<>();

    public Meeting() {}

    public Meeting(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.code = generateUniqueCode();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getCode() {
        return code;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void addParticipant(User user) {
        this.participants.add(user);
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 5);
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public List<DateRange> getDateRanges() {
        return dateRanges;
    }

    public void addDateRange(DateRange dateRange) {
        dateRanges.add(dateRange);
        dateRange.setMeeting(this);
    }

    public void removeDateRange(DateRange dateRange) {
        dateRanges.remove(dateRange);
        dateRange.setMeeting(null);
    }
}
