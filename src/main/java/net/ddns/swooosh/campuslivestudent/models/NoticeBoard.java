package net.ddns.swooosh.campuslivestudent.models;

public class NoticeBoard {

    private String heading;
    private String desription;

    public NoticeBoard(String heading, String desription) {
        this.heading = heading;
        this.desription = desription;
    }

    public String getHeading() {
        return heading;
    }

    public String getDesription() {
        return desription;
    }
}
