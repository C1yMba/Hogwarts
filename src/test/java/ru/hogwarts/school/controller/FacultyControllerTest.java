package ru.hogwarts.school.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.dto.FacultyDto;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentsRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FacultyControllerTest {

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
    void getFacultyInfo() {
        Faculty faculty = createFacultyObject("Slizerin", "yellow");
        facultyRepository.save(faculty);
        var studentResponse = restTestClient.get()
                .uri("/faculty/{id}", faculty.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Faculty.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(studentResponse);
        assertThat(studentResponse.getName()).isEqualTo("Slizerin");
    }

    @Test
    void createFaculty() {
        FacultyDto dto = new FacultyDto("Gryffindor", "red");

        var response = restTestClient.post()
                .uri("/faculty")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Faculty.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertThat(response.getName()).isEqualTo("Gryffindor");
        assertThat(response.getColor()).isEqualTo("red");
    }

    @Test
    void editFaculty() {
        Faculty faculty = createFacultyObject("Hufflepuff", "yellow");
        facultyRepository.save(faculty);

        FacultyDto dto = new FacultyDto("HufflepuffUpdated", "green");

        var response = restTestClient.put()
                .uri("/faculty/update/{id}", faculty.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Faculty.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertThat(response.getName()).isEqualTo("HufflepuffUpdated");
        assertThat(response.getColor()).isEqualTo("green");
    }

    @Test
    void deleteFaculty() {
        Faculty faculty = createFacultyObject("Ravenclaw", "blue");
        facultyRepository.save(faculty);

        restTestClient.delete()
                .uri("/faculty/{id}", faculty.getId())
                .exchange()
                .expectStatus().isOk();

        assertFalse(facultyRepository.findById(faculty.getId()).isPresent());
    }

    @Test
    void findFaculties() {
        Faculty f1 = createFacultyObject("Slytherin", "green");
        Faculty f2 = createFacultyObject("Gryffindor", "red");
        facultyRepository.saveAll(List.of(f1, f2));

        var response = restTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/faculty")
                        .queryParam("color", "green")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Collection<Faculty>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.iterator().next().getColor()).isEqualTo("green");
    }

    @Test
    void findFacultiesByNameOrColor() {
        Faculty f1 = createFacultyObject("Slytherin", "green");
        Faculty f2 = createFacultyObject("Gryffindor", "red");
        facultyRepository.saveAll(List.of(f1, f2));

        var response = restTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/faculty/name_or_color")
                        .queryParam("value", "red")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Collection<Faculty>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.iterator().next().getColor()).isEqualTo("red");
    }

    @Test
    void getStudentFaculty() {
        Faculty faculty = createFacultyObject("Slytherin", "green");
        facultyRepository.save(faculty);
        Student s1 = createStudentObject("Draco", 17, faculty);
        Student s2 = createStudentObject("Pansy", 18, faculty);
        faculty.setStudents(Set.of(s1, s2));

        var response = restTestClient.get()
                .uri("/faculty/student/{faculty_id}", faculty.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Collection<Student>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.stream().map(Student::getName).toList())
                .containsExactlyInAnyOrder("Draco", "Pansy");
    }
    private Faculty createFacultyObject(String name, String color) {
        return Faculty.builder()
                .name(name)
                .color(color)
                .students(new HashSet<>())
                .build();
    }

    private Student createStudentObject(String name, int age, Faculty faculty) {
        Student student = new Student();
        student.setName(name);
        student.setAge(age);
        student.setFaculty(faculty);
        return studentsRepository.save(student);
    }
}