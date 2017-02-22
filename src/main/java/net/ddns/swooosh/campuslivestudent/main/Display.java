package net.ddns.swooosh.campuslivestudent.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

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
    private Text classesText;
    private ComboBox<String> classesComboBox;
    private Text selectedClassText;
    private Button selectedClassResultsButton;
    private Button selectedClassContactLecturerButton;
    private HBox selectedClassActionsPane;
    private ListView<?> selectedClassFilesListView;
    private VBox selectedClassPane;
    private VBox classPane;

    private TabPane tabPane;
    private StackPane headingPane;
    private VBox studentPane;
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

        //Setup tooltip delays
        try {
            Class<?> clazz = Tooltip.class.getDeclaredClasses()[0];
            Constructor<?> constructor = clazz.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
            constructor.setAccessible(true);
            Object tooltipBehavior = constructor.newInstance(new Duration(50), new Duration(5000), new Duration(50), false);
            Field fieldBehavior = Tooltip.class.getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            fieldBehavior.set(Tooltip.class, tooltipBehavior);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

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
                " -fx-background-position: 5, 5, 5, 5;");
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
            studentNumberTextField.setBorder(null);
            passwordField.setBorder(null);
           if (studentNumberTextField.getText().length() < 11) { //TODO min length login
               studentNumberTextField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
               Tooltip studentNumberShortTooltip = new Tooltip("Student number too short");
               studentNumberShortTooltip.setStyle(" -fx-background-color: red;" +
                       " -fx-background-radius: 10, 10, 10, 10;" +
                       " -fx-text-fill: white;" +
                       " -fx-font-family: \"Verdana\";");
               studentNumberTextField.setTooltip(studentNumberShortTooltip);
           } else if (passwordField.getText().length() < 5) { //TODO min length password
               passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
               Tooltip passwordShortTooltip = new Tooltip("Password too short");
               passwordShortTooltip.setStyle(" -fx-background-color: red;" +
                       " -fx-background-radius: 10, 10, 10, 10;" +
                       " -fx-text-fill: white;" +
                       " -fx-font-family: \"Verdana\";");
               passwordField.setTooltip(passwordShortTooltip);
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
                               contentPane.getChildren().addAll(backgroundPane, studentPane);
                           });
                       } else {
                           Platform.runLater(() -> {
                               passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
                               Tooltip incorrectLoginTooltip = new Tooltip("Incorrect login details");
                               incorrectLoginTooltip.setStyle(" -fx-background-color: red;" +
                                       " -fx-background-radius: 10, 10, 10, 10;" +
                                       " -fx-text-fill: white;" +
                                       " -fx-font-family: \"Verdana\";");
                               passwordField.setTooltip(incorrectLoginTooltip);
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

        //Setup classes pane
        classesText = new Text("My Classes");
        classesText.setStyle("-fx-font-size: 42pt;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        classesComboBox = new ComboBox<>(FXCollections.observableList(Arrays.asList(new String[]{"Software Development Project 3", "Advanced Database", "Object Orientated System Analysis and Design"})));
        classesComboBox.getSelectionModel().select(0);
        classesComboBox.setOnAction(e -> {
            selectedClassText.setText(classesComboBox.getSelectionModel().getSelectedItem());
        });
        selectedClassText = new Text(classesComboBox.getSelectionModel().getSelectedItem());
        selectedClassText.setStyle("-fx-font-size: 28pt;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        selectedClassResultsButton = new Button("R");
        selectedClassResultsButton.setStyle(" -fx-background-radius: 100, 100, 100, 100;" +
                " -fx-background-color: #4287a7;");
        selectedClassResultsButton.setMinSize(75, 75);
        selectedClassContactLecturerButton = new Button("C");
        selectedClassContactLecturerButton.setStyle(" -fx-background-radius: 100, 100, 100, 100;" +
                " -fx-background-color: #4287a7;");
        selectedClassContactLecturerButton.setMinSize(75, 75);
        selectedClassActionsPane = new HBox(selectedClassResultsButton, selectedClassContactLecturerButton);
        selectedClassActionsPane.setAlignment(Pos.CENTER);
        selectedClassActionsPane.setSpacing(50);
        selectedClassFilesListView = new ListView<>(FXCollections.observableList(Arrays.asList(new String[]{"Study Guide", "Module Outline", "Assignment Specification"})));
        selectedClassFilesListView.setStyle("-fx-background-color: rgba(66, 135, 167, .7);" +
                " -fx-control-inner-background: transparent;");
        selectedClassPane = new VBox(selectedClassText, selectedClassActionsPane, selectedClassFilesListView);
        selectedClassPane.setAlignment(Pos.CENTER);
        selectedClassPane.setSpacing(10);
        selectedClassPane.setPadding(new Insets(25, 250, 50, 250));
        VBox.setVgrow(selectedClassFilesListView, Priority.ALWAYS);
        classPane = new VBox(classesText, classesComboBox, selectedClassPane);
        classPane.setAlignment(Pos.CENTER);
        classPane.setSpacing(10);
        classPane.setPadding(new Insets(25));
        VBox.setVgrow(selectedClassPane, Priority.ALWAYS);

        //Setup tab pane
        Tab classesTab = new Tab("My Classes", classPane);
        tabPane = new TabPane(classesTab);

        //Setup heading pane
        headingPane = new StackPane();
        headingPane.setStyle("-fx-background-image: url(\"CampusLiveBanner.png\");" +
                " -fx-background-size: auto 100%;" +
                " -fx-background-position: center;" +
                " -fx-background-repeat: no-repeat;" +
                " -fx-background-color: #4287a7;" +
                " -fx-accent: white");
        headingPane.setMaxHeight(150);
        headingPane.setMinHeight(100);

        //Setup student pane
        studentPane = new VBox(headingPane, tabPane);
        VBox.setVgrow(headingPane, Priority.ALWAYS);

        //Setup background pane
        backgroundPane = new StackPane();
        backgroundPane.setStyle("-fx-background-image: url(\"Background.png\");" +
                " -fx-background-size: auto 100%;" +
                " -fx-background-position: center;" +
                " -fx-background-repeat: no-repeat;" +
                " -fx-background-color: white;" +
                " -fx-accent: white");
        backgroundPane.setEffect(new GaussianBlur(10000));

        //Setup content pane
        contentPane = new StackPane(backgroundPane, loginPane); //TODO loginpane
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
