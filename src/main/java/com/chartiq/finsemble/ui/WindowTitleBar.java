package com.chartiq.finsemble.ui;

import com.chartiq.finsemble.Finsemble;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowTitleBar extends AnchorPane {
    //region FXML controls
    @FXML
    private Button linkerButton;
    @FXML
    private Button dragButton;
    @FXML
    private Pane group1;
    @FXML
    private Pane group2;
    @FXML
    private Pane group3;
    @FXML
    private Pane group4;
    @FXML
    private Pane group5;
    @FXML
    private Pane group6;
    @FXML
    private Label windowTitle;
    @FXML
    private Button dockingButton;
    @FXML
    private Button maximizeButton;
    //endregion

    // TODO: How to control parent window
    private Stage primaryStage;

    private Finsemble FSBL;

    public WindowTitleBar() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WindowTitleBar.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TODO: Can we hide the default windowtitle bar in this consctructor or does it need to happen in external code?
    }

    @FXML
    public void initialize() {
        dragButton.setVisible(false);
        group1.setVisible(false);
        group2.setVisible(false);
        group3.setVisible(false);
        group4.setVisible(false);
        group5.setVisible(false);
        group6.setVisible(false);
        dockingButton.setVisible(false);
        // TODO: Get initial group state from component state and update which are displayed
    }

    @FXML
    public void showLinker() {
        // Use finsemble API to show linker window
        final Bounds linkerBounds = linkerButton.getLayoutBounds();
        final double x = linkerBounds.getMinX();
        final double y = linkerBounds.getMaxY();

        // TODO: Add linker behavior
    }

    @FXML
    public void minimize() {
        primaryStage.setIconified(true);
    }

    @FXML
    public void maximizeRestore() {
        final boolean isMaximized = primaryStage.isMaximized();
        primaryStage.setMaximized(!isMaximized);

        if (!isMaximized) {
            // Set icon and tooltip to maximize
            maximizeButton.setText("3");
            maximizeButton.setTooltip(new Tooltip("Maximize"));
        } else {
            // Set icon and tooltip to restore
            maximizeButton.setText("#");
            maximizeButton.setTooltip(new Tooltip("Restore"));
        }

    }

    @FXML
    public void close() {
        primaryStage.close();
    }

    @FXML
    public void dock() {
        // dock: >
        // undock: @
        // TODO: Add docking behavior
    }

    public void setWindowTitle(String title) {
        windowTitle.setText(title);
    }

    public void setFinsemble(Finsemble fsbl) {
        this.FSBL = fsbl;
    }
}
