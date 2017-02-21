package net.ddns.swooosh.campuslivestudent.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Display extends Application{

    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private volatile BooleanProperty running = new SimpleBooleanProperty(false);
    private volatile BooleanProperty result = new SimpleBooleanProperty(false);
    private Stage stage;
    private ImageView loginLogoImageView;
    private TextField studentNumberTextField;
    private PasswordField passwordField;
    private Hyperlink forgotPasswordHyperlink;
    private ProgressIndicator waitIndicator;
    private Button loginButton;
    private VBox loginPane;

    private StackPane backgroundPane;
    private StackPane contentPane;
    private Scene scene;

    public Display() {

    }

    public void start(Stage primaryStage) throws Exception {

        //Setup stage
        stage = primaryStage;
        stage.setTitle("Campus Live Student");
        stage.getIcons().addAll(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        stage.setMaximized(true);

        //Setup Login pane
        loginLogoImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        loginLogoImageView.setFitHeight(250);
        loginLogoImageView.setFitWidth(250);
        studentNumberTextField = new TextField();
        studentNumberTextField.setPromptText("Student Number");
        studentNumberTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -40%);" +
                " -fx-font-size: 12pt;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , black , 5, 0.0 , 0 , 1 );" +
                " -fx-background-radius: 20, 20, 20, 20;" +
                " -fx-background-image: url(\"User.png\");" +
                " -fx-background-repeat: no-repeat;" +
                " -fx-background-size: auto 50%;" +
                " -fx-background-position: 5, 5, 5, 5");
        studentNumberTextField.setMaxWidth(250);
        studentNumberTextField.setMinHeight(40);
        studentNumberTextField.setPadding(new Insets(5, 5, 5, 30));
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -40%);" +
                " -fx-font-size: 12pt;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , black , 5, 0.0 , 0 , 1 );" +
                " -fx-background-radius: 20, 20, 20, 20;" +
                " -fx-background-image: url(\"Password.png\");" +
                " -fx-background-repeat: no-repeat;" +
                " -fx-background-size: auto 50%;" +
                " -fx-background-position: 5, 5, 5, 15");
        passwordField.setMaxWidth(250);
        passwordField.setMinHeight(40);
        passwordField.setPadding(new Insets(5, 5, 5, 30));
        loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 16pt;" +
                " -fx-text-fill: white;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: #4287a7;" +
                " -fx-effect: dropshadow( three-pass-box , black , 5, 0.0 , 0 , 1 );" +
                " -fx-background-radius: 20, 20, 20, 20;");
        loginButton.setMaxWidth(250);
        loginButton.setMinHeight(40);
        loginButton.setPadding(new Insets(2, 2, 2, 2));
        loginButton.setOnAction((ActionEvent e) -> {
           if (studentNumberTextField.getText().length() < 11) { //TODO min length login
               //TODO
           } else if (passwordField.getText().length() < 5) { //TODO min length password
               //TODO
           } else {
               loginPane.getChildren().clear();
               loginPane.getChildren().addAll(loginLogoImageView, waitIndicator);
               running.setValue(true);
               Thread loginThread = new Thread(() -> {
                    result.setValue(connectionHandler.authorise(studentNumberTextField.getText(), passwordField.getText()));
                    running.set(false);
               });
               loginThread.start();
               running.addListener(al -> {
                   if (!running.getValue()) {
                       if (result.getValue()) {
                           Platform.runLater(() -> {
                               contentPane.getChildren().clear();
                               contentPane.getChildren().addAll(backgroundPane);
                           });
                       } else {
                           Platform.runLater(() -> {
                               loginPane.getChildren().clear();
                               loginPane.getChildren().addAll(loginLogoImageView, studentNumberTextField, passwordField, loginButton, forgotPasswordHyperlink);
                               passwordField.clear();
                               passwordField.requestFocus();
                           });
                       }
                   }
               });
           }
        });
        loginButton.setDefaultButton(true);
        forgotPasswordHyperlink = new Hyperlink("Forgot Password?");
        forgotPasswordHyperlink.setFont(new Font("Verdana", 14));
        waitIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        waitIndicator.setPrefSize(200, 200);
        loginPane = new VBox(loginLogoImageView, studentNumberTextField, passwordField, loginButton, forgotPasswordHyperlink);
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setSpacing(15);
        loginPane.setPadding(new Insets(10));
        loginPane.setMaxSize(500, 500);

        //Setup background pane
        backgroundPane = new StackPane();
        backgroundPane.setStyle("-fx-background-image: url(\"Background.png\");" +
                " -fx-background-size: auto 100%;" +
                " -fx-background-position: center;" +
                " -fx-background-repeat: no-repeat;" +
                " -fx-background-color: white;" +
                " -fx-accent: white");
        backgroundPane.setEffect(new GaussianBlur(150));

        //Setup content pane
        contentPane = new StackPane(backgroundPane, loginPane);
        contentPane.setAlignment(Pos.CENTER);
;
        //Setup scene
        scene = new Scene(contentPane);

        //Select and show scene
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
