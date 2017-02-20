package net.ddns.swooosh.campuslivestudent.main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Display extends Application{

    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private Stage stage;
    private Text loginHeadingText;
    private TextField studentNumberTextField;
    private PasswordField passwordField;
    private Hyperlink forgotPasswordHyperlink;
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
        stage.getIcons().addAll(new Image(getClass().getClassLoader().getResourceAsStream("resources/CLLogo.png")));
        stage.setMaximized(true);

        //Setup Login pane
        loginHeadingText = new Text("Login");
        loginHeadingText.setStyle("-fx-font-size: 48pt; -fx-text-fill: black; -fx-font-family: \"Verdana\"; -fx-font-weight: bold; -fx-background-color: linear-gradient(#ffffff, #d3d3d3); -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        studentNumberTextField = new TextField();
        studentNumberTextField.setPromptText("Student Number");
        studentNumberTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -40%); -fx-font-size: 12pt; -fx-text-fill: black; -fx-font-family: \"Arial\"; -fx-font-weight: bold; -fx-background-color: linear-gradient(#ffffff, #d3d3d3); -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -40%); -fx-font-size: 12pt; -fx-text-fill: black; -fx-font-family: \"Arial\"; -fx-font-weight: bold; -fx-background-color: linear-gradient(#ffffff, #d3d3d3); -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        forgotPasswordHyperlink = new Hyperlink("Forgot Password");
        forgotPasswordHyperlink.setFont(new Font(8));
        loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 12pt; -fx-text-fill: black; -fx-font-family: \"Arial\"; -fx-font-weight: bold; -fx-background-color: linear-gradient(#ffffff, #d3d3d3); -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        loginPane = new VBox(loginHeadingText, studentNumberTextField, passwordField, forgotPasswordHyperlink, loginButton);
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setSpacing(15);
        loginPane.setPadding(new Insets(10));

        //Setup background pane
        backgroundPane = new StackPane();
        backgroundPane.setStyle("-fx-background-image: url(\"resources/Background.png\"); -fx-background-size: auto 100%; -fx-background-position: center; -fx-background-repeat: no-repeat;-fx-background-color: white;-fx-accent: white");
        backgroundPane.setEffect(new GaussianBlur(25));

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
