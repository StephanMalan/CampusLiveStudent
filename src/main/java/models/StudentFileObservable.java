package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.ddns.swooosh.campuslivestudent.main.ConnectionHandler;

public class StudentFileObservable {

    private StudentFile studentFile;
    private DoubleProperty progress;
    private IntegerProperty type;

    public StudentFileObservable(StudentFile studentFile) {
        this.studentFile = studentFile;
        progress = new SimpleDoubleProperty();
        type = new SimpleIntegerProperty();
    }

    public StudentFile getStudentFile() {
        return studentFile;
    }

    public DoubleProperty progressProperty() {
        progress.set(0);
        if (studentFile.getFileDownloader() != null) {
            progress.set(((ConnectionHandler.FileDownloader) studentFile.getFileDownloader()).progress.get());
        }
        return progress;
    }

    public IntegerProperty typeProperty() {
        type.set(studentFile.getValue());
        return type;
    }
}
