package net.ddns.swooosh.campuslivestudent.main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.ClassAndResult;
import models.Result;

public class ResultDisplay extends Application {

    private ObservableList<ClassAndResult> classAndResults;
    private ClassAndResult startClass;
    private Stage parent;

    public ResultDisplay(ObservableList<ClassAndResult> classAndResults, Stage parent) {
        this.classAndResults = classAndResults;
        this.parent = parent;
        try {
            start(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ResultDisplay(ObservableList<ClassAndResult> classAndResults, ClassAndResult startClass, Stage parent) {
        this.classAndResults = classAndResults;
        this.startClass = startClass;
        this.parent = parent;
        try {
            start(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("My Results");
        stage.getIcons().addAll(new Image(getClass().getClassLoader().getResourceAsStream("CLLogo.png")));
        stage.setResizable(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);

        //Setup content pane
        VBox contentPane = new VBox();
        HBox buttonPane = new HBox();
        Text headingText = new Text("My Results");
        headingText.setStyle("-fx-font-size: 28pt;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
        HBox headingPane = new HBox(headingText);
        headingPane.setStyle("-fx-background-color: #FECD34;" +
                " -fx-border-color: black;" +
                " -fx-border-width: 3;" +
                " -fx-border-radius: 30;" +
                " -fx-background-radius: 30;");
        headingPane.setAlignment(Pos.CENTER);
        headingPane.setPadding(new Insets(5, 150, 5, 150));
        HBox headingMainPane = new HBox(headingPane);
        headingMainPane.setAlignment(Pos.CENTER);
        HBox.setHgrow(headingPane, Priority.NEVER);
        ObservableList<ClassAndResult> resultsToDisplay = FXCollections.observableArrayList();
        if (startClass != null) {
            resultsToDisplay.add(startClass);
        } else {
            resultsToDisplay.addAll(classAndResults);
        }
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> stage.close());
        closeButton.setStyle(" -fx-background-radius: 17;" +
                " -fx-border-radius: 17;" +
                " -fx-border-width: 2;" +
                " -fx-background-color: #FECD34;" +
                " -fx-border-color: black;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-weight: bold;" +
                " -fx-font-size: 20;" +
                " -fx-background-insets: 0;");
        closeButton.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.6), 5, 0.0, 2, 2));
        Button viewAllButton = new Button("View All");
        viewAllButton.setOnAction(e -> {
            contentPane.getChildren().clear();
            contentPane.getChildren().addAll(headingMainPane, getAllResultsPane(classAndResults), buttonPane);
        });
        viewAllButton.setStyle(" -fx-background-radius: 17;" +
                " -fx-border-radius: 17;" +
                " -fx-border-width: 2;" +
                " -fx-background-color: #FECD34;" +
                " -fx-border-color: black;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-weight: bold;" +
                " -fx-font-size: 20;" +
                " -fx-background-insets: 0;");
        viewAllButton.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.6), 5, 0.0, 2, 2));
        if (startClass == null) {
            buttonPane.getChildren().addAll(closeButton);
        } else {
            buttonPane.getChildren().addAll(closeButton, viewAllButton);
        }
        buttonPane.setSpacing(15);
        buttonPane.setAlignment(Pos.CENTER);
        contentPane.getChildren().addAll(headingMainPane, getAllResultsPane(resultsToDisplay), buttonPane);
        contentPane.setSpacing(15);
        contentPane.setPadding(new Insets(10, 15, 10, 15));
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setStyle("-fx-background-color: transparent;");

        //Setup scene
        Scene scene = new Scene(contentPane, Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("CampusLiveStyle.css").toExternalForm());

