package models;

import java.io.Serializable;
import java.util.List;

public class StudentClass implements Serializable {

    private String moduleName;
    private String moduleNumber;
    private Lecturer lecturer;
    private List<ClassTime> classTimes;
    private List<ClassFile> files;

    public StudentClass(String moduleName, String moduleNumber, Lecturer lecturer, List<ClassTime> classTimes, List<ClassFile> files) {
        this.moduleName = moduleName;
        this.moduleNumber = moduleNumber;
        this.lecturer = lecturer;
        this.classTimes = classTimes;
        this.files = files;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleNumber() {
        return moduleNumber;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public List<ClassTime> getClassTimes() {
        return classTimes;
    }

    public List<ClassFile> getFiles() {
        return files;
    }
}
