package net.ddns.swooosh.campuslivestudent.main;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import models.all.ClassLecturer;

public class LecturerBadge extends HBox {

    private Circle lecturerPicture;
    private Text lecturerText;
    private VBox lecturerTextPane;
    private ClassLecturer classLecturer;

    public LecturerBadge() {
        lecturerPicture = new Circle(30);
        lecturerPicture.setStroke(Color.BLACK);
        lecturerPicture.setStrokeWidth(2);
        Label lecturerLabel = new Label("Lecturer:               ");
        lecturerLabel.setStyle("-fx-font-size: 12;" +
                "-fx-text-fill: white;");
        lecturerText = new Text();
        lecturerText.setStyle("-fx-font-size: 16;" +
                "-fx-fill: white;");
        lecturerTextPane = new VBox(lecturerLabel, lecturerText);
        lecturerTextPane.setMaxSize(200, 40);
        lecturerTextPane.setMinSize(200, 40);
        lecturerTextPane.setAlignment(Pos.CENTER);
        lecturerTextPane.getStyleClass().add("lecturer-text-pane");
        getChildren().addAll(lecturerTextPane, lecturerPicture);
        setSpacing(-245);
        setAlignment(Pos.CENTER);
        setStyle("-fx-padding: 0 200 0 0;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.6), 5, 0, 2, 2);");
        setPickOnBounds(false);
    }

    public void setClassLecturer(ClassLecturer classLecturer) {
        this.classLecturer = classLecturer;
        if (classLecturer.getLecturerImage() == null) {
            lecturerPicture.setFill(new ImagePattern(new Image(getClass().getClassLoader().getResourceAsStream("DefaultProfile.jpg"))));
        } else {
            lecturerPicture.setFill(new ImagePattern(classLecturer.getLecturerImage()));
        }
        lecturerText.setText(classLecturer.getFirstName() + " " + classLecturer.getLastName());
    }

    public String getLecturerName() {
        return lecturerText.getText();
    }

    public String getLecturerEmail() {
        return classLecturer.getEmail();
    }

    public String getLecturerNumber() {
        return classLecturer.getLecturerID();
    }
}
