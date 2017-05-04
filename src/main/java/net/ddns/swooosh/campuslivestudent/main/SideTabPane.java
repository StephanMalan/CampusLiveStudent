package net.ddns.swooosh.campuslivestudent.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SideTabPane extends HBox {

    private SideTab[] tabs;
    private SideTab selectedSideTab;
    private int spacing;
    private Insets insets;
    private VBox tabHeadersPane;
    private Boolean extended;

    public SideTabPane(SideTab... tabs) {
        this.tabs = tabs;
        extended = false;
        spacing = 10;
        insets = new Insets(10);
        init();
    }

    private void init() {
        tabHeadersPane = new VBox();
        selectedSideTab = tabs[0];
        for (int i = 0; i < tabs.length; i++) {
            if (i == tabs.length - 1) {
                Pane spacingPane = new Pane();
                tabHeadersPane.getChildren().add(spacingPane);
                VBox.setVgrow(spacingPane, Priority.ALWAYS);
            }
            SideTab tab = tabs[i];
            tab.setOnMouseClicked(evt -> {
                selectedSideTab = (SideTab) evt.getSource();
                updateSelected();
            });
            tabHeadersPane.getChildren().add(tab);
        }
        tabHeadersPane.setAlignment(Pos.CENTER);
        tabHeadersPane.getStyleClass().add("tab-header-pane");
        getChildren().add(0, tabHeadersPane);
        updateSelected();
    }

    private void updateSelected() {
        for (SideTab sideTab : tabs) {
            if (sideTab == selectedSideTab) {
                sideTab.getStyleClass().add("selected-side-tab");
            } else {
                sideTab.getStyleClass().removeAll("selected-side-tab");
            }
        }
        if (getChildren().size() == 1) {
            getChildren().add(1, selectedSideTab.getContent());
        } else {
            getChildren().set(1, selectedSideTab.getContent());
        }
        HBox.setHgrow(selectedSideTab.getContent(), Priority.ALWAYS);
    }

    public void setExtended(Boolean extended) {
        this.extended = extended;
        for (SideTab sideTab : tabs) {
            sideTab.setExtended(extended);
        }
    }

    public Boolean getExtended() {
        return extended;
    }

    public void select(int index) {
        selectedSideTab = tabs[index];
        updateSelected();
    }

}
