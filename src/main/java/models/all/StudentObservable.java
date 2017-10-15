package models.all;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class StudentObservable {

    private Student student;
    public volatile BooleanProperty update;

    public StudentObservable(Student student) {
        this.student = student;
        update = new SimpleBooleanProperty(false);
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void update() {
        update.setValue(false);
        update.setValue(true);
    }
}
