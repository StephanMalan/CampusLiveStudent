package net.ddns.swooosh.campuslivestudent.main;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class UserNotification {

    public static final int EMAIL_OPTION = 1;
    public static final int DIRECT_OPTION = 2;

    public static void showErrorMessage(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static void showMessage(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(header);
        alert.showAndWait();
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

    public static int showLecturerContactMethod() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Contact lecturer");
        alert.setHeaderText("Do you want to contact lecturer by email or directly?");
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(new Image(UserNotification.class.getClassLoader().getResourceAsStream("CLLogo.png")));
        ButtonType emailButtonType = new ButtonType("Email");
        ButtonType directButtonType = new ButtonType("Direct");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(emailButtonType, directButtonType, ButtonType.CLOSE);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == emailButtonType) {
            return 1;
        } else if (result.isPresent() && result.get() == directButtonType) {
            return 2;
        } else {
            return 0;
        }
    }

}