        //Select and display scene
        stage.setScene(scene);
        stage.showAndWait();
    }

    public ScrollPane getAllResultsPane(ObservableList<ClassAndResult> resultsToDisplay) {
        ObservableList<StackPane> results = FXCollections.observableArrayList();
        for (ClassAndResult car : resultsToDisplay) {
            VBox resultPane = new VBox();
            ImageView staple1ImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Staple.png")));
            staple1ImageView.setFitHeight(17);
            staple1ImageView.setFitWidth(58);
            staple1ImageView.setRotate(-30);
            ImageView staple2ImageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Staple.png")));
            staple2ImageView.setFitHeight(17);
            staple2ImageView.setFitWidth(58);
            staple2ImageView.setRotate(30);
            HBox staple1Pane = new HBox(staple1ImageView);
            staple1Pane.setAlignment(Pos.CENTER_LEFT);
            HBox staple2Pane = new HBox(staple2ImageView);
            staple2Pane.setAlignment(Pos.CENTER_RIGHT);
            HBox stapleMainPane = new HBox(staple1Pane, staple2Pane);
            stapleMainPane.setPadding(new Insets(-25, -15, -40, -15));
            HBox.setHgrow(staple1Pane, Priority.ALWAYS);
            Label classLabel = new Label(car.getStudentClass().getModuleName());
            classLabel.setStyle("-fx-font-family: Verdana;" +
                    " -fx-font-size: 18pt;" +
                    " -fx-text-fill: midnightblue;" +
                    " -fx-font-weight: bold;");
            ObservableList<Label> labels = FXCollections.observableArrayList();
            Double weightedTotal = 0D;
            labels.add(classLabel);
            for (Result r : car.getResults()) {
                Double resultPerc = 0D;
                if (r.getResult() != -1D) {
                    resultPerc = r.getResult() * 100 / r.getResultMax();
                }
                Double weightedResult = resultPerc * r.getFinalWeight();
                weightedTotal += weightedResult;
                Label result;
                if (r.getResult() == -1D) {
                    result = new Label(String.format("%-25s N/A N/A", r.getResultName(), Double.valueOf(resultPerc).intValue(), Double.valueOf(weightedResult).intValue()));
                } else {
                    result = new Label(String.format("%-25s %3d %3d", r.getResultName(), Double.valueOf(resultPerc).intValue(), Double.valueOf(weightedResult).intValue()));
                }
                result.setStyle("-fx-font-family: monospace;" +
                        " -fx-font-size: 14pt;" +
                        " -fx-font-weight: bold;");
                labels.add(result);
            }
            Label totalLabel = new Label(String.format("%-29s %3d", "Total", Double.valueOf(weightedTotal).intValue()));
            totalLabel.setStyle("-fx-font-family: monospace;" +
                    " -fx-font-size: 14pt;" +
                    " -fx-font-weight: bold;");
            labels.add(totalLabel);
            resultPane.getChildren().add(stapleMainPane);
            resultPane.getChildren().addAll(labels);
            resultPane.setPadding(new Insets(10));
            resultPane.setSpacing(5);
            resultPane.setAlignment(Pos.CENTER);
            resultPane.setStyle("-fx-background-image: url(\"PaperTexture.png\");" +
                    " -fx-background-size: 25%;");
            StackPane shadowPane = new StackPane(resultPane);
            shadowPane.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0.0, -5, 5);");
            results.add(shadowPane);
        }
        VBox allResultsPane = new VBox();
        allResultsPane.getChildren().addAll(results);
        allResultsPane.setSpacing(25);
        allResultsPane.setPadding(new Insets(25, 220, 25, 220));
        allResultsPane.setAlignment(Pos.CENTER);
        allResultsPane.setStyle("-fx-background-color: transparent;");
        ScrollPane scrollPane = new ScrollPane(allResultsPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxSize(1200, 600);
        scrollPane.setMinSize(1200, 600);
        scrollPane.setStyle("-fx-border-color: black;" +
                " -fx-border-width: 10;" +
                " -fx-border-radius: 15;" +
                " -fx-border-insets: -10;" +
                " -fx-background-radius: 15;" +
                " -fx-background-insets: -5;" +
                " -fx-background-image: url(\"Carpet.jpg\");" +
                " -fx-background-size: 45%");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return scrollPane;
    }
}
