package models;

import java.io.Serializable;

public class ContactDetails implements Serializable {

    private String name;
    private String position;
    private String contactNumber;
    private String email;

    public ContactDetails(String name, String position, String contactNumber, String email) {
        this.name = name;
        this.position = position;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }
}
