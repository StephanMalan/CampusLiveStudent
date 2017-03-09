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
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.ddns.swooosh.campuslivestudent.models.ClassAndResult;
import net.ddns.swooosh.campuslivestudent.models.Result;

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
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setMaxWidth(800);
        stage.setMaxHeight(600);
        stage.setResizable(true);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);

        //Setup content pane
        VBox contentPane = new VBox();
        HBox buttonPane = new HBox();
        Text headingText = new Text("My Results");
        headingText.setStyle("-fx-font-size: 28pt;" +
                " -fx-fill: #FECD34;" +
                " -fx-stroke: black;" +
                " -fx-font-family: \"Verdana\";" +
                " -fx-font-weight: bold;" +
                " -fx-background-color: linear-gradient(#ffffff, #d3d3d3);" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
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
                " -fx-background-color: white;" +
                " -fx-border-color: black;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-weight: bold;" +
                " -fx-font-size: 20;");
        Button viewAllButton = new Button("View All");
        viewAllButton.setOnAction(e -> {
            contentPane.getChildren().clear();
            contentPane.getChildren().addAll(headingText, getAllResultsPane(classAndResults), buttonPane);
        });
        viewAllButton.setStyle(" -fx-background-radius: 17;" +
                " -fx-border-radius: 17;" +
                " -fx-border-width: 2;" +
                " -fx-background-color: white;" +
                " -fx-border-color: black;" +
                " -fx-font-family: Verdana;" +
                " -fx-font-weight: bold;" +
                " -fx-font-size: 20;");
        if (startClass == null) {
            buttonPane.getChildren().addAll(closeButton);
        } else {
            buttonPane.getChildren().addAll(closeButton, viewAllButton);
        }
        buttonPane.setSpacing(15);
        buttonPane.setAlignment(Pos.CENTER);
        contentPane.getChildren().addAll(headingText, getAllResultsPane(resultsToDisplay), buttonPane);
        contentPane.setSpacing(15);
        contentPane.setPadding(new Insets(10, 30, 30, 15));
        contentPane.setAlignment(Pos.CENTER);

        //Setup background pane
        StackPane backgroundPane = new StackPane();
        backgroundPane.setStyle("-fx-background-image: url(\"SquareBackground.png\");" +
                " -fx-background-size: auto 100%;" +
                " -fx-background-position: center;" +
                " -fx-background-repeat: no-repeat;" +
                " -fx-background-color: white;" +
                " -fx-accent: white");
        backgroundPane.setEffect(new GaussianBlur(10000));

        //Setup main pane
        StackPane mainPane = new StackPane(backgroundPane, contentPane);
        mainPane.setAlignment(Pos.CENTER);

        //Setup scene
        Scene scene = new Scene(mainPane);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("CampusLiveStyle.css").toExternalForm());

        //Select and display scene
        stage.setScene(scene);
        stage.showAndWait();
    }

    public ScrollPane getAllResultsPane(ObservableList<ClassAndResult> resultsToDisplay) {
        ObservableList<VBox> results = FXCollections.observableArrayList();
        for (ClassAndResult car : resultsToDisplay) {
            VBox resultPane = new VBox();
            resultPane.setStyle("-fx-border-color: deepskyblue;" +
                    " -fx-border-width: 3;" +
                    " -fx-border-radius: 10;");
            Label classLabel = new Label(car.getStudentClass().getModuleName());
            classLabel.setStyle("-fx-font-family: Verdana;" +
                    " -fx-font-size: 18pt;" +
                    " -fx-text-fill: midnightblue;" +
                    " -fx-font-weight: bold;" +
                    " -fx-effect: null;");
            ObservableList<Label> labels = FXCollections.observableArrayList();
            Double weightedTotal = 0D;
            labels.add(classLabel);
            for (Result r : car.getResults()) {
                Double resultPerc = 0D;
                if (r.getResult() != -1D) {
                    resultPerc = r.getResult() * 100 / r.getResultMax();
                }
                Double weightedResult = resultPerc * r.getResultsWeight();
                weightedTotal += weightedResult;
                //TODO check if total is N/A
                Label result = new Label(String.format("%-25s %3d %3d", r.getResultName(), Double.valueOf(resultPerc).intValue(), Double.valueOf(weightedResult).intValue()));
                result.setStyle("-fx-font-family: monospace;" +
                        " -fx-font-size: 14pt;" +
                        " -fx-effect: null;");
                labels.add(result);
            }
            Label totalLabel = new Label(String.format("%-29s %3d", "Total", Double.valueOf(weightedTotal).intValue()));
            totalLabel.setStyle("-fx-font-family: monospace;" +
                    " -fx-font-size: 14pt;" +
                    " -fx-effect: null;");
            labels.add(totalLabel);
            resultPane.getChildren().addAll(labels);
            resultPane.setPadding(new Insets(15));
            resultPane.setSpacing(5);
            resultPane.setAlignment(Pos.CENTER);
            results.add(resultPane);
        }
        VBox allResultsPane = new VBox();
        allResultsPane.getChildren().addAll(results);
        allResultsPane.setSpacing(15);
        allResultsPane.setPadding(new Insets(15));
        allResultsPane.setAlignment(Pos.CENTER);
        allResultsPane.setStyle("-fx-background-color: transparent;");
        ScrollPane scrollPane = new ScrollPane(allResultsPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-border-color: black;" +
                " -fx-border-width: 2;" +
                " -fx-border-radius: 15;" +
                " -fx-border-insets: -5;" +
                " -fx-background-radius: 15;" +
                " -fx-background-insets: -5;" +
                " -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return scrollPane;
    }
}
