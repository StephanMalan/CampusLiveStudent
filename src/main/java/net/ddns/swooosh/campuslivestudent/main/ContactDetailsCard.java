package net.ddns.swooosh.campuslivestudent.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Window;
import models.ContactDetails;

public class ContactDetailsCard extends HBox {

    public ContactDetailsCard(Window parent, ContactDetails contactDetails, String studentName, String studentEmail) {
        Circle contactImage = new Circle(30);
        contactImage.setFill(new ImagePattern(contactDetails.getImage()));
        contactImage.setStroke(Color.BLACK);
        contactImage.setStrokeWidth(2);
        Text contactName = new Text(contactDetails.getName());
        contactName.setStyle("-fx-font-size: 22");
        Text contactPosition = new Text(contactDetails.getPosition());
        VBox contactMainPane = new VBox(contactName, contactPosition);
        contactMainPane.setAlignment(Pos.CENTER_LEFT);
        contactMainPane.setMinWidth(500);
        contactMainPane.setMaxWidth(500);
        Text contactNumber = new Text(contactDetails.getContactNumber());
        //Text contactEmail = new Text(contactDetails.getEmail());
        Hyperlink contactEmail = new Hyperlink(contactDetails.getEmail());
        contactEmail.setOnAction(e -> {
            new EmailDialog(parent, contactDetails.getName(), contactDetails.getEmail(), studentName, studentEmail).showDialog();
            contactEmail.setVisited(false);
        });
        contactEmail.setFocusTraversable(false);
        contactEmail.setStyle("-fx-effect: null");
        setStyle("-fx-background-color: white;" +
                "-fx-border-color: black;" +
                "-fx-min-width: 1000;" +
                "-fx-max-width: 1000");
        setSpacing(25);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(5));
        getChildren().addAll(contactImage, contactMainPane, contactNumber, contactEmail);
    }

}
