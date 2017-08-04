package models;

import java.io.Serializable;
import java.util.List;

public class Lecturer implements Serializable{

    private String lecturerID;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String email;

    public Lecturer(String lecturerID, String firstName, String lastName, String contactNumber, String email) {
        this.lecturerID = lecturerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getLecturerID() {
        return lecturerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

}
