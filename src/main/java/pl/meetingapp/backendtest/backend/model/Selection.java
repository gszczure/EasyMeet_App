package pl.meetingapp.backendtest.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "selections")
public class Selection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meeting_id")
    private Long meetingId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "date_range_id")
    private Long dateRangeId;

    @Column(name = "state")
    private String state;

    public Selection(Long meetingId, Long userId, Long dateRangeId) {
        this.meetingId = meetingId;
        this.userId = userId;
        this.dateRangeId = dateRangeId;
    }

}

