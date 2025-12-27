package ru.hogwarts.school.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.dto.StudentDto;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentsRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static ru.hogwarts.school.utils.StudentsObjectFactory.createFacultyObject;
import static ru.hogwarts.school.utils.StudentsObjectFactory.createStudentObject;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StudentControllerTest {

    @LocalServerPort
    private int port;
    private RestTestClient restTestClient;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    StudentsRepository studentsRepository;

    @BeforeEach
    public void setup() {
        restTestClient = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @AfterEach
    public void tearDown() {
        studentsRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void createNewStudent() {
        Faculty faculty = createFacultyObject("Slizerin", "yellow");
        facultyRepository.save(faculty);
        StudentDto dto = new StudentDto("Harry", 17, faculty.getId());

        var student = restTestClient.post()
                .uri("/student")
                .contentType(APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Student.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(student);
        assertThat(student.getName()).isEqualTo("Harry");
    }

    @Test
    void getStudentById() {
        Student student = createStudentObject("Harry", 19);
        studentsRepository.save(student);
        var studentResponse = restTestClient.get()
                .uri("/student/{id}", student.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Student.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(studentResponse);
        assertThat(studentResponse.getName()).isEqualTo("Harry");
    }

    @Test
    void updateStudent() {
        Faculty faculty = createFacultyObject("Slizerin", "yellow");
        facultyRepository.save(faculty);
        Student student = createStudentObject("Harry", 19);
        studentsRepository.save(student);
        StudentDto dto = new StudentDto("Henry", 10, faculty.getId());
        var studentResponse = restTestClient.put()
                .uri("/student/update-student/{id}", student.getId())
                .contentType(APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Student.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(studentResponse);
        assertThat(studentResponse.getName()).isEqualTo("Henry");
    }

    @Test
    void deleteStudent() {
        Faculty faculty = createFacultyObject("Slizerin", "yellow");
        facultyRepository.save(faculty);
        Student student = createStudentObject("Harry", 19);
        studentsRepository.save(student);
        restTestClient.delete()
                .uri("/student/delete-student/{id}", student.getId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void findStudents() {
        Student student = createStudentObject("Harry", 19);
        Student student2 = createStudentObject("Harry", 25);
        studentsRepository.save(student);
        studentsRepository.save(student2);
        var studentResponse = restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/student")
                        .queryParam("age", 19)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Collection<Student>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(studentResponse);
        assertThat(studentResponse.size()).isEqualTo(1);
    }

    @Test
    void findStudentsWhereAgeBetweenValues() {
        Student student = createStudentObject("Harry", 19);
        Student student2 = createStudentObject("Harry", 25);
        studentsRepository.save(student);
        studentsRepository.save(student2);
        var studentResponse = restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/student/between/{min_age}")
                        .queryParam("maxAge", 20)
                        .build(10))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Collection<Student>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(studentResponse);
        assertThat(studentResponse.size()).isEqualTo(1);
    }

    @Test
    void getStudentFaculty() {
        Faculty faculty = createFacultyObject("Slizerin", "yellow");
        facultyRepository.save(faculty);
        Student student = createStudentObject("Harry", 19);
        student.setFaculty(facultyRepository.findById(faculty.getId()).get());
        studentsRepository.save(student);
        var facultyResponse = restTestClient.get()
                .uri("/student/faculty/{studentId}", student.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Faculty.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(facultyResponse);
        assertThat(facultyResponse.getName()).isEqualTo("Slizerin");
    }
}