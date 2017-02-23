package net.ddns.swooosh.campuslivestudent.models;

public class StudentClass {

    private int classID;
    private String moduleName;
    private String moduleNumber;
    private String roomNumber;
    private String lecturerName;
    private int dayOfWeek;
    private int startSlot;
    private int endSlot;

    public StudentClass(int classID, String moduleName, String moduleNumber, String roomNumber, String lecturerName, int dayOfWeek, int startSlot, int endSlot) {
        this.classID = classID;
        this.moduleName = moduleName;
        this.moduleNumber = moduleNumber;
        this.roomNumber = roomNumber;
        this.lecturerName = lecturerName;
        this.dayOfWeek = dayOfWeek;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
    }

    public int getClassID() {
        return classID;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleNumber() {
        return moduleNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getStartSlot() {
        return startSlot;
    }

    public int getEndSlot() {
        return endSlot;
    }

    public String toString() {
        return moduleName;
    }
}
