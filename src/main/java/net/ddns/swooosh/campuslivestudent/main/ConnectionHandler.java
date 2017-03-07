package net.ddns.swooosh.campuslivestudent.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.ddns.swooosh.campuslivestudent.models.*;

import java.util.Arrays;

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

    public Student getStudent() {
        return new Student("DV2015-0073", "Durbanville", "BSc IT", "Stephan", "Malan", "stephanmalan.rob@gmail.com", FXCollections.observableList(Arrays.asList(
                new ClassAndResult(new StudentClass("Object Oriented Systems Analysis and Design", "C_ITOO311", "Henk", "Lubbe", "Dv001", "henk@cti.ac.za", FXCollections.observableArrayList(Arrays.asList(new ClassTime("A0002", 2, 3, 4), new ClassTime("A0001", 4, 3, 4))), getFiles1()), FXCollections.observableArrayList(Arrays.asList(new Result("Continuous assessment", 80D, 100D, 0.1), new Result("Semester Test", 80D, 100D, 0.2), new Result("Assignment", 80D, 100D, 0.2), new Result("Examination", 80D, 100D, 0.5)))),
                new ClassAndResult(new StudentClass("Social Practices and Security", "C_ITSC311", "Stephen", "L", "Dv002", "stephen@cti.ac.za", FXCollections.observableArrayList(Arrays.asList(new ClassTime("B201", 2, 5, 6), new ClassTime("A0001", 4, 5, 6))), getFiles2()), FXCollections.observableArrayList(Arrays.asList(new Result("Continuous assessment", 80D, 100D, 0.1), new Result("Semester Test", 80D, 100D, 0.2), new Result("Assignment", 80D, 100D, 0.2), new Result("Examination", 80D, 100D, 0.5)))),
                new ClassAndResult(new StudentClass("Software Development Project 3", "C_ITSP300", "Nyarai", "Tunjera", "Dv003", "nyarai@cti.ac.za", FXCollections.observableArrayList(Arrays.asList(new ClassTime("A0002", 2, 7, 8))), getFiles1()), FXCollections.observableArrayList(Arrays.asList(new Result("Continuous assessment", 80D, 100D, 0.1), new Result("Semester Test", 80D, 100D, 0.2), new Result("Assignment", 80D, 100D, 0.2), new Result("Examination", 80D, 100D, 0.5)))),
                new ClassAndResult(new StudentClass("Advanced Database Systems", "C_ITDA310", "Emanuel", "Madzume", "Dv004", "emanuel@cti.ac.za", FXCollections.observableArrayList(Arrays.asList(new ClassTime("A0001", 3, 1, 2), new ClassTime("A0002", 4, 7, 8))), getFiles2()), FXCollections.observableArrayList(Arrays.asList(new Result("Continuous assessment", 80D, 100D, 0.1), new Result("Semester Test", 80D, 100D, 0.2), new Result("Assignment", 80D, 100D, 0.2), new Result("Examination", 80D, 100D, 0.5)))),
                new ClassAndResult(new StudentClass("Internet Programming and e-Commerce", "C_ITEC301", "Tem", "M", "Dv005", "tem@cti.ac.za", FXCollections.observableArrayList(Arrays.asList(new ClassTime("A0002", 3, 3, 4))), getFiles1()), FXCollections.observableArrayList(Arrays.asList(new Result("Continuous assessment", 80D, 100D, 0.1), new Result("Semester Test", 80D, 100D, 0.2), new Result("Assignment", 80D, 100D, 0.2), new Result("Examination", 80D, 100D, 0.5))))
        )));
    }

    //TODO remove
    private ObservableList<StudentFile> getFiles1() {
        return FXCollections.observableArrayList(Arrays.asList(new StudentFile(1, "Study Guide.pdf", 1024), new StudentFile(1, "Module Outline.pdf", 231722), new StudentFile(1, "Project Specifications.pdf", 1024)));
    }

    //TODO remove
    private ObservableList<StudentFile> getFiles2() {
        return FXCollections.observableArrayList(Arrays.asList(new StudentFile(2, "Study Guide.pdf", 1024), new StudentFile(2, "Module Outline.pdf", 1024), new StudentFile(2, "Project Specifications.pdf", 1024), new StudentFile(2, "Assessment 1.pdf", 1024)));
    }

}
