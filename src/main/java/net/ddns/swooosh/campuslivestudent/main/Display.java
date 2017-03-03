package net.ddns.swooosh.campuslivestudent.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import net.ddns.swooosh.campuslivestudent.models.StudentClass;
import net.ddns.swooosh.campuslivestudent.models.StudentFiles;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

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
    private Text selectedClassText;
    private Button selectedClassResultsButton;
    private ComboBox<StudentClass> selectedClassComboBox;
    private Button selectedClassContactLecturerButton;
    private HBox selectedClassActionsPane;
    private ListView<StudentFiles> selectedClassFilesListView;
    private VBox selectedClassPane;
    private Text timetableText;
    private GridPane timetableGridPane;
    private VBox timetablePane;
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
        stage.setTitle("Campus Live Student (On-Campus)");
        stage.getIcons().addAll(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        stage.setMaxHeight(1080);
        stage.setMaxWidth(1920);
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
        //TODO warning for caps lock
        loginLogoImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        loginLogoImageView.setFitHeight(200);
        loginLogoImageView.setFitWidth(200);
        studentNumberTextField = new TextField();
        studentNumberTextField.setPromptText("Student Number");
        studentNumberTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -45%);" +
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
        passwordField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -45%);" +
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
        loginPane.setSpacing(20);
        loginPane.setPadding(new Insets(10));
        loginPane.setMaxSize(500, 500);

        //Setup selected class pane
        selectedClassText = new Text();
        selectedClassText.setStyle("-fx-font-size: 20pt;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        selectedClassComboBox = new ComboBox<>(FXCollections.observableList(connectionHandler.getClasses()));
        selectedClassComboBox.setStyle(" -fx-background-radius: 5;" +
                " -fx-background-color: transparent;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-size: 18;");
        selectedClassComboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
            selectedClassText.setText(selectedClassComboBox.getSelectionModel().getSelectedItem().toString());
        });
        selectedClassComboBox.getSelectionModel().select(0);
        selectedClassComboBox.setMinSize(50, 50);
        selectedClassComboBox.setMaxSize(50, 50);
        selectedClassResultsButton = new Button("My Results");
        selectedClassResultsButton.setStyle(" -fx-background-radius: 25;" +
                " -fx-border-radius: 25;" +
                " -fx-border-width: 2;" +
                " -fx-background-color: white;" +
                " -fx-border-color: black;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-weight: bold;" +
                " -fx-font-size: 22;");
        selectedClassResultsButton.setMinSize(250, 50);
        selectedClassContactLecturerButton = new Button("Contact Lecturer");
        selectedClassContactLecturerButton.setStyle(" -fx-background-radius: 25;" +
                " -fx-border-radius: 25;" +
                " -fx-border-width: 2;" +
                " -fx-background-color: white;" +
                " -fx-border-color: black;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-weight: bold;" +
                " -fx-font-size: 22;");
        selectedClassContactLecturerButton.setMinSize(250, 50);
        selectedClassActionsPane = new HBox(selectedClassResultsButton, selectedClassContactLecturerButton);
        selectedClassActionsPane.setAlignment(Pos.CENTER);
        selectedClassActionsPane.setSpacing(50);
        selectedClassFilesListView = new ListView<>(FXCollections.observableList(Arrays.asList(new StudentFiles(1, 1, "Study Guide", 48), new StudentFiles(2, 1, "Assignment Specification", 48), new StudentFiles(3, 1, "Module Outline", 48))));
        selectedClassFilesListView.setStyle("-fx-background-color: rgba(66, 135, 167, .7);" +
                " -fx-control-inner-background: transparent;" +
                " -fx-background-radius: 15, 15, 15, 15;" +
                " -fx-background-insets: -10;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-size: 18");
        selectedClassFilesListView.setCellFactory((ListView<StudentFiles> param) -> new ListCell<StudentFiles>() {
            @Override
            protected void updateItem(StudentFiles file, boolean empty) {
                super.updateItem(file, empty);
                MenuItem openFileMenuItem = new MenuItem("Open File");
                openFileMenuItem.setOnAction(e -> System.out.println("Open file " + textProperty()));
                MenuItem exportFileMenuItem = new MenuItem("Export File");
                MenuItem redownloadFileMenuItem = new MenuItem("Redownload File");
                ContextMenu contextMenu = new ContextMenu(openFileMenuItem, exportFileMenuItem, redownloadFileMenuItem);
                if (empty || file == null || file.getFileName() == null) {
                    setText(null);
                    setContextMenu(null);
                    setGraphic(null);
                } else {
                    setText(file.getFileName());
                    setContextMenu(contextMenu);
                    ImageView savedImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Saved.png")));
                    savedImageView.setFitHeight(24);
                    savedImageView.setFitWidth(24);
                    ImageView downloadImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Download.png")));
                    downloadImageView.setFitHeight(24);
                    downloadImageView.setFitWidth(24);
                    if (file.getFileName().equals("Study Guide")) { //TODO
                        setGraphic(savedImageView);
                    } else {
                        setGraphic(downloadImageView);
                    }
                }
            }
        });
        selectedClassFilesListView.setMinWidth(300);
        selectedClassFilesListView.setPrefWidth(1800);
        HBox selectedClassHeadingPane = new HBox(selectedClassComboBox, selectedClassText);
        selectedClassHeadingPane.setSpacing(5);
        selectedClassHeadingPane.setAlignment(Pos.CENTER);
        HBox selectedClassFilesPane = new HBox(selectedClassFilesListView);
        HBox.setHgrow(selectedClassFilesPane, Priority.ALWAYS);
        selectedClassFilesPane.setAlignment(Pos.CENTER);
        selectedClassFilesPane.setPadding(new Insets(15, 150, 15, 150));
        selectedClassPane = new VBox(selectedClassHeadingPane, selectedClassActionsPane, selectedClassFilesPane);
        VBox.setVgrow(selectedClassFilesPane, Priority.ALWAYS);
        selectedClassPane.setSpacing(20);
        selectedClassPane.setPadding(new Insets(15));
        selectedClassPane.setAlignment(Pos.CENTER);

        //Setup time table pane
        timetableText = new Text("Timetable");
        timetableText.setStyle("-fx-font-size: 28pt;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        timetableGridPane = new GridPane();
        ObservableList<ColumnConstraints> columnConstraints = FXCollections.observableArrayList();
        for (int i = 0; i < 14; i++) {
            ColumnConstraints col = new ColumnConstraints();
            if (i == 0) {
                col.setPercentWidth(9);
            } else {
                col.setPercentWidth(7);
            }
            columnConstraints.add(col);
        }
        ObservableList<RowConstraints> rowConstraints = FXCollections.observableArrayList();
        for (int i = 0; i < 6; i++) {
            RowConstraints row = new RowConstraints();
            if (i == 0) {
                row.setPercentHeight(10);
            } else {
                row.setPercentHeight(18);
            }
            rowConstraints.add(row);
        }
        timetableGridPane.getColumnConstraints().addAll(columnConstraints);
        timetableGridPane.getRowConstraints().addAll(rowConstraints);
        String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        for (int i = 0; i < 5; i++) {
            Label label = new Label(weekdays[i]);
            label.setStyle("-fx-font-family: Verdana;" +
                    " -fx-font-size: 26;");
            label.setMaxSize(150, 150);
            label.setMinSize(100, 100);
            label.setAlignment(Pos.CENTER);
            label.setPadding(new Insets(5));
            timetableGridPane.add(label, 0, i + 1);
        }
        String[] timeSlots = {"08:00 - 08:45", "09:00 - 09:45", "10:00 - 10:45", "11:00 - 11:45", "12:00 - 12:45", "13:00 - 13:45", "14:00 - 14:45", "15:00 - 15:45", "16:00 - 16:45", "17:00 - 17:45", "18:00 - 18:45", "18:45 - 19:30", "19:30 - 20:15"};
        for (int i = 0; i < timeSlots.length; i++) {
            Label label = new Label(timeSlots[i]);
            label.setStyle("-fx-font-family: Verdana;" +
                    " -fx-font-size: 12;");
            label.setMaxSize(150, 150);
            label.setMinSize(100, 100);
            label.setAlignment(Pos.CENTER);
            label.setWrapText(true);
            label.setPadding(new Insets(5));
            timetableGridPane.add(label, i + 1, 0);
        }
        timetableGridPane.setGridLinesVisible(true);
        timetableGridPane.setMaxSize(1900, 780);
        timetableGridPane.setMinSize(1400, 500);
        HBox timetableInnerPane = new HBox(timetableGridPane);
        timetableInnerPane.setAlignment(Pos.CENTER);
        timetableInnerPane.setStyle("-fx-border-color: black");
        HBox.setHgrow(timetableInnerPane, Priority.ALWAYS);
        ScrollPane timetableScrollPane = new ScrollPane(timetableInnerPane);
        timetableScrollPane.setFitToHeight(true);
        timetableScrollPane.setFitToWidth(true);
        timetableScrollPane.setStyle("-fx-border-color: black");
        timetablePane = new VBox(timetableText, timetableScrollPane);
        timetablePane.setAlignment(Pos.CENTER);
        timetablePane.setSpacing(15);
        timetablePane.setPadding(new Insets(25));
        VBox.setVgrow(timetableScrollPane, Priority.ALWAYS);

        populateTimetable();

        //Setup chat pane


        //Setup tab pane
        Tab classesTab = new Tab("My Classes", selectedClassPane);
        classesTab.setClosable(false);
        Tab timetableTab = new Tab("My Timetable", timetablePane);
        timetableTab.setClosable(false);
        tabPane = new TabPane(classesTab, timetableTab);

        //Setup heading pane
        headingPane = new StackPane();
        headingPane.setStyle("-fx-background-image: url(\"CampusLiveBanner.png\");" +
                " -fx-background-size: auto 100%;" +
                " -fx-background-position: center;" +
                " -fx-background-repeat: no-repeat;" +
                " -fx-background-color: linear-gradient(rgba(66, 135, 167, .9), rgba(66, 135, 167, .7));" +
                " -fx-accent: white");
        headingPane.setMaxHeight(150);
        headingPane.setPrefHeight(150);
        headingPane.setMinHeight(100);

        //Setup student pane
        studentPane = new VBox(headingPane, tabPane);
        studentPane.setAlignment(Pos.CENTER);
        VBox.setVgrow(headingPane, Priority.ALWAYS);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

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
        contentPane = new StackPane(backgroundPane, loginPane); //TODO
        contentPane.setAlignment(Pos.CENTER);
;
        //Setup scene
        scene = new Scene(contentPane);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("CampusLiveStyle.css").toExternalForm());

        //Select and show scene
        stage.setScene(scene);
        stage.show();

    }

    private void populateTimetable() {
        List<StudentClass> studentClasses = connectionHandler.getClasses();
        for (StudentClass c : studentClasses) {
            for (int i = c.getStartSlot(); i <= c.getEndSlot(); i++) {
                Label label = new Label(c.getModuleName() + "\n\nTM (" + c.getRoomNumber() + ")");
                label.setStyle("-fx-font-family: Verdana;" +
                        " -fx-font-size: 11;");
                label.setMaxSize(150, 150);
                label.setMinSize(100, 100);
                label.setWrapText(true);
                label.setAlignment(Pos.TOP_CENTER);
                label.setPadding(new Insets(5));
                label.setOnMouseClicked(e -> {
                    selectedClassComboBox.getSelectionModel().select(c);
                    System.out.println(((Label) e.getSource()).getWidth());
                    tabPane.getSelectionModel().select(0);
                });
                timetableGridPane.add(label, i, c.getDayOfWeek());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
