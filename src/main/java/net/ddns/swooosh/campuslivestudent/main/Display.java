package net.ddns.swooosh.campuslivestudent.main;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class Display extends Application {

    public static final File APPLICATION_FOLDER = new File(System.getProperty("user.home") + "/AppData/Local/Swooosh/CampusLiveStudent");
    public static final File LOCAL_CACHE = new File(APPLICATION_FOLDER.getAbsolutePath() + "/Local Cache");
    public static BooleanProperty enableAnimations = new SimpleBooleanProperty(true);
    public IntegerProperty currentTimeslot = new SimpleIntegerProperty();
    private ConnectionHandler connectionHandler = new ConnectionHandler();
    private ObservableList<ClassResultAttendance> classAndResults = FXCollections.observableArrayList();
    private Stage stage;
    private ImageView loginLogoImageView;
    private TextField studentNumberTextField;
    private PasswordField passwordField;
    private Hyperlink forgotPasswordHyperlink;
    private ProgressIndicator loginWaitIndicator;
    private Button loginButton;
    private VBox loginPane;
    private Text classText;
    private ComboBox<ClassResultAttendance> classSelectComboBox;
    private ListView<StudentFileObservable> classFilesListView;
    private GridPane timetableGridPane;
    private VBox resultsInnerPane;
    private FlowPane noticeboardInnerPane;
    private TableView<ContactDetails> contactTableView;
    private SideTabPane sideTabPane;
    private VBox studentPane;
    private StackPane backgroundPane;
    private StackPane contentPane;

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Start timeslot updater
        //<editor-fold desc="Timeslot Updater">
        new TimeslotUpdater().start();
        //</editor-fold>

        //Setup Stage
        //<editor-fold desc="Stage">
        stage = primaryStage;
        stage.setTitle("CampusLive Student " + getBuild());
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        stage.setMaxWidth(1920);
        stage.setMaxHeight(1080);
        stage.setMinWidth(1280);
        stage.setMinHeight(800);
        stage.setMaximized(true);
        //</editor-fold>

        //Setup tooltip delays
        //<editor-fold desc="Tooltip Delays">
        try {
            Class<?> clazz = Tooltip.class.getDeclaredClasses()[0];
            Constructor<?> constructor = clazz.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
            constructor.setAccessible(true);
            Object tooltipBehavior = constructor.newInstance(new Duration(300), new Duration(5000), new Duration(300), false);
            Field fieldBehavior = Tooltip.class.getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            fieldBehavior.set(Tooltip.class, tooltipBehavior);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //</editor-fold>

        //Setup login pane
        //<editor-fold desc="Login Pane">
        loginLogoImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        loginLogoImageView.setFitHeight(200);
        loginLogoImageView.setFitWidth(200);
        studentNumberTextField = new TextField("DV2015-0073"); //TODO
        studentNumberTextField.setPromptText("Student Number");
        studentNumberTextField.getStyleClass().add("login-fields");
        passwordField = new PasswordField();
        passwordField.setText("password"); //TODO
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("login-fields");
        passwordField.getStyleClass().add("login-password");
        loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");
        loginButton.setOnAction((ActionEvent e) -> {
            studentNumberTextField.setBorder(null);
            passwordField.setBorder(null);
            if (studentNumberTextField.getText().length() < 11) {
                studentNumberTextField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
                Tooltip studentNumberShortTooltip = new Tooltip("Student number too short");
                studentNumberShortTooltip.getStyleClass().add("login-tooltip");
                studentNumberTextField.setTooltip(studentNumberShortTooltip);
            } else if (passwordField.getText().length() < 5) {
                passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
                Tooltip passwordShortTooltip = new Tooltip("Password too short");
                passwordShortTooltip.getStyleClass().add("login-tooltip");
                passwordField.setTooltip(passwordShortTooltip);
            } else {
                loginPane.getChildren().clear();
                loginPane.getChildren().addAll(loginLogoImageView, loginWaitIndicator);
                BooleanProperty waitingForAuthorisation = new SimpleBooleanProperty(true);
                BooleanProperty authoriseResult = new SimpleBooleanProperty(false);
                Thread loginThread = new Thread(() -> {
                    if (connectionHandler.authorise(studentNumberTextField.getText(), passwordField.getText())) {
                        while (connectionHandler.studentInitialized()) ;
                        authoriseResult.setValue(true);
                    } else {
                        authoriseResult.setValue(false);
                    }
                    waitingForAuthorisation.set(false);
                });
                loginThread.start();
                waitingForAuthorisation.addListener(al -> {
                    if (authoriseResult.getValue()) {
                        Platform.runLater(() -> {
                            contentPane.getChildren().clear();
                            contentPane.getChildren().addAll(backgroundPane, studentPane);
                        });
                    } else {
                        Platform.runLater(() -> {
                            passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
                            Tooltip incorrectLoginTooltip = new Tooltip("Incorrect login details");
                            incorrectLoginTooltip.getStyleClass().add("login-tooltip");
                            passwordField.setTooltip(incorrectLoginTooltip);
                            loginPane.getChildren().clear();
                            loginPane.getChildren().addAll(loginLogoImageView, studentNumberTextField, passwordField, loginButton, forgotPasswordHyperlink);
                            passwordField.clear();
                            passwordField.requestFocus();
                        });
                    }
                });
            }
        });
        loginButton.setDefaultButton(true);
        forgotPasswordHyperlink = new Hyperlink("Forgot Password?");
        loginWaitIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        loginWaitIndicator.setPrefSize(206, 206);
        loginPane = new VBox(loginLogoImageView, studentNumberTextField, passwordField, loginButton, forgotPasswordHyperlink);
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setSpacing(20);
        loginPane.setPadding(new Insets(10));
        loginPane.setMaxSize(500, 500);
        //</editor-fold>

        //Setup class pane
        //<editor-fold desc="Class Pane">
        classText = new Text();
        classText.setOnMouseClicked(e -> classSelectComboBox.show());
        classText.getStyleClass().add("class-heading-text");
        classSelectComboBox = new ComboBox<>(classAndResults);
        classSelectComboBox.getStyleClass().add("class-select-combo-box");
        classSelectComboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
            if (!classSelectComboBox.getSelectionModel().isEmpty()) {
                classText.setText(classSelectComboBox.getSelectionModel().getSelectedItem().toString());
                if (classFilesListView != null) {
                    classFilesListView.getItems().clear();
                    ObservableList<StudentFileObservable> studentFiles = FXCollections.observableArrayList();
                    for (ClassFile classFile : classSelectComboBox.getSelectionModel().getSelectedItem().getStudentClass().getFiles()) {
                        studentFiles.add(new StudentFileObservable(classFile));
                    }
                    classFilesListView.setItems(studentFiles);
                }
            }
        });
        HBox classHeadingPane = new HBox(classSelectComboBox, classText);
        classHeadingPane.setSpacing(5);
        classHeadingPane.setAlignment(Pos.CENTER);
        Button classResultsButton = new Button("My Results");
        //classResultsButton.setOnAction(e -> new ResultDisplay(classSelectComboBox.getItems(), classSelectComboBox.getSelectionModel().getSelectedItem(), stage));
        classResultsButton.getStyleClass().add("class-button");
        classResultsButton.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.6), 5, 0.0, 2, 2));
        Button classContactLecturerButton = new Button("Contact Lecturer");
        classContactLecturerButton.setOnAction(e -> {
            if (connectionHandler.isLecturerOnline(classSelectComboBox.getSelectionModel().getSelectedItem().getStudentClass().getLecturer().getLecturerID())) {
                int contactMethod = UserNotification.showLecturerContactMethod();
                if (contactMethod == UserNotification.EMAIL_OPTION) {
                    //TODO open email to lecturer
                    System.out.println("email");
                } else if (contactMethod == UserNotification.DIRECT_OPTION) {
                    //TODO open direct message to lecturer
                    System.out.println("dm");
                }
            } else {
                //TODO open email to lecturer
                System.out.println("email");
            }
        });
        classContactLecturerButton.getStyleClass().add("class-button");
        classContactLecturerButton.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.6), 5, 0.0, 2, 2));
        HBox classActionsPane = new HBox(classResultsButton, classContactLecturerButton);
        classActionsPane.setAlignment(Pos.CENTER);
        classActionsPane.setSpacing(50);
        classFilesListView = new ListView<>();
        classFilesListView.getStyleClass().add("files-list-view");
        classFilesListView.setCellFactory(param -> new ListCell<StudentFileObservable>() {
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
                    File openFile;
                    if ((openFile = new File(LOCAL_CACHE.getAbsolutePath() + "/" + file.getClassFile().getClassID() + "/" + file.getClassFile().getFileName())).exists() && openFile.length() == file.getClassFile().getFileLength()) {
                        try {
                            java.awt.Desktop.getDesktop().open(openFile);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                MenuItem exportFileMenuItem = new MenuItem("Export File");
                exportFileMenuItem.setOnAction(event -> {
                    try {
                        DirectoryChooser dc = new DirectoryChooser();
                        dc.setTitle("Choose Directory to export to..");
                        File f = dc.showDialog(stage);
                        if (f != null) {
                            File target = new File(f.getAbsolutePath() + "/" + file.getClassFile().getFileName());
                            File toCopy = new File(LOCAL_CACHE.getAbsolutePath() + "/" + file.getClassFile().getClassID() + "/" + file.getClassFile().getFileName());
                            Files.copy(toCopy.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                MenuItem deleteFileMenuItem = new MenuItem("Delete File");
                deleteFileMenuItem.setOnAction(e -> connectionHandler.deleteFile(file.getClassFile().getClassID(), file.getClassFile().getFileName()));
                ContextMenu savedContextMenu = new ContextMenu(openFileMenuItem, exportFileMenuItem, deleteFileMenuItem);
                savedContextMenu.getStyleClass().add("file-context-menu");
                MenuItem downloadFileMenuItem = new MenuItem("Download File");
                downloadFileMenuItem.setOnAction(e -> {
                    file.getClassFile().setValue(2);
                    ConnectionHandler.FileDownloader fileDownloader = connectionHandler.new FileDownloader(file.getClassFile());
                    fileDownloader.start();
                    file.getClassFile().setFileDownloader(fileDownloader);
                    connectionHandler.student.update();
                });
                ContextMenu downloadContextMenu = new ContextMenu(downloadFileMenuItem);
                downloadContextMenu.getStyleClass().add("file-context-menu");

                setOnMouseClicked(evt -> {
                    if (evt.getClickCount() == 2) {
                        if (getGraphic().equals(savedImageView)) {
                            openFileMenuItem.fire();
                        } else if (getGraphic().equals(downloadImageView)) {
                            downloadFileMenuItem.fire();
                        }
                    }
                });

                if (empty || file == null || file.getClassFile().getFileName() == null) {
                    setText(null);
                    setContextMenu(null);
                    setGraphic(null);
                } else {
                    setText(getFileNameWithoutExtension(file.getClassFile().getFileName()));
                    setGraphicTextGap(35);
                    if (file.getClassFile().getValue() == 0) {
                        setGraphic(downloadImageView);
                        setContextMenu(downloadContextMenu);
                    } else if (file.getClassFile().getValue() == 1) {
                        setGraphic(savedImageView);
                        setContextMenu(savedContextMenu);
                    } else {
                        ProgressBar downloadProgressBar = new ProgressBar(0);
                        downloadProgressBar.getStyleClass().add("download-progress-bar");
                        downloadProgressBar.progressProperty().bind(file.progressProperty());
                        Text downloadPercentageText = new Text();
                        downloadPercentageText.textProperty().bind(file.progressProperty().multiply(100).asString("%.0f").concat("%"));
                        downloadPercentageText.getStyleClass().add("percentage-text");
                        StackPane downloadPane = new StackPane(downloadProgressBar, downloadPercentageText);
                        setGraphic(downloadPane);
                        setGraphicTextGap(16);
                    }
                }
            }
        });
        classFilesListView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) classFilesListView.getSelectionModel().clearSelection();
        });
        HBox classFilesPane = new HBox(classFilesListView);
        HBox.setHgrow(classFilesListView, Priority.ALWAYS);
        classFilesPane.setAlignment(Pos.CENTER);
        classFilesPane.setPadding(new Insets(15, 150, 30, 150));
        VBox classPane = new VBox(classHeadingPane, classActionsPane, classFilesPane);
        VBox.setVgrow(classFilesPane, Priority.ALWAYS);
        classPane.setSpacing(20);
        classPane.setPadding(new Insets(15));
        classPane.setAlignment(Pos.CENTER);
        //</editor-fold>

        //Setup timetable pane
        //<editor-fold desc="Timetable Pane">
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
        timetableGridPane.getColumnConstraints().addAll(columnConstraints);
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
        timetableGridPane.getRowConstraints().addAll(rowConstraints);
        timetableGridPane.getStyleClass().add("timetable-pane");
        timetableGridPane.setMaxSize(2000, 2000);
        VBox timetablePane = new VBox(timetableGridPane);
        VBox.setVgrow(timetableGridPane, Priority.ALWAYS);
        //</editor-fold>

        //Setup results pane
        //<editor-fold desc="Results Pane">
        VBox resultsPane = new VBox();
        resultsInnerPane = new VBox();
        resultsInnerPane.setAlignment(Pos.CENTER);
        resultsInnerPane.setSpacing(15);
        resultsInnerPane.setPadding(new Insets(20));
        ScrollPane resultsScrollPane = new ScrollPane(resultsInnerPane);
        VBox.setVgrow(resultsScrollPane, Priority.ALWAYS);
        resultsInnerPane.prefHeightProperty().bind(resultsScrollPane.heightProperty().subtract(46D));
        resultsScrollPane.setFitToWidth(true);
        resultsPane.getChildren().addAll(resultsScrollPane);
        resultsPane.setAlignment(Pos.CENTER);
        //</editor-fold>

        //Setup noticeboard pane
        //<editor-fold desc="Noticeboard Pane">
        noticeboardInnerPane = new FlowPane();
        populateNoticeBoard();
        noticeboardInnerPane.setAlignment(Pos.CENTER);
        noticeboardInnerPane.setHgap(10);
        noticeboardInnerPane.setVgap(10);
        noticeboardInnerPane.setOrientation(Orientation.HORIZONTAL);
        noticeboardInnerPane.setPadding(new Insets(20));
        noticeboardInnerPane.getStyleClass().add("noticeboard-pane");
        ScrollPane noticeboardScrollPane = new ScrollPane(new StackPane(noticeboardInnerPane));
        noticeboardInnerPane.prefHeightProperty().bind(noticeboardScrollPane.heightProperty().subtract(2D));
        noticeboardScrollPane.setFitToWidth(true);
        VBox noticeboardPane = new VBox(noticeboardScrollPane);
        VBox.setVgrow(noticeboardScrollPane, Priority.ALWAYS);
        //</editor-fold>

        //Setup contact details pane
        //<editor-fold desc="Contact Details Pane">
        Text contactText = new Text("Contact Details");
        contactText.getStyleClass().add("heading-text");
        contactTableView = new TableView<>();
        TableColumn<ContactDetails, String> nameTableColumn = new TableColumn<>("Name");
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<ContactDetails, String> positionTableColumn = new TableColumn<>("Position");
        positionTableColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        TableColumn<ContactDetails, String> telephoneTableColumn = new TableColumn<>("Contact Number");
        telephoneTableColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        TableColumn<ContactDetails, String> emailTableColumn = new TableColumn<>("Email");
        emailTableColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        contactTableView.getColumns().addAll(nameTableColumn, positionTableColumn, telephoneTableColumn, emailTableColumn);
        VBox contactPane = new VBox(contactText, contactTableView);
        contactPane.setSpacing(15);
        contactPane.setPadding(new Insets(15, 150, 150, 150));
        contactPane.setAlignment(Pos.CENTER);
        VBox.setVgrow(contactTableView, Priority.ALWAYS);
        populateContactDetails();
        //</editor-fold>

        //Setup important dates pane
        //<editor-fold desc="Important Dates">
        VBox importantDatesPane = new VBox(new Label("Content"));
        //</editor-fold>

        //Setup settings pane
        //<editor-fold desc="Settings Pane">
        Text settingsText = new Text("Settings");
        settingsText.getStyleClass().add("heading-text");
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setMinSize(250, 50);
        changePasswordButton.getStyleClass().add("settingsButton");
        Button aboutButton = new Button("About");
        aboutButton.setMinSize(250, 50);
        aboutButton.getStyleClass().add("settingsButton");
        Button logoutButton = new Button("Log Out");
        logoutButton.setMinSize(250, 50);
        logoutButton.getStyleClass().add("settingsButton");
        ToggleSlider toggleSlider = new ToggleSlider(true);
        enableAnimations.bind(toggleSlider.enabled);
        VBox settingsButtonsPane = new VBox(changePasswordButton, aboutButton, logoutButton, toggleSlider);
        settingsButtonsPane.setAlignment(Pos.TOP_CENTER);
        settingsButtonsPane.setSpacing(25);
        VBox settingsInnerPane = new VBox(settingsText, settingsButtonsPane);
        settingsInnerPane.getChildren().addAll();
        settingsInnerPane.setSpacing(25);
        settingsInnerPane.setPadding(new Insets(0, 0, 15, 0));
        settingsInnerPane.setAlignment(Pos.CENTER);
        settingsInnerPane.setStyle("-fx-background-color: rgba(0,135,167,0.8);" +
                " -fx-background-radius: 15");
        settingsInnerPane.setMaxSize(450, 450);
        settingsInnerPane.setMinSize(450, 450);
        VBox settingsPane = new VBox(settingsInnerPane);
        settingsPane.setAlignment(Pos.CENTER);
        //</editor-fold>

        //Setup side tab pane
        //<editor-fold desc="Side Tab Pane">
        SideTab classSideTab = new SideTab(new Image(getClass().getClassLoader().getResourceAsStream("class.png")), "Class", classPane);
        SideTab rosterSideTab = new SideTab(new Image(getClass().getClassLoader().getResourceAsStream("roster.png")), "Timetable", timetablePane);
        SideTab resultsSideTab = new SideTab(new Image(getClass().getClassLoader().getResourceAsStream("results.png")), "Results", resultsPane);
        SideTab noticeboardSideTab = new SideTab(new Image(getClass().getClassLoader().getResourceAsStream("noticeboard.png")), "Noticeboard", noticeboardPane);
        SideTab contactSideTab = new SideTab(new Image(getClass().getClassLoader().getResourceAsStream("contact.png")), "Contact", contactPane);
        SideTab importantDatesSideTab = new SideTab(new Image(getClass().getClassLoader().getResourceAsStream("importantDates.png")), "Important Dates", importantDatesPane);
        SideTab settingsSideTab = new SideTab(new Image(getClass().getClassLoader().getResourceAsStream("settings2.png")), "Settings", settingsPane);
        sideTabPane = new SideTabPane(classSideTab, rosterSideTab, resultsSideTab, noticeboardSideTab, contactSideTab, importantDatesSideTab, settingsSideTab);
        //</editor-fold>

        //Setup top pane
        //<editor-fold desc="Top Pane">
        ImageView optionsImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Options.png")));
        ImageView logoImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        logoImageView.setFitHeight(32);
        logoImageView.setFitWidth(32);
        optionsImageView.setOnMouseClicked(e -> sideTabPane.setExtended(!sideTabPane.getExtended()));
        Label campusLabel = new Label("Durbanville");
        campusLabel.getStyleClass().add("top-pane-text");
        HBox campusPane = new HBox(campusLabel);
        Label connectionTypeLabel = new Label();
        connectionTypeLabel.getStyleClass().add("top-pane-text");
        HBox connectionTypePane = new HBox(connectionTypeLabel);
        connectionTypePane.setAlignment(Pos.CENTER);
        Label studentInfoLabel = new Label("Stephan Malan DV2015-0073");
        studentInfoLabel.getStyleClass().add("top-pane-text");
        HBox studentInfoPane = new HBox(studentInfoLabel);
        studentInfoPane.setAlignment(Pos.CENTER_RIGHT);
        HBox topPane = new HBox(optionsImageView, logoImageView, campusLabel, connectionTypePane, studentInfoPane);
        campusPane.minWidthProperty().bind(studentInfoLabel.widthProperty().subtract(107D));
        campusPane.maxWidthProperty().bind(studentInfoLabel.widthProperty().subtract(107D));
        HBox.setHgrow(connectionTypePane, Priority.ALWAYS);
        topPane.getStyleClass().add("top-pane");
        //</editor-fold>

        //Setup background pane
        //<editor-fold desc="Background Pane">
        backgroundPane = new StackPane();
        backgroundPane.getStyleClass().add("background-pane");
        backgroundPane.setEffect(new GaussianBlur(50));
        //</editor-fold>

        //Setup student pane
        //<editor-fold desc="Student Pane">
        studentPane = new VBox(topPane, sideTabPane);
        VBox.setVgrow(sideTabPane, Priority.ALWAYS);
        //</editor-fold>

        //Setup content pane
        //<editor-fold desc="Content Pane">
        contentPane = new StackPane(backgroundPane, loginPane);
        //</editor-fold>

        //Setup student update listener
        //<editor-fold desc="Student Update Listener">
        connectionHandler.student.update.addListener((observable, oldV, newV) -> {
            if (newV) {
                System.out.println("Student updated");
                if (connectionHandler.student.getStudent() != null) {
                    String prevSelected = null;
                    if (!classSelectComboBox.getSelectionModel().isEmpty()) {
                        prevSelected = classSelectComboBox.getSelectionModel().getSelectedItem().getStudentClass().getModuleNumber();
                    }
                    classAndResults.clear();
                    classAndResults.addAll(connectionHandler.student.getStudent().getClassResultAttendances());
                    resultsInnerPane.getChildren().clear();
                    for (ClassResultAttendance result : classAndResults) {
                        System.out.println(result.getResults().size());
                        resultsInnerPane.getChildren().add(new ResultPane(result));
                    }
                    if (prevSelected == null) {
                        SetTimeSlot:
                        if (!classSelectComboBox.getItems().isEmpty()) {
                            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
                            for (ClassResultAttendance car : classSelectComboBox.getItems()) {
                                for (ClassTime ct : car.getStudentClass().getClassTimes()) {
                                    if (ct.getDayOfWeek() == dayOfWeek && currentTimeslot.get() >= ct.getStartSlot() && currentTimeslot.get() <= ct.getEndSlot()) {
                                        classSelectComboBox.getSelectionModel().select(car);
                                        break SetTimeSlot;
                                    }
                                }
                            }
                            Platform.runLater(() -> classSelectComboBox.getSelectionModel().select(0));
                        }
                    } else {
                        for (ClassResultAttendance car : classAndResults) {
                            if (car.getStudentClass().getModuleNumber().equals(prevSelected)) {
                                classSelectComboBox.getSelectionModel().select(car);
                            }
                        }
                    }
                    populateTimetable();
                    populateContactDetails();
                }
            }
        });
        //</editor-fold>

        //Setup noticeboard update listener
        //<editor-fold desc="Noticeboard Update Listener">
        connectionHandler.notices.addListener((InvalidationListener) e -> {
            populateNoticeBoard();
        });
        connectionHandler.notifications.addListener((InvalidationListener) e -> {
            System.out.println("lol");
            populateNoticeBoard();
        });
        //</editor-fold>

        //Setup contact details update listener
        //<editor-fold desc="Contact Details Update Listener">
        connectionHandler.contactDetails.addListener((InvalidationListener) e -> {
            populateContactDetails();
        });
        //</editor-fold>

        //Setup stage close listener
        //<editor-fold desc="Stage Close Listener">
        stage.setOnCloseRequest(e -> System.exit(0));
        //</editor-fold>

        //Setup login pane animation
        //<editor-fold desc="Login Pane animation">
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), loginPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
        //</editor-fold>

        //Setup scene
        //<editor-fold desc="Scene">
        Scene scene = new Scene(contentPane);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("CampusLiveStyle.css").toExternalForm());
        //</editor-fold>

        //Set and display scene
        //<editor-fold desc="Display Scene">
        stage.setScene(scene);
        stage.show();
        //</editor-fold>
    }

    private String getFileNameWithoutExtension(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    private int getCurrentTimeSlot() {
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

    private void populateTimetable() {
        List<ClassResultAttendance> classAndResults = classSelectComboBox.getItems();
        timetableGridPane.getChildren().clear();
        String[] weekdays = {"Mo", "Tu", "We", "Th", "Fr"};
        for (int i = 0; i < 5; i++) {
            Label label = new Label(weekdays[i]);
            label.getStyleClass().add("timetable-weekdays");
            timetableGridPane.add(label, 0, i + 1);
        }
        String[] timeSlots = {"08:00 - 08:45", "09:00 - 09:45", "10:00 - 10:45", "11:00 - 11:45", "12:00 - 12:45", "13:00 - 13:45", "14:00 - 14:45", "15:00 - 15:45", "16:00 - 16:45", "17:00 - 17:45", "18:00 - 18:45", "18:45 - 19:30", "19:30 - 20:15"};
        for (int i = 0; i < timeSlots.length; i++) {
            Label label = new Label(timeSlots[i]);
            label.getStyleClass().add("timetable-text");
            label.setAlignment(Pos.CENTER);
            timetableGridPane.add(label, i + 1, 0);
        }
        int classNumber = 0;
        for (ClassResultAttendance cr : classAndResults) {
            String moduleName = cr.getStudentClass().getModuleName();
            String lecturerInitials = cr.getStudentClass().getLecturer().getFirstName().charAt(0) + "" + cr.getStudentClass().getLecturer().getLastName().charAt(0);
            for (ClassTime ct : cr.getStudentClass().getClassTimes()) {
                for (int i = ct.getStartSlot(); i <= ct.getEndSlot(); i++) {
                    TimetableBlock timetableBlock = new TimetableBlock(moduleName + "\n\n" + lecturerInitials + " (" + ct.getRoomNumber() + ")", classNumber);
                    timetableBlock.setOnMouseClicked(e -> {
                        Platform.runLater(() -> {
                            classSelectComboBox.getSelectionModel().select(cr);
                            sideTabPane.select(0);
                        });
                    });
                    timetableGridPane.add(timetableBlock, i, ct.getDayOfWeek());
                }
            }
            classNumber++;
        }
        timetableGridPane.setGridLinesVisible(true);
    }

    private void populateNoticeBoard() {
        ObservableList<StackPane> noticePanes = FXCollections.observableArrayList();
        for (Notification nb : connectionHandler.notifications) {
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
        for (Notice nb : connectionHandler.notices) {
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
        noticeboardInnerPane.getChildren().clear();
        noticeboardInnerPane.getChildren().addAll(noticePanes);
    }

    private void populateContactDetails() {
        ObservableList<ContactDetails> lecturerContactDetails = FXCollections.observableArrayList();
        for (ClassResultAttendance cra : classAndResults) {
            Lecturer lecturer = cra.getStudentClass().getLecturer();
            ContactDetails newContactDetails = new ContactDetails(lecturer.getFirstName() + "" + lecturer.getLastName(), "Lecturer", lecturer.getContactNumber(), lecturer.getEmail());
            if (!lecturerContactDetails.contains(newContactDetails)) {
                System.out.println("test");
                lecturerContactDetails.add(newContactDetails);
            }
        }
        ObservableList<ContactDetails> contactDetails = FXCollections.observableArrayList();
        contactDetails.addAll(connectionHandler.contactDetails);
        contactDetails.addAll(lecturerContactDetails);
        contactTableView.setItems(contactDetails);
    }

    private String getBuild() {
        try {
            Scanner scn = new Scanner(new File(APPLICATION_FOLDER.getAbsolutePath() + "/Version.txt"));
            return "(Build " + scn.nextLine() + ")";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "(Build N/A)";
    }

    public class TimeslotUpdater extends Thread {
        @Override
        public void run() {
            while (true) {
                currentTimeslot.set(getCurrentTimeSlot());
                int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
                Platform.runLater(() -> {
                    for (Node node : timetableGridPane.getChildren()) {
                        if (node != null && !(node instanceof Group) && GridPane.getColumnIndex(node) == currentTimeslot.get() && dayOfWeek > 0 && dayOfWeek < 6 && GridPane.getRowIndex(node) == dayOfWeek) {
                            if (node != null && node instanceof TimetableBlock) {
                                ((TimetableBlock) node).setSelected(true);
                            }
                        } else {
                            if (node != null && node instanceof TimetableBlock) {
                                ((TimetableBlock) node).setSelected(false);
                            }
                        }
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        if (!LOCAL_CACHE.exists()) {
            if (!LOCAL_CACHE.mkdirs()) {
                System.exit(0);
            }
        }
        launch(args);
    }

}
