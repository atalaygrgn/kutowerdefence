package com.canja.kutowerdefence;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.IOException;

public final class Routing {

    public static void openMainMenu(Control control) throws IOException {
        open(control, "mainmenu-view.fxml", "KU Tower Defence");
    }

    public static void openEditMode(Control control) throws IOException {
        open(control, "mapeditor-view.fxml", "Edit Mode");
    }

    private static void open(Control control, String layoutPath, String title) throws IOException {
        Stage currentStage = (Stage) control.getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(Routing.class.getResource(layoutPath));
        Parent editorRoot = fxmlLoader.load();

        Scene editorScene = new Scene(editorRoot, 1280, 768);
        currentStage.setScene(editorScene);
        currentStage.setTitle(title);
        currentStage.setResizable(false);
    }
}
