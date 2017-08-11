package net.ddns.swooosh.campuslivestudent.main;

import com.jfoenix.controls.JFXToggleButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Window;

public class SettingsDialog extends CustomDialogSkin{

    public SettingsDialog(Window parent) {
        initOwner(parent);
        Text settingsText = new Text("Settings");
        settingsText.getStyleClass().add("heading-text");
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.getStyleClass().add("settingsButton");
        changePasswordButton.setOnAction(e -> {
            new ChangePasswordDialog(parent, false).showDialog();
        });
        Button aboutButton = new Button("About");
        aboutButton.getStyleClass().add("settingsButton");
        Button helpButton = new Button("Help");
        helpButton.getStyleClass().add("settingsButton");
        JFXToggleButton toggleButton = new JFXToggleButton();
        toggleButton.setText("Enable Animations");
        toggleButton.setToggleColor(Color.GREEN);
        toggleButton.setUnToggleColor(Color.RED);
        toggleButton.setSelected(true);
        Display.enableAnimations.bind(toggleButton.selectedProperty());
        Button animationsButton = new Button();
        animationsButton.setDisable(true);
        animationsButton.setGraphic(toggleButton);
        animationsButton.getStyleClass().add("settingsButton");
        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("settingsButton");
        closeButton.setOnAction(e -> {
            closeAnimation();
        });
        VBox settingsButtonsPane = new VBox(changePasswordButton, aboutButton, helpButton, toggleButton, closeButton);
        settingsButtonsPane.setAlignment(Pos.TOP_CENTER);
        settingsButtonsPane.setSpacing(25);
        VBox settingsInnerPane = new VBox(settingsText, settingsButtonsPane);
        settingsInnerPane.getChildren().addAll();
        settingsInnerPane.setSpacing(25);
        settingsInnerPane.setPadding(new Insets(0, 0, 15, 0));
        settingsInnerPane.setAlignment(Pos.CENTER);
        settingsInnerPane.setStyle("-fx-background-color: #007FA3");
        settingsInnerPane.setMaxSize(450, 450);
        settingsInnerPane.setMinSize(450, 450);
        VBox settingsPane = new VBox(settingsInnerPane);
        setWidth(450);
        settingsPane.setAlignment(Pos.CENTER);
        getDialogPane().setContent(settingsPane);
    }

}
