package net.ddns.swooosh.campuslivestudent.main;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import models.all.ClassLecturer;

public class LecturerBadge extends HBox {

    private Circle lecturerPicture;
    private Text lecturerText;
    private HBox lecturerTextPane;
    private ClassLecturer classLecturer;

    public LecturerBadge() {
        lecturerPicture = new Circle(30);

        lecturerPicture.setStroke(Color.BLACK);
        lecturerPicture.setStrokeWidth(2);
        lecturerText = new Text();
        lecturerText.setStyle("-fx-font-size: 16;" +
                "-fx-fill: white;");
        lecturerTextPane = new HBox(lecturerText);
        lecturerTextPane.setMaxSize(200, 30);
        lecturerTextPane.setMinSize(200, 30);
        lecturerTextPane.setAlignment(Pos.CENTER);
        lecturerTextPane.getStyleClass().add("classLecturer-text-pane");
        getChildren().addAll(lecturerTextPane, lecturerPicture);
        setSpacing(-245);
        setAlignment(Pos.CENTER);
        setStyle("-fx-padding: 0 200 0 0;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.6), 5, 0, 2, 2);");
        setPickOnBounds(false);
    }

    public void setClassLecturer(ClassLecturer classLecturer) {
        this.classLecturer = classLecturer;
        lecturerPicture.setFill(new ImagePattern(classLecturer.getLecturerImage()));
        lecturerText.setText(classLecturer.getFirstName() + " " + classLecturer.getLastName());
    }

    public String getLecturerName() {
        return lecturerText.getText();
    }

    public String getLecturerEmail() {
        return classLecturer.getEmail();
    }
}
