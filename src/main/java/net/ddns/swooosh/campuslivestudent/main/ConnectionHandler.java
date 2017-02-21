package net.ddns.swooosh.campuslivestudent.main;

public class ConnectionHandler {

    public ConnectionHandler() {

    }

    public Boolean authorise(String studentNumber, String password) {
        Boolean result = studentNumber.equals("DV2015-0073") && password.equals("password");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

}
