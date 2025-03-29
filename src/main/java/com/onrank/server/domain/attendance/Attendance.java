package com.onrank.server.domain.attendance;

import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.schedule.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attendaces")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus attendanceStatus = AttendanceStatus.UNKNOWN; // 기본값 설정

    @Builder
    public Attendance(Schedule schedule, Member member) {
        this.schedule = schedule;
        this.member = member;
    }

    public void updateStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }
}
