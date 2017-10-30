package net.ddns.swooosh.campuslivestudent.main;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.all.*;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;

public class ConnectionHandler {

    public static final int PORT = 25760;
    public static String LOCAL_ADDRESS = "10.0.0.3"; //TODO
    public static final String INTERNET_ADDRESS = "swooosh.ddns.net";
    public StudentObservable student = new StudentObservable(null);
    public volatile ObservableList<Notice> notices = FXCollections.observableArrayList();
    public volatile ObservableList<Notification> notifications = FXCollections.observableArrayList();
    public volatile ObservableList<ContactDetails> contactDetails = FXCollections.observableArrayList();
    public volatile ObservableList<ImportantDate> importantDates = FXCollections.observableArrayList();
    public volatile ObservableList<String> outputQueue = FXCollections.observableArrayList();
    public volatile ObservableList<Object> inputQueue = FXCollections.observableArrayList();
    public String connectionType = "On Campus";
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Boolean logOut = false;

    public ConnectionHandler() {
        connect();
    }

    //<editor-fold desc="Connection">
    private void connect() {
        if (!connectLocal()) {
            if (!connectInternet()) {
                UserNotification.showErrorMessage("Connection Error", "Failed to connect to CampusLive Servers! (" + LOCAL_ADDRESS + ")\nPlease check your network connection and try again!");
                System.out.println("Exiting..");
                System.exit(0);
            } else {
                connectionType = "Off Campus";
            }
        }
        new InputProcessor().start();
        new OutputProcessor().start();
    }

    private Boolean connectLocal() {
        System.out.println("Trying to connect to local server...");
        try {
            System.setProperty("javax.net.ssl.trustStore", Display.APPLICATION_FOLDER + "/campuslive.store");
            socket = SSLSocketFactory.getDefault().createSocket(LOCAL_ADDRESS, PORT);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Socket is connected");
            return true;
        } catch (Exception ex) {
            System.out.println("Could not connect to local server");
        }
        return false;
    }

    private Boolean connectInternet() {
        System.out.println("Trying to connect to internet server...");
        try {
            System.setProperty("javax.net.ssl.trustStore", "src/main/resources/campuslive.store");
            socket = SSLSocketFactory.getDefault().createSocket();
            socket.connect(new InetSocketAddress(INTERNET_ADDRESS, PORT), 1000);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Socket is connected");
            return true;
        } catch (Exception ex) {
            System.out.println("Could not connect to internet server");
        }
        return false;
    }
    //</editor-fold>

    public String getConnectionType() {
        return connectionType;
    }

    //<editor-fold desc="Commands">
    public Boolean authorise(String studentNumber, String password) {
        if (connectionType.equals("On Campus")) {
            outputQueue.add("saf:" + studentNumber + ":" + password);
            return getStringReply("saf:");
        } else {
            outputQueue.add("san:" + studentNumber + ":" + password);
            return getStringReply("san:");
        }
    }

    public Boolean isLecturerOnline(String lecturerNumber) {
        outputQueue.add("lo:" + lecturerNumber);
        return getStringReply("lo:");
    }

    public Boolean changePassword(String prevPassword, String newPassword) {
        outputQueue.add("cp:" + prevPassword + ":" + newPassword);
        return getStringReply("cp:");
    }

    public Boolean changeDefaultPassword(String newPassword) {
        outputQueue.add("cdp:" + newPassword);
        return getStringReply("cdp:");
    }

    public void forgotPassword(String email) {
        outputQueue.add("fsp:" + email);
    }

    public void sendMessage(String message, String lecturerNumber) {
        outputQueue.add("sm:" + message + ":" + lecturerNumber);
    }

    public boolean isDefaultPassword() {
        outputQueue.add("idp:");
        return getStringReply("idp:");
    }

    public void deleteFile(int classID, String fileName) {
        new File(Display.LOCAL_CACHE + "/" + classID + "/" + fileName).delete();
        updateSavedFiles();
    }

    public void dismissNotification(int notificationID) {
        outputQueue.add("dn:" + notificationID);
    }

    public void logOut() {
        sendData("lgt:");
        logOut = true;
    }

