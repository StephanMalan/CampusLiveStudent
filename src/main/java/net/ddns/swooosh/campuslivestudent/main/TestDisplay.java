package net.ddns.swooosh.campuslivestudent.main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestDisplay extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        stage.setMaximized(true);
        stage.setTitle("CampusLive Student (Build 12)");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));

        //Setup class pane
        Label titleLabel = new Label("Object oriented");
        VBox classPane = new VBox();

        //Setup timetable pane
        VBox timetablePane = new VBox();

        //Setup results pane
        VBox resultsPane = new VBox();

        //Setup noticeboard pane
        VBox noticeboardsPane = new VBox();

        //Setup contact pane
        VBox contactPane = new VBox();

        //Setup settings pane
        VBox settingsPane = new VBox();

        Tab classTab = new Tab("", classPane);
        ImageView classImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("class.png")));
        classImageView.setFitWidth(35);
        classImageView.setFitHeight(35);
        VBox classImgPane = new VBox(classImageView);
        classTab.setGraphic(classImgPane);
        Tab timetableTab = new Tab("", timetablePane);
        ImageView timetableImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("timetable.png")));
        timetableImageView.setFitWidth(35);
        timetableImageView.setFitHeight(35);
        timetableTab.setGraphic(timetableImageView);
        Tab resultsTab = new Tab("", resultsPane);
        ImageView resultsImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("results.png")));
        resultsImageView.setFitWidth(35);
        resultsImageView.setFitHeight(35);
        resultsTab.setGraphic(resultsImageView);
        Tab noticeboardTab = new Tab("", noticeboardsPane);
        ImageView noticeboardImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("noticeboard.png")));
        noticeboardImageView.setFitWidth(35);
        noticeboardImageView.setFitHeight(35);
        noticeboardTab.setGraphic(noticeboardImageView);
        Tab contactTab = new Tab("", contactPane);
        ImageView contactImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("contact.png")));
        contactImageView.setFitWidth(35);
        contactImageView.setFitHeight(35);
        contactTab.setGraphic(contactImageView);
        Tab settingsTab = new Tab("", settingsPane);
        ImageView settingsImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("settings2.png")));
        settingsImageView.setFitWidth(35);
        settingsImageView.setFitHeight(35);
        settingsTab.setGraphic(settingsImageView);

        TabPane tabPane = new TabPane(classTab, timetableTab, resultsTab, noticeboardTab, contactTab, settingsTab);
        tabPane.setSide(Side.LEFT);
        tabPane.setRotateGraphic(true);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setTabMaxHeight(60);
        tabPane.setTabMinHeight(60);
        tabPane.setTabMaxWidth(50);
        tabPane.setTabMinWidth(50);

        HBox hbox = new HBox(tabPane);
        HBox.setHgrow(tabPane, Priority.ALWAYS);

        ImageView optionsImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Options.png")));
        optionsImageView.setOnMouseClicked(e -> {
            System.out.println(tabPane.getTabMinHeight() + ":" + tabPane.getTabMaxHeight());
            if (tabPane.getTabMaxHeight() == 60) {
                System.out.println("open");
                tabPane.getStyleClass().add("tabEnlarge");
                tabPane.setTabMaxHeight(220);
                tabPane.setTabMinHeight(220);
                tabPane.setTabMaxWidth(180);
                tabPane.setTabMinWidth(180);
                classTab.setText("Class   ");
                timetableTab.setText("Roster  ");
                resultsTab.setText("Results ");
                noticeboardTab.setText("Notices ");
                contactTab.setText("Contacts");
                settingsTab.setText("Settings");
            } else {
                System.out.println("close");
                tabPane.getStyleClass().remove("tabEnlarge");
                tabPane.setTabMaxHeight(60);
                tabPane.setTabMinHeight(60);
                tabPane.setTabMaxWidth(50);
                tabPane.setTabMinWidth(50);
                classTab.setText("");
                timetableTab.setText("");
                resultsTab.setText("");
                noticeboardTab.setText("");
                contactTab.setText("");
                settingsTab.setText("");
            }
        });
        Label connectionTypeLabel = new Label("On-Campus");
        connectionTypeLabel.setStyle("-fx-text-fill: #D2DB0E; -fx-font-family: Verdana; -fx-font-size: 18");
        Label studentInfoLabel = new Label("Stephan Malan DV2015-0073");
        studentInfoLabel.setStyle("-fx-text-fill: #D2DB0E; -fx-font-family: Verdana; -fx-font-size: 18");

        HBox optionsPane = new HBox(optionsImageView);
        optionsPane.setAlignment(Pos.CENTER);
        optionsPane.setPadding(new Insets(0, 0, 0, 18));
        HBox studentInfoPane = new HBox(studentInfoLabel);
        studentInfoPane.setAlignment(Pos.CENTER_RIGHT);
        studentInfoPane.setPadding(new Insets(0, 10, 0, 0));
        HBox topInnerPane = new HBox(optionsPane, studentInfoPane);
        HBox.setHgrow(studentInfoPane, Priority.ALWAYS);
        StackPane topPane = new StackPane(topInnerPane, connectionTypeLabel);
        topPane.setAlignment(Pos.CENTER);
        topPane.setStyle("-fx-min-height: 50; -fx-background-color: #003057;");
        StackPane backgroundLogoPane = new StackPane();
        backgroundLogoPane.getStyleClass().add("backgroundPane");
        backgroundLogoPane.setEffect(new GaussianBlur(50));

        VBox backgroundPane = new VBox(backgroundLogoPane);
        VBox.setVgrow(backgroundLogoPane, Priority.ALWAYS);

        VBox contentPane = new VBox(topPane, hbox);
        VBox.setVgrow(hbox, Priority.ALWAYS);

        StackPane rootPane = new StackPane(backgroundPane, contentPane);

        Scene scene = new Scene(rootPane);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("CampusLiveStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
