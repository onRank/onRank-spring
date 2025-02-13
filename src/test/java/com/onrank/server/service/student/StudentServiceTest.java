package com.onrank.server.service.student;

import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.domain.student.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 테스트 후 롤백
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Test
    public void findStudentByEmail() throws Exception {
        //given
        String email = "test@example.com";
        Student student = Student.builder()
                .name("Test User")
                .email(email)
                .department("Computer Science")
                .phoneNumber("010-1234-5678")
                .build();

        //when
        studentService.createMember(student);

        //then
        Optional<Student> findStudent = studentService.findByEmail(email);
        assertThat(findStudent).isPresent();
        assertThat(findStudent.get().getEmail()).isEqualTo(email);
        assertThat(findStudent.get().getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(findStudent.get().getDepartment()).isEqualTo("Computer Science");
        assertThat(findStudent.get().getName()).isEqualTo("Test User");
    }

    @Test
    public void findStudentById() throws Exception {
        //given
        String email = "test@example.com";
        Student student = Student.builder()
                .name("Test User")
                .email(email)
                .department("Computer Science")
                .phoneNumber("010-1234-5678")
                .build();

        //when
        studentService.createMember(student);

        //then
        Optional<Student> findStudent = studentService.findById(student.getId());
        assertThat(findStudent).isPresent();
        assertThat(findStudent.get().getEmail()).isEqualTo(email);
        assertThat(findStudent.get().getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(findStudent.get().getDepartment()).isEqualTo("Computer Science");
        assertThat(findStudent.get().getName()).isEqualTo("Test User");
    }

    @Test
    public void notFound() throws Exception {
        //given
        String email = "nonexistent@example.com";


        //when: 존재하지 않는 이메일로 조회
        Optional<Student> findStudent = studentService.findByEmail(email);

        //then: 학생이 존재하지 않아야 함
        assertThat(findStudent).isNotPresent();
    }
}
