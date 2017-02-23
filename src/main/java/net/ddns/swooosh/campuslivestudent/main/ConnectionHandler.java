package net.ddns.swooosh.campuslivestudent.main;

import net.ddns.swooosh.campuslivestudent.models.StudentClass;

import java.util.Arrays;
import java.util.List;

public class ConnectionHandler {

    public ConnectionHandler() {

    }

    public Boolean authorise(String studentNumber, String password) {
        Boolean result = studentNumber.equals("DV2015-0073") && password.equals("password");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<StudentClass> getClasses() {
        return Arrays.asList(
                new StudentClass(1, "Object Oriented Systems Analysis and Design", "C_ITOO311", "A0002", "Henk Lubbe", 2, 3, 4),
                new StudentClass(2, "Social Practices and Security", "C_ITSC311", "B201", "Stephen", 2, 5, 6),
                new StudentClass(3, "Software Development Project 3", "C_ITSP300", "A0002", "Nyarai Tunjera", 2, 7, 8),
                new StudentClass(4, "Advanced Database Systems", "C_ITDA310", "A0002", "Emanuel Madzume", 3, 1, 2),
                new StudentClass(5, "Internet Programming and e-Commerce", "C_ITEC301", "A0002", "Tem", 3, 3, 4),
                new StudentClass(6, "Object Oriented Systems Analysis and Design", "C_ITOO310", "A0001", "Emanuel Madzume", 4, 3, 4),
                new StudentClass(7, "Social Practices and Security", "C_ITSC3111", "A0001", "Henk Lubbe", 4, 5, 6),
                new StudentClass(8, "Advanced Database Systems", "C_ITDA310", "A0002", "Emanuel Madzume", 4, 7, 8));
    }

}
