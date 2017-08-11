package net.ddns.swooosh.campuslivestudent.main;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Window;

public class ChangePasswordDialog extends CustomDialogSkin {

    public ChangePasswordDialog(Window parent, Boolean defaultPassword) {
        initOwner(parent);
        VBox contentPane = new VBox();
        Text changePasswordHeading = new Text("Change Password");
        changePasswordHeading.setStyle("-fx-font-size: 22");
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        JFXButton changeButton = new JFXButton("Change");
        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.setOnAction(e -> closeAnimation());
        HBox buttonPane = new HBox(changeButton);
        if (!defaultPassword) {
            buttonPane.getChildren().add(cancelButton);
            contentPane.getChildren().addAll(changePasswordHeading, currentPasswordField, newPasswordField, buttonPane);
        } else {
            contentPane.getChildren().addAll(changePasswordHeading, newPasswordField, buttonPane);
        }
        contentPane.setPadding(new Insets(20, 50, 20, 50));
        contentPane.setSpacing(15);
        contentPane.setMinWidth(600);
        contentPane.setMaxWidth(600);
        setWidth(600);
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setStyle("-fx-background-color: #D4EAE4");
        getDialogPane().setContent(contentPane);
    }

}
