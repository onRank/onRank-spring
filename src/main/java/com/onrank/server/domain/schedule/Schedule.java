package com.onrank.server.domain.schedule;

import com.onrank.server.domain.attendance.Attendance;
import com.onrank.server.domain.study.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    @Column(nullable = false)
    private String scheduleTitle;

    @Column(nullable = false)
    private String scheduleContent;

    @Column(nullable = false)
    private LocalDateTime scheduleStartingAt;
}
