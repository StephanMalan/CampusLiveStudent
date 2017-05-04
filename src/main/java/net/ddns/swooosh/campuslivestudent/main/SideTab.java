package net.ddns.swooosh.campuslivestudent.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class SideTab extends HBox {

    private ImageView graphic;
    private String text;
    private Pane content;
    private int spacing;
    private Insets insets;
    private int iconSize;

    public SideTab(Image image, String text, Pane content) {
        this.graphic = new ImageView(image);
        this.text = text;
        this.content = content;
        spacing = 15;
        insets = new Insets(10);
        iconSize = 32;
        graphic.setFitHeight(iconSize);
        graphic.setFitWidth(iconSize);
        setSpacing(spacing);
        setPadding(insets);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().add(graphic);
        getStyleClass().add("side-tab");
    }

    public ImageView getGraphic() {
        return graphic;
    }

    public String getText() {
        return text;
    }

    public Pane getContent() {
        return content;
    }

    public void setExtended(Boolean extended) {
        if (extended) {
            Text tabHeaderText = new Text(text);
            tabHeaderText.getStyleClass().add("side-tab-text");
            getChildren().add(1, tabHeaderText);
        } else {
            getChildren().remove(1);
        }
    }

}
