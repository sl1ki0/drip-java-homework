package model;

import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.Table;

import java.time.LocalDate;

@Table(name = "students")
public class Student {

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private int age;

    @Column
    private double grade;

    @Column
    private LocalDate enrollmentDate;

    public Student() {
    }

    public Student(String name, int age, double grade, LocalDate enrollmentDate) {
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.enrollmentDate = enrollmentDate;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', age=" + age
                + ", grade=" + grade + ", enrollmentDate=" + enrollmentDate + "}";
    }
}
