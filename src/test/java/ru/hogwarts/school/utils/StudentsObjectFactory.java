package ru.hogwarts.school.utils;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

public class StudentsObjectFactory {

    public static Faculty createFacultyObject(String name, String color){
        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setColor(color);
        return faculty;
    }

    public static Student createStudentObject(String name, int age){
        Student student = new Student();
        student.setName(name);
        student.setAge(age);
        return student;
    }
}
