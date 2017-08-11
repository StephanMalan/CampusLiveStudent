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

    private VBox contentPane;

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
        }
        HBox buttonPane = new HBox((JFXButton[])actionButtons.toArray());
        buttonPane.setAlignment(Pos.CENTER);
        contentPane = new VBox(headingText, bodyText, buttonPane);
        contentPane.setMinWidth(500);
        contentPane.setMaxWidth(500);
        setWidth(500);
        contentPane.setStyle("-fx-background-color: white");
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setSpacing(15);
        contentPane.setPadding(new Insets(15));
        getDialogPane().setContent(contentPane);
    }


}
