package ru.hogwarts.school.controller.mocked;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FacultyController.class)
@Import(FacultyServiceImpl.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyController facultyController;

    @Test
    void getFacultyInfo() throws Exception {
        Faculty faculty = Faculty.builder().id(1L).name("Slytherin").color("green").build();
        Mockito.when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/faculty/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Slytherin"))
                .andExpect(jsonPath("$.color").value("green"));
    }

    @Test
    void createFaculty() throws Exception {
        Faculty savedFaculty = Faculty.builder()
                .id(2L)
                .name("Gryffindor")
                .color("red")
                .build();

        Mockito.when(facultyRepository.save(any(Faculty.class)))
                .thenReturn(savedFaculty);
        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", "Gryffindor");
        facultyObject.put("color", "red");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyObject.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("red"));
    }

    @Test
    void editFaculty() throws Exception {
        Faculty existingFaculty = Faculty.builder()
                .id(3L)
                .name("Hufflepuff")
                .color("yellow")
                .build();

        Faculty updatedFaculty = Faculty.builder()
                .id(3L)
                .name("HufflepuffUpdated")
                .color("yellow")
                .build();

        Mockito.when(facultyRepository.findById(3L))
                .thenReturn(Optional.of(existingFaculty));
        Mockito.when(facultyRepository.save(any(Faculty.class)))
                .thenReturn(updatedFaculty);
        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", "HufflepuffUpdated");
        facultyObject.put("color", "yellow");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty/update/{id}", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyObject.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("HufflepuffUpdated"))
                .andExpect(jsonPath("$.color").value("yellow"));
    }

    @Test
    void deleteFaculty() throws Exception {
        Mockito.doNothing().when(facultyRepository).deleteById(4L);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", 4))
                .andExpect(status().isOk());

        Mockito.verify(facultyRepository).deleteById(4L);
    }

    @Test
    void findFaculties() throws Exception {
        Faculty f1 = Faculty.builder()
                .id(1L)
                .name("Slytherin")
                .color("green")
                .build();

        Mockito.when(facultyRepository.findByColor("green"))
                .thenReturn(List.of(f1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty").param("color", "green"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].color").value("green"));
    }

    @Test
    void findFacultiesByNameOrColor() throws Exception {
        Faculty f1 = Faculty.builder()
                .id(1L)
                .name("Slytherin")
                .color("green")
                .build();

        Mockito.when(facultyRepository.findByNameOrColorIgnoreCase("green"))
                .thenReturn(List.of(f1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/name_or_color").param("value", "green"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].color").value("green"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getStudentFaculty() throws Exception {
        Student s1 = Student.builder()
                .id(1L)
                .name("Draco")
                .age(17)
                .build();
        Student s2 = Student.builder()
                .id(2L)
                .name("Pansy")
                .age(18)
                .build();

        Faculty faculty = Faculty.builder()
                .id(1L)
                .name("Slytherin")
                .color("green")
                .students(List.of(s1, s2))
                .build();

        Mockito.when(facultyRepository.findById(1L))
                .thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/student/{faculty_id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Draco", "Pansy")));
    }
}
