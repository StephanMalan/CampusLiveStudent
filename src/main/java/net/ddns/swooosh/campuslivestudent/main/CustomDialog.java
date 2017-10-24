package net.ddns.swooosh.campuslivestudent.main;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

public class CustomDialog extends CustomDialogSkin {

    public CustomDialog(Window parent, String heading, String body, JFXButton... buttons) {
        initOwner(parent);
        Text headingText = new Text(heading);
        headingText.getStyleClass().add("custom-dialog-heading");
        Text bodyText = new Text(body);
        bodyText.getStyleClass().add("custom-dialog-body");
        List<JFXButton> actionButtons = Arrays.asList(buttons);
        for (JFXButton button : actionButtons) {
            button.setOnAction(e -> {
                setCustomResult(actionButtons.indexOf(e.getSource()) + 1);
                closeAnimation();
            });
            button.getStyleClass().add("dialog-button");
        }
        HBox buttonPane = new HBox((JFXButton[])actionButtons.toArray());
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setSpacing(15);
        VBox innerPane = new VBox(headingText, bodyText, buttonPane);
        innerPane.setPadding(new Insets(20, 50, 20, 50));
        innerPane.setSpacing(20);
        innerPane.setMinWidth(600);
        innerPane.setMaxWidth(600);
        innerPane.setAlignment(Pos.CENTER);
        innerPane.setStyle("-fx-background-color: #007FA3;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;");
        VBox contentPane = new VBox(innerPane);
        contentPane.setAlignment(Pos.CENTER);
        setWidth(600);
        getDialogPane().setContent(contentPane);
    }


}
