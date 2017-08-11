package net.ddns.swooosh.campuslivestudent.main;

import com.jfoenix.controls.*;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.ClassFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

public class UserNotification {


    public static void showErrorMessage(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static void showMessage(Window parent, String heading, String message) {
        new CustomDialog(parent, heading, message, new JFXButton("Ok"));
    }

    public static String getText(String title, String message) {
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setContentText(message);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public static void showFileDetails(Window parent, ClassFile classFile) {
        CustomDialog customDialog = new CustomDialog(parent, Display.getFileNameWithoutExtension(classFile.getFileName()), "Extension: " + Display.getFileExtension(classFile.getFileName()) + "\nSize          : " + (classFile.getFileLength() / 1024) + "kB", new JFXButton("Ok"));
        customDialog.showDialog();
    }

    public static Boolean confirmationDialog(Window parent, String heading, String body) {
        CustomDialog customDialog = new CustomDialog(parent, heading, body, new JFXButton("Yes"), new JFXButton("Cancel"));
        return customDialog.showDialog() == 1;
    }

    public static int showLecturerContactMethod(Window parent) {
        CustomDialog customDialog = new CustomDialog(parent, "Contact Lecturer", "Do you want to contact lecturer by email or directly?", new JFXButton("Email"), new JFXButton("Direct Message"), new JFXButton("Cancel"));
        return customDialog.showDialog();
    }

}
