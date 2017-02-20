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
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Display extends Application{

    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private Stage stage;
    private ImageView loginLogoImageView;
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
        stage.getIcons().addAll(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        stage.setMaximized(true);

        //Setup Login pane
        loginLogoImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        loginLogoImageView.setFitHeight(200);
        loginLogoImageView.setFitWidth(200);
        studentNumberTextField = new TextField();
        studentNumberTextField.setPromptText("Student Number");
        studentNumberTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -40%);" +
                " -fx-font-size: 12pt;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: \"Arial\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );" +
                " -fx-background-radius: 15, 15, 15, 15;" +
                " -fx-background-image: url(\"User.png\");" +
                " -fx-background-repeat: no-repeat;" +
                " -fx-background-position: left center;" +
                " -fx-background-size: auto 100%;");
        studentNumberTextField.setMaxWidth(250);
        studentNumberTextField.setPadding(new Insets(5, 5, 5, 50));
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -40%);" +
                " -fx-font-size: 12pt;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: \"Arial\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );" +
                " -fx-background-radius: 15, 15, 15, 15;" +
                " -fx-background-image: url(\"Password.png\");" +
                " -fx-background-repeat: no-repeat;" +
                " -fx-background-position: left center;" +
                " -fx-background-size: auto 100%;");
        passwordField.setMaxWidth(250);
        passwordField.setPadding(new Insets(5, 5, 5, 50));
        loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 14pt; -fx-text-fill: white; -fx-font-family: \"Arial\"; -fx-font-weight: bold; -fx-background-color: #4287a7; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 ); -fx-background-radius: 15, 15, 15, 15;");
        loginButton.setMaxWidth(250);
        forgotPasswordHyperlink = new Hyperlink("Forgot Password?");
        forgotPasswordHyperlink.setFont(new Font(16));
        loginPane = new VBox(loginLogoImageView, studentNumberTextField, passwordField, loginButton, forgotPasswordHyperlink);
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setSpacing(15);
        loginPane.setPadding(new Insets(10));
        loginPane.setMaxSize(500, 500);
        //loginPane.setStyle("-fx-border-color: black;  -fx-background-color: white");

        //Setup background pane
        backgroundPane = new StackPane();
        backgroundPane.setStyle("-fx-background-image: url(\"Background.png\"); -fx-background-size: auto 100%; -fx-background-position: center; -fx-background-repeat: no-repeat;-fx-background-color: white;-fx-accent: white");
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