    public void sendData(String data) {
        try {
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
            System.out.println("Sent data: " + data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object getReply() {
        try {
            Object input;
            while ((input = objectInputStream.readObject()) == null) ;
            return input;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (!logOut) {
                System.exit(0);
            }
        }
        return null;
    }

    public void updateSavedFiles() {
        Boolean updated = false;
        for (ClassResultAttendance car : student.getStudent().getClassResultAttendances()) {
            for (ClassFile cf : car.getStudentClass().getFiles()) {
                File f;
                if ((f = new File(Display.LOCAL_CACHE + "/" + cf.getClassID() + "/" + cf.getFileName())).exists() && f.length() == cf.getFileLength()) {
                    if (cf.getValue() != 1) {
                        cf.setValue(1);
                        updated = true;
                    }
                } else if (cf.getValue() == 1) {
                    cf.setValue(0);
                    updated = true;

                }
            }
            try {
                File classFolder = new File(Display.LOCAL_CACHE + "/" + car.getStudentClass().getClassID());
                for (File file : classFolder.listFiles()) {
                    Boolean found = false;
                    for (ClassFile cf : car.getStudentClass().getFiles()) {
                        if (cf.getFileName().equals(file.getName()) && cf.getFileLength() == file.length()) {
                            found = true;
                        }
                    }
                    if (!found) {
                        Files.delete(file.toPath());
                        System.out.println("Deleted file: " + file.getName());
                    }
                }
            } catch (Exception ex) {
            }
        }
        if (updated) {
            Platform.runLater(() -> student.update());
            System.out.println("Files Updated");
        }
    }

    public Boolean getStringReply(String startsWith) {
        Boolean result;
        Object objectToRemove;
        ReturnResult:
        while (true) {
            for (int i = 0; i < inputQueue.size(); i++) {
                Object object = inputQueue.get(i);
                if (object instanceof String) {
                    String in = (String) object;
                    if (in.startsWith(startsWith)) {
                        objectToRemove = object;
                        result = in.charAt(startsWith.length()) == 'y';
                        break ReturnResult;
                    }
                }
            }
        }
        inputQueue.remove(objectToRemove);
        return result;
    }

    public List<ContactDetails> getContactDetails() {
        return contactDetails;
    }

    public Boolean studentInitialized() {
        return student.getStudent() != null;
    }

    private class InputProcessor extends Thread {
        public void run() {
            while (!logOut) {
                Object input;
                if ((input = getReply()) != null) {
                    if (input instanceof Student) {
                        student.setStudent((Student) input);
                        updateSavedFiles();
                        student.update();
                        System.out.println("Updated Student");
                    } else if (input instanceof List<?>) {
                        List list = (List) input;
                        if (!list.isEmpty() && list.get(0) instanceof Notice) {
                            notices.clear();
                            if (!((Notice) list.get(0)).getHeading().equals("NoNotice")) {
                                notices.addAll(list);
                            }
                            System.out.println("Updated Notices");
                        } else if (!list.isEmpty() && list.get(0) instanceof Notification) {
                            notifications.clear();
                            if (!((Notification) list.get(0)).getHeading().equals("NoNotification")) {
                                notifications.addAll(list);
                            }
                            System.out.println("hello? " + notifications.size());
                            System.out.println("Updated Notifications (" + notifications.size() + ")");
                        } else if (!list.isEmpty() && list.get(0) instanceof ContactDetails) {
                            contactDetails.clear();
                            if (!((ContactDetails) list.get(0)).getName().equals("NoContactDetails")) {
                                contactDetails.addAll(list);
                            }
                            System.out.println("Updated Contact Details");
                        } else if (!list.isEmpty() && list.get(0) instanceof ImportantDate) {
                            importantDates.clear();
                            if (!((ImportantDate) list.get(0)).getDate().equals("NoImportantDate")) {
                                importantDates.addAll(list);
                            }
                            System.out.println("Updated Important Dates");
                        }
                    } else {
                        inputQueue.add(input);
                    }
                }
            }
        }
    }

    private class OutputProcessor extends Thread {
        public void run() {
            while (true) {
                if (!outputQueue.isEmpty()) {
                    sendData(outputQueue.get(0));
                    outputQueue.remove(0);
                }
            }
        }
    }

    public class FileDownloader extends Thread {

        public volatile IntegerProperty size;
        public volatile DoubleProperty progress;
        ClassFile file;
        byte[] bytes;

        public FileDownloader(ClassFile file) {
            this.file = file;
            bytes = new byte[file.getFileLength()];
            size = new SimpleIntegerProperty(0);
            progress = new SimpleDoubleProperty(0);
        }

        @Override
        public void run() {
            outputQueue.add("gf:" + file.getClassID() + ":" + file.getFileName());
            Done:
            while (true) {
                FilePart filePartToRemove = null;
                BreakSearch:
                for (int i = inputQueue.size() - 1; i > -1; i--) {
                    try {
                        Object object = inputQueue.get(i);
                        if (object instanceof FilePart) {
                            FilePart filePart = (FilePart) object;
                            if (filePart.getClassID() == file.getClassID() && filePart.getFileName().equals(file.getFileName())) {
                                filePartToRemove = filePart;
                                break BreakSearch;
                            }
                        }
                    } catch (IndexOutOfBoundsException ex) {
                    }
                }
                if (filePartToRemove != null) {
                    for (int i = 0; i < filePartToRemove.getFileBytes().length; i++) {
                        bytes[size.get() + i] = filePartToRemove.getFileBytes()[i];
                    }
                    size.set(size.get() + filePartToRemove.getFileBytes().length);
                    progress.set(1D * size.get() / bytes.length);
                    Platform.runLater(() -> student.update());
                    inputQueue.remove(filePartToRemove);
                }
                if (size.get() == file.getFileLength()) {
                    System.out.println("File successfully downloaded!");
                    File f = new File(Display.LOCAL_CACHE + "/" + file.getClassID() + "/" + file.getFileName());
                    f.getParentFile().mkdirs();
                    try {
                        Files.write(f.toPath(), bytes);
                        updateSavedFiles();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break Done;
                }
            }
        }
    }

}
