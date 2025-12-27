package ru.hogwarts.school.controller.mocked;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentsRepository;
import ru.hogwarts.school.service.StudentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudentController.class)
@Import(StudentServiceImpl.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentsRepository studentsRepository;

    @MockitoBean
    private FacultyRepository facultyRepository;

    @Test
    void createNewStudent() throws Exception {
        Faculty faculty = Faculty.builder()
                .id(1L)
                .name("Gryffindor")
                .color("red")
                .build();

        Student savedStudent = Student.builder()
                .id(1L)
                .name("Harry Potter")
                .age(17)
                .faculty(faculty)
                .build();

        when(facultyRepository.findById(1L))
                .thenReturn(Optional.of(faculty));
        when(studentsRepository.save(any(Student.class)))
                .thenReturn(savedStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Harry Potter\",\"age\":17,\"facultyId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Harry Potter"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void getStudentById() throws Exception {
        Faculty faculty = Faculty.builder()
                .id(1L)
                .name("Slytherin")
                .color("green")
                .build();

        Student student = Student.builder()
                .id(1L)
                .name("Draco Malfoy")
                .age(17)
                .faculty(faculty)
                .build();

        when(studentsRepository.findById(1L))
                .thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Draco Malfoy"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void getStudentById_NotFound() throws Exception {
        when(studentsRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent() throws Exception {
        Faculty oldFaculty = Faculty.builder()
                .id(1L)
                .name("Hufflepuff")
                .color("yellow")
                .build();

        Faculty newFaculty = Faculty.builder()
                .id(2L)
                .name("Ravenclaw")
                .color("blue")
                .build();

        Student existingStudent = Student.builder()
                .id(1L)
                .name("Cedric")
                .age(17)
                .faculty(oldFaculty)
                .build();

        Student updatedStudent = Student.builder()
                .id(1L)
                .name("Cedric Diggory")
                .age(18)
                .faculty(newFaculty)
                .build();

        // Мокаем репозитории
        when(studentsRepository.findById(1L))
                .thenReturn(Optional.of(existingStudent));
        when(facultyRepository.findById(2L))
                .thenReturn(Optional.of(newFaculty));
        when(studentsRepository.save(any(Student.class)))
                .thenReturn(updatedStudent);

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", "Cedric Diggory");
        studentObject.put("age", 18);
        studentObject.put("facultyId", 2);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student/update-student/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentObject.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cedric Diggory"))
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    void updateStudent_NotFound() throws Exception {
        when(studentsRepository.findById(999L))
                .thenReturn(Optional.empty());
        JSONObject studentObject = new JSONObject();
        studentObject.put("name", "Test");
        studentObject.put("age", 20);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student/update-student/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentObject.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudent() throws Exception {
        doNothing().when(studentsRepository).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/delete-student/{id}", 1))
                .andExpect(status().isOk());

        verify(studentsRepository).deleteById(1L);
    }

    @Test
    void findStudentsByAge() throws Exception {
        Student s1 = Student.builder()
                .id(1L)
                .name("Harry")
                .age(17)
                .build();

        Student s2 = Student.builder()
                .id(2L)
                .name("Ron")
                .age(17)
                .build();

        when(studentsRepository.findByAge(17))
                .thenReturn(List.of(s1, s2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student").param("age", "17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Harry", "Ron")))
                .andExpect(jsonPath("$[*].age", everyItem(is(17))));
    }

    @Test
    void findStudentsByAge_InvalidAge() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student").param("age", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findStudentsByAgeBetween() throws Exception {
        Student s1 = Student.builder()
                .id(1L)
                .name("Harry")
                .age(17)
                .build();

        Student s2 = Student.builder()
                .id(2L)
                .name("Hermione")
                .age(18)
                .build();

        Student s3 = Student.builder()
                .id(3L)
                .name("Luna")
                .age(16)
                .build();

        when(studentsRepository.findByAgeBetween(16, 18))
                .thenReturn(List.of(s3, s1, s2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/between/{min_age}", 16)
                        .param("maxAge", "18"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Harry", "Hermione", "Luna")));
    }

    @Test
    void findStudentsByAgeBetween_InvalidParameters() throws Exception {
        // Тест когда minAge > maxAge
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/between/{min_age}", 20)
                        .param("maxAge", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findStudentsByAgeBetween_MissingMaxAge() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/between/{min_age}", 16))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getStudentFaculty() throws Exception {
        Faculty faculty = Faculty.builder()
                .id(1L)
                .name("Gryffindor")
                .color("red")
                .build();

        Student student = Student.builder()
                .id(1L)
                .name("Harry Potter")
                .age(17)
                .faculty(faculty)
                .build();

        when(studentsRepository.findById(1L))
                .thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/faculty/{studentId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("red"));
    }

    @Test
    void getStudentFaculty_StudentNotFound() throws Exception {
        when(studentsRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/faculty/{studentId}", 999))
                .andExpect(status().isNotFound());
    }
}