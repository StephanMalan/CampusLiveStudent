package models;

import java.io.Serializable;
import java.util.List;

public class Student implements Serializable {

    private String studentNumber;
    private String campus;
    private String qualification;
    private String firstName;
    private String lastName;
    private String email;
    private List<ClassAndResult> classAndResults;

    public Student(String studentNumber, String campus, String qualification, String firstName, String lastName, String email, List<ClassAndResult> classAndResults) {
        this.studentNumber = studentNumber;
        this.campus = campus;
        this.qualification = qualification;
        firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.classAndResults = classAndResults;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getCampus() {
        return campus;
    }

    public String getQualification() {
        return qualification;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public List<ClassAndResult> getClassAndResults() {
        return classAndResults;
    }
}