package net.ddns.swooosh.campuslivestudent.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.ddns.swooosh.campuslivestudent.models.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Display extends Application{

    private static final File APPLICATION_FOLDER = new File(System.getProperty("user.home") + "/AppData/Local/Swooosh/CampusLiveStudent");
    private static final File LOCAL_CACHE = new File(APPLICATION_FOLDER.getAbsolutePath() + "/Local Cache");
    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private Student student = connectionHandler.getStudent();
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
    private ComboBox<ClassAndResult> selectedClassComboBox;
    private Button selectedClassContactLecturerButton;
    private HBox selectedClassActionsPane;
    private ListView<StudentFile> selectedClassFilesListView;
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

    public void start(Stage primaryStage) throws Exception {

        //Setup stage
        stage = primaryStage;
        stage.setTitle("Campus Live Student (On-Campus) (" + student.getCampus() + ") " + getBuild());
        stage.getIcons().addAll(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        stage.setMaxHeight(1080);
        stage.setMaxWidth(1920);
        stage.setMinHeight(500);
        stage.setMinWidth(800);
        stage.setMaximized(true);

        //Setup tooltip delays
        try {
            Class<?> clazz = Tooltip.class.getDeclaredClasses()[0];
            Constructor<?> constructor = clazz.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
            constructor.setAccessible(true);
            Object tooltipBehavior = constructor.newInstance(new Duration(250), new Duration(5000), new Duration(250), false);
            Field fieldBehavior = Tooltip.class.getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            fieldBehavior.set(Tooltip.class, tooltipBehavior);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //<editor-fold desc="Login Pane">
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
        waitIndicator.setPrefSize(206, 206);
        loginPane = new VBox(loginLogoImageView, studentNumberTextField, passwordField, loginButton, forgotPasswordHyperlink);
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setSpacing(20);
        loginPane.setPadding(new Insets(10));
        loginPane.setMaxSize(500, 500);
        //</editor-fold>

        //<editor-fold desc="Selected Class Pane">
        //Setup selected class pane
        selectedClassText = new Text();
        selectedClassText.setStyle("-fx-font-size: 20pt;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        selectedClassComboBox = new ComboBox<>(FXCollections.observableList(student.getClassAndResults()));
        selectedClassComboBox.setStyle(" -fx-background-radius: 5;" +
                " -fx-background-color: transparent;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-size: 18;");
        selectedClassComboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
            selectedClassText.setText(selectedClassComboBox.getSelectionModel().getSelectedItem().toString());
            if (selectedClassFilesListView != null) {
                selectedClassFilesListView.setItems(selectedClassComboBox.getSelectionModel().getSelectedItem().getStudentClass().getFiles());
            }
        });
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2;
        int currentTimeSlot = getCurrentTimeSlot();
        Boolean timeSlotSet = false;
        SetTimeSlot:
        for (ClassAndResult car : student.getClassAndResults()) {
            for (ClassTime ct : car.getStudentClass().getClassTimes()) {
                if (ct.getDayOfWeek() == dayOfWeek && currentTimeSlot >= ct.getStartSlot() && currentTimeSlot <= ct.getEndSlot()) {
                    selectedClassComboBox.getSelectionModel().select(car);
                    timeSlotSet = true;
                    break SetTimeSlot;
                }
            }
        }
        if (!timeSlotSet) {
            selectedClassComboBox.getSelectionModel().select(0);
        }
        selectedClassComboBox.setMinSize(50, 50);
        selectedClassComboBox.setMaxSize(50, 50);
        selectedClassResultsButton = new Button("My Results");
        selectedClassResultsButton.setOnAction(e -> {
            new ResultDisplay(student.getClassAndResults(), selectedClassComboBox.getSelectionModel().getSelectedItem(), stage);
        });
        selectedClassResultsButton.setStyle(" -fx-background-radius: 15;" +
                " -fx-border-radius: 15;" +
                " -fx-background-insets: 0;" +
                " -fx-border-width: 2;" +
                " -fx-background-color: #FECD34;" +
                " -fx-border-color: black;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-weight: bold;" +
                " -fx-font-size: 16;");
        selectedClassResultsButton.setMinSize(250, 36);
        selectedClassResultsButton.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.6), 5, 0.0, 2, 2));
        selectedClassContactLecturerButton = new Button("Contact Lecturer");
        selectedClassContactLecturerButton.setOnAction(e -> {
            if (connectionHandler.isLecturerOnline(selectedClassComboBox.getSelectionModel().getSelectedItem().getStudentClass().getLecturerNumber())) {
                int result = UserNotification.showLecturerContactMethod();
                if (result == UserNotification.DIRECT_OPTION) {
                    //TODO open email to lecturer
                } else if (result == UserNotification.EMAIL_OPTION) {
                    //TODO open direct message lecturer
                }
            } else {
                //TODO open email to lecturer
            }
        });
        selectedClassContactLecturerButton.setStyle(" -fx-background-radius: 15;" +
                " -fx-border-radius: 15;" +
                " -fx-background-insets: 0;" +
                " -fx-border-width: 2;" +
                " -fx-background-color: #FECD34;" +
                " -fx-border-color: black;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-weight: bold;" +
                " -fx-font-size: 16;");
        selectedClassContactLecturerButton.setMinSize(250, 36);
        selectedClassContactLecturerButton.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.6), 5, 0.0, 2, 2));
        selectedClassActionsPane = new HBox(selectedClassResultsButton, selectedClassContactLecturerButton);
        selectedClassActionsPane.setAlignment(Pos.CENTER);
        selectedClassActionsPane.setSpacing(50);
        selectedClassFilesListView = new ListView<>(selectedClassComboBox.getSelectionModel().getSelectedItem().getStudentClass().getFiles());
        selectedClassFilesListView.setStyle("-fx-background-color: rgba(66, 135, 167, .7);" +
                " -fx-control-inner-background: transparent;" +
                " -fx-background-radius: 15;" +
                " -fx-background-insets: -10;" +
                " -fx-border-color: black;" +
                " -fx-border-radius: 15;" +
                " -fx-border-insets: -10;" +
                " -fx-border-width: 2;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-size: 20");
        selectedClassFilesListView.setCellFactory((ListView<StudentFile> param) -> new ListCell<StudentFile>() {
            @Override
            protected void updateItem(StudentFile file, boolean empty) {
                super.updateItem(file, empty);
                ImageView savedImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Saved.png")));
                savedImageView.setFitHeight(32);
                savedImageView.setFitWidth(32);
                ImageView downloadImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Download.png")));
                downloadImageView.setFitHeight(32);
                downloadImageView.setFitWidth(32);
                MenuItem openFileMenuItem = new MenuItem("Open File");
                openFileMenuItem.setOnAction(event -> {
                    File f;
                    if ((f = new File(LOCAL_CACHE.getAbsolutePath() + "/" + file.getClassID() + "/" + file.getFileName())).exists() && f.length() == file.getFileLength()) {
                        try {
                            Desktop.getDesktop().open(f);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                MenuItem exportFileMenuItem = new MenuItem("Export File");
                exportFileMenuItem.setOnAction(event -> {
                    try {
                        DirectoryChooser dc = new DirectoryChooser();
                        dc.setTitle("Choose Directory to export to");
                        File f = dc.showDialog(stage);
                        if (f != null) {
                            File target = new File(f.getAbsolutePath() + "/" + file.getFileName());
                            File toCopy = new File(LOCAL_CACHE.getAbsolutePath() + "/" + file.getClassID() + "/" + file.getFileName());
                            Files.copy(toCopy.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                MenuItem redownloadFileMenuItem = new MenuItem("Redownload File");
                ContextMenu savedContextMenu = new ContextMenu(openFileMenuItem, exportFileMenuItem, redownloadFileMenuItem);
                savedContextMenu.setStyle("-fx-font-family: Verdana;" +
                        " -fx-font-size: 18;");
                MenuItem downloadFileMenuItem = new MenuItem("Download File");
                ContextMenu downloadContextMenu = new ContextMenu(downloadFileMenuItem);
                downloadContextMenu.setStyle("-fx-font-family: Verdana;" +
                        " -fx-font-size: 18;");
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        File f;
                        if ((f = new File(LOCAL_CACHE.getAbsolutePath() + "/" + file.getClassID() + "/" + file.getFileName())).exists() && f.length() == file.getFileLength()) {
                            try {
                                Desktop.getDesktop().open(f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                setGraphicTextGap(20);
                if (empty || file == null || file.getFileName() == null) {
                    setText(null);
                    setContextMenu(null);
                    setGraphic(null);
                } else {
                    setText(getFileNameWithoutExtension(file.getFileName()));
                    File f;
                    if ((f = new File(LOCAL_CACHE.getAbsolutePath() + "/" + file.getClassID() + "/" + file.getFileName())).exists() && f.length() == file.getFileLength()) {
                        setGraphic(savedImageView);
                        setContextMenu(savedContextMenu);
                    } else {
                        setGraphic(downloadImageView);
                        setContextMenu(downloadContextMenu);
                    }
                }
            }
        });
        selectedClassFilesListView.setMinWidth(300);
        selectedClassFilesListView.setPrefWidth(1800);
        selectedClassFilesListView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                selectedClassFilesListView.getSelectionModel().clearSelection();
            }
        });
        HBox selectedClassHeadingPane = new HBox(selectedClassComboBox, selectedClassText);
        selectedClassHeadingPane.setSpacing(5);
        selectedClassHeadingPane.setAlignment(Pos.CENTER);
        HBox selectedClassFilesPane = new HBox(selectedClassFilesListView);
        HBox.setHgrow(selectedClassFilesPane, Priority.ALWAYS);
        selectedClassFilesPane.setAlignment(Pos.CENTER);
        selectedClassFilesPane.setPadding(new Insets(15, 150, 30, 150));
        selectedClassPane = new VBox(selectedClassHeadingPane, selectedClassActionsPane, selectedClassFilesPane);
        VBox.setVgrow(selectedClassFilesPane, Priority.ALWAYS);
        selectedClassPane.setSpacing(10);
        selectedClassPane.setPadding(new Insets(15));
        selectedClassPane.setAlignment(Pos.CENTER);
        //</editor-fold>

        //<editor-fold desc="Timetable Pane">
        //Setup timetable pane
        timetableText = new Text("Timetable");
        timetableText.setStyle("-fx-font-size: 32pt;" +
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
        //</editor-fold>

        //<editor-fold desc="Notice Board Pane">
        //Setup notice board pane
        Text noticeBoardText = new Text("Notice Board");
        noticeBoardText.setStyle("-fx-font-size: 32pt;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        ObservableList<NoticeBoard> noticeBoards = connectionHandler.getNoticeBoards();
        ObservableList<StackPane> noticePanes = FXCollections.observableArrayList();
        for (NoticeBoard nb : noticeBoards) {
            ImageView pushpinImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Pushpin.png")));
            pushpinImageView.setFitHeight(64);
            pushpinImageView.setFitWidth(64);
            HBox pushpinPane = new HBox(pushpinImageView);
            pushpinPane.setAlignment(Pos.CENTER);
            pushpinPane.setPadding(new Insets(-15, 0, -40, 0));
            Text headingLabel = new Text(nb.getHeading());
            headingLabel.setWrappingWidth(450);
            headingLabel.setStyle("-fx-font-family: Verdana;" +
                    " -fx-font-size: 28;" +
                    " -fx-font-weight: bold;");
            Text descriptionLabel = new Text(nb.getDesription());
            descriptionLabel.setWrappingWidth(450);
            descriptionLabel.setStyle("-fx-font-family: Verdana;" +
                    " -fx-font-size: 22;");
            VBox noticePane = new VBox(pushpinPane, headingLabel, descriptionLabel);
            VBox.setVgrow(descriptionLabel, Priority.ALWAYS);
            noticePane.setAlignment(Pos.TOP_LEFT);
            noticePane.setPadding(new Insets(0, 15, 15, 15));
            noticePane.setSpacing(15);
            int randomInt = (int) (Math.random() * 4);
            if (randomInt == 3) {
                noticePane.setStyle("-fx-background-color: #8ae1e3");
            } else if (randomInt == 2) {
                noticePane.setStyle("-fx-background-color: #cf90e3");
            } else if (randomInt == 1) {
                noticePane.setStyle("-fx-background-color: #82e367");
            } else {
                noticePane.setStyle("-fx-background-color: #dce334");
            }
            noticePane.setMinWidth(500);
            noticePane.setMaxWidth(500);
            StackPane shadowPane = new StackPane(noticePane);
            shadowPane.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0.0, -5, 5);");
            shadowPane.setRotate((Math.random() * 3.0) - 1.5);
            FlowPane.setMargin(shadowPane, new Insets(3));
            noticePanes.add(shadowPane);
        }
        FlowPane bulletinBoardPane = new FlowPane();
        bulletinBoardPane.getChildren().addAll(noticePanes);
        bulletinBoardPane.setAlignment(Pos.CENTER);
        bulletinBoardPane.setOrientation(Orientation.HORIZONTAL);
        bulletinBoardPane.setPadding(new Insets(20));
        bulletinBoardPane.setStyle("-fx-background-image: url(\"BulletinBoard.jpg\");" +
                " -fx-background-size: 75%;");
        ScrollPane bulletinBoardScrollPane = new ScrollPane(new StackPane(bulletinBoardPane));
        bulletinBoardPane.prefHeightProperty().bind(bulletinBoardScrollPane.heightProperty().subtract(42));
        bulletinBoardScrollPane.setFitToWidth(true);
        bulletinBoardScrollPane.setStyle("-fx-border-color: rgba(0, 135, 167, 0.6);" +
                " -fx-border-width: 20;");
        VBox noticeBoardPane = new VBox(noticeBoardText, bulletinBoardScrollPane);
        noticeBoardPane.setPadding(new Insets(15, 100, 15, 100));
        noticeBoardPane.setSpacing(20);
        noticeBoardPane.setAlignment(Pos.CENTER);
        VBox.setVgrow(bulletinBoardScrollPane, Priority.ALWAYS);
        //</editor-fold>

        //Setup chat pane
        //TODO

        //Setup tab pane
        Tab classesTab = new Tab("My Classes", selectedClassPane);
        classesTab.setClosable(false);
        Tab timetableTab = new Tab("My Timetable", timetablePane);
        timetableTab.setClosable(false);
        Tab noticeBoardTab = new Tab("Notice Board", noticeBoardPane);
        noticeBoardTab.setClosable(false);
        tabPane = new TabPane(classesTab, timetableTab, noticeBoardTab);

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

        //<editor-fold desc="Bottom Pane">
        //Setup bottom Pane
        Circle websiteLink = new Circle(15, new ImagePattern(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png"))));
        websiteLink.setOnMouseClicked(e -> {
            try {
                Desktop.getDesktop().browse(new URI("http://swooosh.ddns.net"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        Text websiteText = new Text("Website");
        websiteText.setStyle("-fx-font-family: Verdana;" +
                " -fx-font-size: 16;");
        HBox websitePane = new HBox(websiteLink, websiteText);
        websitePane.setAlignment(Pos.CENTER_LEFT);
        websitePane.setSpacing(5);
        Text settingsText = new Text("Settings");
        settingsText.setStyle("-fx-font-family: Verdana;" +
                " -fx-font-size: 16;");
        Circle settingsLink = new Circle(15, new ImagePattern(new Image(getClass().getClassLoader().getResourceAsStream("Settings.png"))));
        settingsLink.setOnMouseClicked(e -> {
            //TODO open settings
        });
        HBox settingsPane = new HBox(settingsText, settingsLink);
        settingsPane.setAlignment(Pos.CENTER_LEFT);
        settingsPane.setSpacing(5);
        settingsPane.setAlignment(Pos.CENTER_RIGHT);
        Text developerText = new Text("Developed by Swooosh Apps Solutions");
        developerText.setStyle("-fx-font-family: Verdana;" +
                " -fx-font-size: 12;" +
                " -fx-text-fill: dimgrey;");
        HBox developerPane = new HBox(developerText);
        developerPane.setAlignment(Pos.CENTER);
        developerPane.setSpacing(5);
        HBox bottomPane = new HBox(websitePane, developerPane, settingsPane);
        bottomPane.setPadding(new Insets(5));
        bottomPane.setStyle("-fx-background-color: linear-gradient(rgba(66, 135, 167, .5), rgba(66, 135, 167, .9));");
        HBox.setHgrow(developerPane, Priority.ALWAYS);
        //</editor-fold>

        //Setup student pane
        studentPane = new VBox(headingPane, tabPane, bottomPane);
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
        List<ClassAndResult> classAndResults = student.getClassAndResults();
        for (ClassAndResult cr : classAndResults) {
            String moduleName = cr.getStudentClass().getModuleName();
            String lecturerInitials = cr.getStudentClass().getLecturerFirstName().charAt(0) + "" + cr.getStudentClass().getLecturerLastName().charAt(0);
            for (ClassTime ct : cr.getStudentClass().getClassTimes()) {
                for (int i = ct.getStartSlot(); i <= ct.getEndSlot(); i++) {
                    Label label = new Label(moduleName + "\n\n" + lecturerInitials + " (" + ct.getRoomNumber() + ")");
                    label.setStyle("-fx-font-family: Verdana;" +
                            " -fx-font-size: 11;");
                    label.setMaxSize(150, 150);
                    label.setMinSize(100, 100);
                    label.setWrapText(true);
                    label.setAlignment(Pos.TOP_CENTER);
                    label.setPadding(new Insets(5));
                    label.setOnMouseClicked(e -> {
                        selectedClassComboBox.getSelectionModel().select(cr);
                        System.out.println(((Label) e.getSource()).getWidth());
                        tabPane.getSelectionModel().select(0);
                    });
                    timetableGridPane.add(label, i, ct.getDayOfWeek());
                }
            }
        }
    }

    public String getFileNameWithoutExtension(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    public String getBuild() {
        try {
            Scanner scn = new Scanner(new File(APPLICATION_FOLDER.getAbsolutePath() + "/Version.txt"));
            return "(Build " + scn.nextLine() + ")";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "(Build N/A)";
    }

    public int getCurrentTimeSlot() {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date currentDate = parser.parse(parser.format(Calendar.getInstance().getTime()));
            if (currentDate.compareTo(parser.parse("8:00")) >= 0 && currentDate.compareTo(parser.parse("9:00")) < 0) {
                return 1;
            } else if (currentDate.compareTo(parser.parse("9:00")) >= 0 && currentDate.compareTo(parser.parse("10:00")) < 0) {
                return 2;
            } else if (currentDate.compareTo(parser.parse("10:00")) >= 0 && currentDate.compareTo(parser.parse("11:00")) < 0) {
                return 3;
            } else if (currentDate.compareTo(parser.parse("11:00")) >= 0 && currentDate.compareTo(parser.parse("12:00")) < 0) {
                return 4;
            } else if (currentDate.compareTo(parser.parse("12:00")) >= 0 && currentDate.compareTo(parser.parse("13:00")) < 0) {
                return 5;
            } else if (currentDate.compareTo(parser.parse("13:00")) >= 0 && currentDate.compareTo(parser.parse("14:00")) < 0) {
                return 6;
            } else if (currentDate.compareTo(parser.parse("14:00")) >= 0 && currentDate.compareTo(parser.parse("15:00")) < 0) {
                return 7;
            } else if (currentDate.compareTo(parser.parse("15:00")) >= 0 && currentDate.compareTo(parser.parse("16:00")) < 0) {
                return 8;
            } else if (currentDate.compareTo(parser.parse("16:00")) >= 0 && currentDate.compareTo(parser.parse("17:00")) < 0) {
                return 9;
            } else if (currentDate.compareTo(parser.parse("17:00")) >= 0 && currentDate.compareTo(parser.parse("18:00")) < 0) {
                return 10;
            } else if (currentDate.compareTo(parser.parse("18:00")) >= 0 && currentDate.compareTo(parser.parse("18:45")) < 0) {
                return 11;
            } else if (currentDate.compareTo(parser.parse("18:45")) >= 0 && currentDate.compareTo(parser.parse("19:30")) < 0) {
                return 12;
            } else if (currentDate.compareTo(parser.parse("19:30")) >= 0 && currentDate.compareTo(parser.parse("20:15")) < 0) {
                return 13;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }


    public static void main(String[] args) {
        if (!LOCAL_CACHE.exists()) {
            LOCAL_CACHE.mkdirs();
        }
        launch(args);
    }

}
