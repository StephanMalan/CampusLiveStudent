package net.ddns.swooosh.campuslivestudent.main;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.WritableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class ToggleSlider extends HBox {

    public BooleanProperty enabled;

    public ToggleSlider(Boolean enabled) {
        this.enabled = new SimpleBooleanProperty(enabled);
        init();
    }

    private void init() {
        setMaxWidth(50);
        setMinWidth(50);
        setMaxHeight(26);
        setMinHeight(26);
        setAlignment(Pos.CENTER_LEFT);
        Circle circle = new Circle(10, Color.WHITE);
        Pane filler = new Pane();
        WritableValue<Double> fillerValue = new WritableValue<Double>() {
            @Override
            public Double getValue() {
                return filler.getWidth();
            }

            @Override
            public void setValue(Double value) {
                filler.setMaxWidth(value);
                filler.setMinWidth(value);
            }
        };
        if (enabled.get()) {
            fillerValue.setValue(26D);
            setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(13), Insets.EMPTY)));
        } else {
            fillerValue.setValue(4D);
            setBackground(new Background(new BackgroundFill(Color.GREY, new CornerRadii(13), Insets.EMPTY)));
        }
        getChildren().addAll(filler, circle);
        setOnMouseClicked(e -> toggle());
        enabled.addListener(e -> {
            KeyValue keyValue;
            if (enabled.get()) {
                keyValue = new KeyValue(fillerValue, 26D);
                setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(13), Insets.EMPTY)));
            } else {
                keyValue = new KeyValue(fillerValue, 4D);
                setBackground(new Background(new BackgroundFill(Color.GREY, new CornerRadii(13), Insets.EMPTY)));
            }
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
            Timeline timeline = new Timeline(keyFrame);
            timeline.play();
        });
        getStyleClass().add("toggle-slide");
    }

    public void toggle() {
        enabled.setValue(!enabled.getValue());
    }

}
