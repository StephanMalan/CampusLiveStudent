package net.ddns.swooosh.campuslivestudent.main;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.*;

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

public class Display extends Application {

    public static final File APPLICATION_FOLDER = new File(System.getProperty("user.home") + "/AppData/Local/Swooosh/CampusLiveStudent");
    public static final File LOCAL_CACHE = new File(APPLICATION_FOLDER.getAbsolutePath() + "/Local Cache");
    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private ObservableList<ClassAndResult> classAndResults = FXCollections.observableArrayList();
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
    private ListView<StudentFileObservable> selectedClassFilesListView;
    private VBox selectedClassPane;
    private Text timetableText;
    private GridPane timetableGridPane;
    private VBox timetablePane;
    private FlowPane bulletinBoardPane;
    private TabPane tabPane;
    private StackPane headingPane;
    private VBox studentPane;
    private StackPane backgroundPane;
    private StackPane contentPane;
    private Scene scene;

    public void start(Stage primaryStage) throws Exception {

        //Setup stage
        stage = primaryStage;
        stage.setTitle("Campus Live Student " + connectionHandler.getConnectionType() + " " + getBuild());
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
        } catch (Exception e) {
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
        studentNumberTextField.getStyleClass().add("loginFields");
        studentNumberTextField.setMaxWidth(250);
        studentNumberTextField.setMinHeight(40);
        studentNumberTextField.setPadding(new Insets(5, 5, 5, 30));
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("loginFields");
        passwordField.getStyleClass().add("loginPassword");
        passwordField.setMaxWidth(250);
        passwordField.setMinHeight(40);
        passwordField.setPadding(new Insets(5, 5, 5, 30));
        loginButton = new Button("Login");
        loginButton.getStyleClass().add("loginButton");
        loginButton.setMaxWidth(250);
        loginButton.setMinHeight(40);
        loginButton.setPadding(new Insets(2, 2, 2, 2));
        loginButton.setOnAction((ActionEvent e) -> {
            studentNumberTextField.setBorder(null);
            passwordField.setBorder(null);
            if (studentNumberTextField.getText().length() < 11) {
                studentNumberTextField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
                Tooltip studentNumberShortTooltip = new Tooltip("Student number too short");
                studentNumberShortTooltip.getStyleClass().add("loginTooltip");
                studentNumberTextField.setTooltip(studentNumberShortTooltip);
            } else if (passwordField.getText().length() < 5) {
                passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
                Tooltip passwordShortTooltip = new Tooltip("Password too short");
                passwordShortTooltip.getStyleClass().add("loginTooltip");
                passwordField.setTooltip(passwordShortTooltip);
            } else {
                loginPane.getChildren().clear();
                loginPane.getChildren().addAll(loginLogoImageView, waitIndicator);
                BooleanProperty waitingToAuthorise = new SimpleBooleanProperty(false);
                BooleanProperty authoriseResult = new SimpleBooleanProperty(false);
                waitingToAuthorise.setValue(true);
                Thread loginThread = new Thread(() -> {
                    if (connectionHandler.authorise(studentNumberTextField.getText(), passwordField.getText())) {
                        System.out.println(1);
                        while (connectionHandler.studentInitialized()) ;
                        System.out.println(2);
                        authoriseResult.setValue(true);
                    } else {
                        authoriseResult.setValue(false);
                    }
                    waitingToAuthorise.set(false);
                });
                loginThread.start();
                waitingToAuthorise.addListener(al -> {
                    if (!waitingToAuthorise.getValue()) {
                        if (authoriseResult.getValue()) {
                            Platform.runLater(() -> {
                                contentPane.getChildren().clear();
                                contentPane.getChildren().addAll(backgroundPane, studentPane);
                            });
                        } else {
                            Platform.runLater(() -> {
                                passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
                                Tooltip incorrectLoginTooltip = new Tooltip("Incorrect login details");
                                incorrectLoginTooltip.getStyleClass().add("loginTooltip");
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
        selectedClassText.setOnMouseClicked(e -> selectedClassComboBox.show());
        selectedClassText.getStyleClass().add("selectedClassText");
        selectedClassComboBox = new ComboBox<>(classAndResults);
        selectedClassComboBox.getStyleClass().add("selectedClassCombo");
        selectedClassComboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
            if (selectedClassComboBox.getSelectionModel().getSelectedIndex() >= 0) {
                selectedClassText.setText(selectedClassComboBox.getSelectionModel().getSelectedItem().toString());
                if (selectedClassFilesListView != null) {
                    selectedClassFilesListView.setItems(null);
                    ObservableList<StudentFileObservable> studentFiles = FXCollections.observableArrayList();
                    for (StudentFile studentFile : selectedClassComboBox.getSelectionModel().getSelectedItem().getStudentClass().getFiles()) {
                        studentFiles.add(new StudentFileObservable(studentFile));
                    }
                    selectedClassFilesListView.setItems(studentFiles);
                }
            }
        });
        selectedClassComboBox.setMinSize(50, 50);
        selectedClassComboBox.setMaxSize(50, 50);
        selectedClassResultsButton = new Button("My Results");
        selectedClassResultsButton.setOnAction(e -> {
            new ResultDisplay(selectedClassComboBox.getItems(), selectedClassComboBox.getSelectionModel().getSelectedItem(), stage);
        });
        selectedClassResultsButton.getStyleClass().add("classButton");
        selectedClassResultsButton.setMinSize(200, 35);
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
        selectedClassContactLecturerButton.getStyleClass().add("classButton");
        selectedClassContactLecturerButton.setMinSize(200, 35);
        selectedClassContactLecturerButton.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.6), 5, 0.0, 2, 2));
        selectedClassActionsPane = new HBox(selectedClassResultsButton, selectedClassContactLecturerButton);
        selectedClassActionsPane.setAlignment(Pos.CENTER);
        selectedClassActionsPane.setSpacing(50);
        selectedClassFilesListView = new ListView<>();
        selectedClassFilesListView.getStyleClass().add("selectedListView");
        selectedClassFilesListView.setCellFactory(param -> new ListCell<StudentFileObservable>() {
            @Override
            protected void updateItem(StudentFileObservable file, boolean empty) {
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
                    if ((f = new File(LOCAL_CACHE.getAbsolutePath() + "/" + file.getStudentFile().getClassID() + "/" + file.getStudentFile().getFileName())).exists() && f.length() == file.getStudentFile().getFileLength()) {
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
                            File target = new File(f.getAbsolutePath() + "/" + file.getStudentFile().getFileName());
                            File toCopy = new File(LOCAL_CACHE.getAbsolutePath() + "/" + file.getStudentFile().getClassID() + "/" + file.getStudentFile().getFileName());
                            Files.copy(toCopy.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                MenuItem redownloadFileMenuItem = new MenuItem("Redownload File");
                redownloadFileMenuItem.setOnAction(e -> connectionHandler.deleteFile(file.getStudentFile().getClassID(), file.getStudentFile().getFileName()));
                ContextMenu savedContextMenu = new ContextMenu(openFileMenuItem, exportFileMenuItem, redownloadFileMenuItem);
                savedContextMenu.getStyleClass().add("selectedContextMenu");
                MenuItem downloadFileMenuItem = new MenuItem("Download File");
                downloadFileMenuItem.setOnAction(e -> {
                    file.getStudentFile().setValue(2);
                    ConnectionHandler.FileDownloader fileDownloader = connectionHandler.new FileDownloader(file.getStudentFile());
                    fileDownloader.start();
                    file.getStudentFile().setFileDownloader(fileDownloader);
                    connectionHandler.student.update();
                });
                ContextMenu downloadContextMenu = new ContextMenu(downloadFileMenuItem);
                downloadContextMenu.getStyleClass().add("selectedContextMenu");
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        if (getGraphic().equals(savedImageView)) {
                            File f;
                            if ((f = new File(LOCAL_CACHE.getAbsolutePath() + "/" + file.getStudentFile().getClassID() + "/" + file.getStudentFile().getFileName())).exists() && f.length() == file.getStudentFile().getFileLength()) {
                                try {
                                    Desktop.getDesktop().open(f);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (getGraphic().equals(downloadImageView)) {
                            downloadFileMenuItem.fire();
                        }
                    }
                });
                if (empty || file == null || file.getStudentFile().getFileName() == null) {
                    setText(null);
                    setContextMenu(null);
                    setGraphic(null);
                } else {
                    setText(getFileNameWithoutExtension(file.getStudentFile().getFileName()));
                    if (file.getStudentFile().getValue() == 0) {
                        setGraphic(downloadImageView);
                        setGraphicTextGap(35);
                        setContextMenu(downloadContextMenu);
                    } else if (file.getStudentFile().getValue() == 1) {
                        setGraphic(savedImageView);
                        setGraphicTextGap(35);
                        setContextMenu(savedContextMenu);
                    } else {
                        ProgressBar percentageBar = new ProgressBar(0);
                        percentageBar.setMinSize(50, 30);
                        percentageBar.setMaxSize(50, 30);
                        percentageBar.progressProperty().bind(file.progressProperty());
                        Text percentageText = new Text();
                        percentageText.textProperty().bind(file.progressProperty().multiply(100).asString("%.0f").concat("%"));
                        percentageText.getStyleClass().add("percentageText");
                        StackPane percentagePane = new StackPane(percentageBar, percentageText);
                        setGraphic(percentagePane);
                        setGraphicTextGap(16);
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
        selectedClassPane.setPadding(new Insets(5));
        selectedClassPane.setAlignment(Pos.CENTER);
        //</editor-fold>

        //<editor-fold desc="Timetable Pane">
        //Setup timetable pane
        timetableText = new Text("Timetable");
        timetableText.getStyleClass().add("headingText");
        timetableGridPane = new GridPane();
        ObservableList<ColumnConstraints> columnConstraints = FXCollections.observableArrayList();
        for (int i = 0; i < 14; i++) {
            ColumnConstraints col = new ColumnConstraints();
            if (i == 0) {
                col.setPercentWidth(4);
            } else {
                col.setPercentWidth(8);
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
        timetableGridPane.setGridLinesVisible(true);
        timetableGridPane.getStyleClass().add("timeTablePane");
        timetableGridPane.setMaxSize(1900, 780);
        timetableGridPane.setMinSize(1400, 500);
        HBox timetableInnerPane = new HBox(timetableGridPane);
        timetableInnerPane.setAlignment(Pos.CENTER);
        timetableInnerPane.getStyleClass().add("timeTableInnerPane");
        HBox.setHgrow(timetableInnerPane, Priority.ALWAYS);
        ScrollPane timetableScrollPane = new ScrollPane(timetableInnerPane);
        timetableScrollPane.setFitToHeight(true);
        timetableScrollPane.setFitToWidth(true);
        timetableScrollPane.getStyleClass().add("timetableScrollPane");
        timetablePane = new VBox(timetableText, timetableScrollPane);
        timetablePane.setAlignment(Pos.CENTER);
        timetablePane.setSpacing(15);
        timetablePane.setPadding(new Insets(10));
        VBox.setVgrow(timetableScrollPane, Priority.ALWAYS);
        //</editor-fold>

        //<editor-fold desc="Notice Board Pane">
        //Setup notice board pane
        Text noticeBoardText = new Text("Notice Board");
        noticeBoardText.getStyleClass().add("headingText");
        bulletinBoardPane = new FlowPane();
        populateNoticeBoard();
        bulletinBoardPane.setAlignment(Pos.CENTER);
        bulletinBoardPane.setHgap(10);
        bulletinBoardPane.setVgap(10);
        bulletinBoardPane.setOrientation(Orientation.HORIZONTAL);
        bulletinBoardPane.setPadding(new Insets(20));
        bulletinBoardPane.getStyleClass().add("bulletinBoardPane");
        ScrollPane bulletinBoardScrollPane = new ScrollPane(new StackPane(bulletinBoardPane));
        bulletinBoardPane.prefHeightProperty().bind(bulletinBoardScrollPane.heightProperty().subtract(42));
        bulletinBoardScrollPane.setFitToWidth(true);
        bulletinBoardScrollPane.getStyleClass().add("bulletinBoardScrollPane");
        VBox noticeBoardPane = new VBox(noticeBoardText, bulletinBoardScrollPane);
        noticeBoardPane.setPadding(new Insets(10, 100, 10, 100));
        noticeBoardPane.setSpacing(20);
        noticeBoardPane.setAlignment(Pos.CENTER);
        VBox.setVgrow(bulletinBoardScrollPane, Priority.ALWAYS);
        //</editor-fold>

        //Setup tab pane
        Tab classesTab = new Tab("   My Classes   ", selectedClassPane);
        classesTab.setClosable(false);
        Tab timetableTab = new Tab("   My Timetable   ", timetablePane);
        timetableTab.setClosable(false);
        Tab noticeBoardTab = new Tab("   Notice Board   ", noticeBoardPane);
        noticeBoardTab.setClosable(false);
        tabPane = new TabPane(classesTab, timetableTab, noticeBoardTab);

        //Setup heading pane
        headingPane = new StackPane();
        headingPane.getStyleClass().add("headingPane");
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
        HBox websitePane = new HBox(websiteLink, websiteText);
        websitePane.setAlignment(Pos.CENTER_LEFT);
        websitePane.setSpacing(5);
        Text settingsText = new Text("Settings");
        Circle settingsLink = new Circle(15, new ImagePattern(new Image(getClass().getClassLoader().getResourceAsStream("Settings.png"))));
        settingsLink.setOnMouseClicked(e -> new Settings(stage));
        HBox settingsPane = new HBox(settingsText, settingsLink);
        settingsPane.setAlignment(Pos.CENTER_LEFT);
        settingsPane.setSpacing(5);
        settingsPane.setAlignment(Pos.CENTER_RIGHT);
        Text developerText = new Text("Developed by Swooosh Apps Solutions");
        developerText.getStyleClass().add("developerText");
        HBox developerPane = new HBox(developerText);
        developerPane.setAlignment(Pos.CENTER);
        developerPane.setSpacing(5);
        HBox bottomPane = new HBox(websitePane, developerPane, settingsPane);
        bottomPane.setPadding(new Insets(5));
        bottomPane.getStyleClass().add("bottomPane");
        HBox.setHgrow(developerPane, Priority.ALWAYS);
        //</editor-fold>

        //Setup student pane
        studentPane = new VBox(headingPane, tabPane, bottomPane);
        studentPane.setAlignment(Pos.CENTER);
        VBox.setVgrow(headingPane, Priority.ALWAYS);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        //Setup background pane
        backgroundPane = new StackPane();
        backgroundPane.getStyleClass().add("backgroundPane");
        backgroundPane.setEffect(new GaussianBlur(10000));

        //Setup content pane
        contentPane = new StackPane(backgroundPane, loginPane); //TODO
        contentPane.setAlignment(Pos.CENTER);

        //Setup login pane animation
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), loginPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        //Setup student update listener
        connectionHandler.student.update.addListener((observable, oldV, newV) -> {
            if (newV) {
                System.out.println("Student updated");
                if (connectionHandler.student.getStudent() != null) {
                    String prevSelected = null;
                    if (!selectedClassComboBox.getSelectionModel().isEmpty()) {
                        prevSelected = selectedClassComboBox.getSelectionModel().getSelectedItem().getStudentClass().getModuleNumber();
                    }
                    classAndResults.clear();
                    classAndResults.addAll(connectionHandler.student.getStudent().getClassAndResults());
                    if (prevSelected == null) {
                        SetTimeSlot:
                        if (!selectedClassComboBox.getItems().isEmpty()) {
                            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
                            int currentTimeSlot = getCurrentTimeSlot();
                            for (ClassAndResult car : selectedClassComboBox.getItems()) {
                                for (ClassTime ct : car.getStudentClass().getClassTimes()) {
                                    if (ct.getDayOfWeek() == dayOfWeek && currentTimeSlot >= ct.getStartSlot() && currentTimeSlot <= ct.getEndSlot()) {
                                        selectedClassComboBox.getSelectionModel().select(car);
                                        break SetTimeSlot;
                                    }
                                }
                            }
                            selectedClassComboBox.getSelectionModel().select(0);
                        }
                    } else {
                        for (ClassAndResult car : classAndResults) {
                            if (car.getStudentClass().getModuleNumber().equals(prevSelected)) {
                                selectedClassComboBox.getSelectionModel().select(car);
                            }
                        }
                    }
                    Platform.runLater(() -> stage.setTitle("Campus Live Student " + connectionHandler.getConnectionType() + " (" + connectionHandler.student.getStudent().getCampus() + ") " + getBuild()));
                    populateTimetable();
                }
            }
        });

        //Setup notice board update listener
        connectionHandler.noticeBoard.addListener((InvalidationListener) e -> {
            if (!connectionHandler.noticeBoard.isEmpty()) {
                populateNoticeBoard();
            }
        });

        //Setup scene
        scene = new Scene(contentPane);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("CampusLiveStyle.css").toExternalForm());

        //Select and show scene
        stage.setScene(scene);
        stage.show();
    }

    private void populateTimetable() {
        List<ClassAndResult> classAndResults = selectedClassComboBox.getItems();
        ObservableList<Node> nodesToRemove = FXCollections.observableArrayList();
        for (Node node : timetableGridPane.getChildren()) {
            if (node instanceof Label) {
                nodesToRemove.add(node);
            }
        }
        for (Node node : nodesToRemove) {
            timetableGridPane.getChildren().remove(node);
        }
        String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        for (int i = 0; i < 5; i++) {
            Label label = new Label(weekdays[i]);
            label.getStyleClass().add("timeTableWeekdays");
            label.setMaxSize(100, 100);
            label.setMinSize(50, 50);
            label.setAlignment(Pos.CENTER);
            label.setPadding(new Insets(5));
            timetableGridPane.add(label, 0, i + 1);
        }
        String[] timeSlots = {"08:00 - 08:45", "09:00 - 09:45", "10:00 - 10:45", "11:00 - 11:45", "12:00 - 12:45", "13:00 - 13:45", "14:00 - 14:45", "15:00 - 15:45", "16:00 - 16:45", "17:00 - 17:45", "18:00 - 18:45", "18:45 - 19:30", "19:30 - 20:15"};
        for (int i = 0; i < timeSlots.length; i++) {
            Label label = new Label(timeSlots[i]);
            label.getStyleClass().add("timeTableText");
            label.setMaxSize(150, 150);
            label.setMinSize(100, 100);
            label.setAlignment(Pos.CENTER);
            label.setWrapText(true);
            label.setPadding(new Insets(5));
            timetableGridPane.add(label, i + 1, 0);
        }
        for (ClassAndResult cr : classAndResults) {
            String moduleName = cr.getStudentClass().getModuleName();
            String lecturerInitials = cr.getStudentClass().getLecturerFirstName().charAt(0) + "" + cr.getStudentClass().getLecturerLastName().charAt(0);
            for (ClassTime ct : cr.getStudentClass().getClassTimes()) {
                for (int i = ct.getStartSlot(); i <= ct.getEndSlot(); i++) {
                    Label label = new Label(moduleName + "\n\n" + lecturerInitials + " (" + ct.getRoomNumber() + ")");
                    label.getStyleClass().add("timeTableText");
                    label.setMaxSize(150, 150);
                    label.setMinSize(100, 100);
                    label.setWrapText(true);
                    label.setAlignment(Pos.TOP_CENTER);
                    label.setPadding(new Insets(5));
                    label.setOnMouseClicked(e -> {
                        selectedClassComboBox.getSelectionModel().select(cr);
                        tabPane.getSelectionModel().select(0);
                    });
                    timetableGridPane.add(label, i, ct.getDayOfWeek());
                }
            }
        }
        timetableGridPane.setGridLinesVisible(true);
    }

    public void populateNoticeBoard() {
        ObservableList<NoticeBoard> noticeBoards = connectionHandler.noticeBoard;
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
            headingLabel.getStyleClass().add("noteHeading");
            Text descriptionLabel = new Text(nb.getDescription());
            descriptionLabel.setWrappingWidth(450);
            descriptionLabel.getStyleClass().add("noteDescription");
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
            shadowPane.getStyleClass().add("noteShadow");
            shadowPane.setRotate((Math.random() * 3.0) - 1.5);
            FlowPane.setMargin(shadowPane, new Insets(5));
            noticePanes.add(shadowPane);
        }
        bulletinBoardPane.getChildren().addAll(noticePanes);
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
