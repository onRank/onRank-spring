package com.onrank.server.api.calendar;

import com.onrank.server.api.dto.student.CalendarDetailResponse;
import com.onrank.server.api.dto.student.CalendarResponse;
import com.onrank.server.api.service.notification.NotificationTimeResolver;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.notification.Notification;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.onrank.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class CalendarAssembler {

    private final MemberJpaRepository memberRepository;
    private final NotificationTimeResolver timeResolver;

    public CalendarResponse assembleCalendarResponse(Student student, Study study, List<Notification> notifications) {
        Member member = memberRepository.findByStudentStudentIdAndStudyStudyId(student.getStudentId(), study.getStudyId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        List<CalendarDetailResponse> detailList = notifications.stream()
                .map(n -> CalendarDetailResponse.builder()
                        .title(n.getNotificationTitle())
                        .relatedUrl(n.getRelatedUrl())
                        .category(n.getNotificationCategory())
                        .time(timeResolver.resolveTime(n))
                        .build())
                .toList();

        return new CalendarResponse(
                study.getStudyName(),
                member.getMemberColorCode(),
                detailList
        );
    }
}
