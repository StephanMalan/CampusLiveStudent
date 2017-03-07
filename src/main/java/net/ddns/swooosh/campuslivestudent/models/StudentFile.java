package net.ddns.swooosh.campuslivestudent.models;

public class StudentFile {

    private int classID;
    private String fileName;
    private int fileLength;

    public StudentFile(int classID, String fileName, int fileLength) {
        this.classID = classID;
        this.fileName = fileName;
        this.fileLength = fileLength;
    }

    public int getClassID() {
        return classID;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileLength() {
        return fileLength;
    }
}
