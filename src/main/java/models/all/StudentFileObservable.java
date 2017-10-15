package models.all;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import net.ddns.swooosh.campuslivestudent.main.ConnectionHandler;

public class StudentFileObservable {

    private ClassFile classFile;
    private DoubleProperty progress;
    private IntegerProperty type;

    public StudentFileObservable(ClassFile classFile) {
        this.classFile = classFile;
        progress = new SimpleDoubleProperty();
        type = new SimpleIntegerProperty();
    }

    public ClassFile getClassFile() {
        return classFile;
    }

    public DoubleProperty progressProperty() {
        progress.set(0);
        if (classFile.getFileDownloader() != null) {
            progress.set(((ConnectionHandler.FileDownloader) classFile.getFileDownloader()).progress.get());
        }
        return progress;
    }

    public IntegerProperty typeProperty() {
        type.set(classFile.getValue());
        return type;
    }
}
