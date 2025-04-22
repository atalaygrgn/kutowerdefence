package com.canja.kutowerdefence;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 Routing API for easy stage navigation! <br>
 How to use: In the UI elements' event method you want to use e.g. exitButtonOnClick(), <br>
 <code>
 try {   // Internal routing methods of JavaFX throws IOException, so try/catch is mandatory
 Routing.openMainMenu(exitButton); // call the static method and pass the UI element as argument
 } catch (IOException e) {
 ... // whatever you want to do if things go wrong
 }
 </code> <br>
 Note: In fact, as long as the UI element can access its Stage element by element.getScene().getWindow(), you can pass it
 as the argument. It does not have to be a button.
 */
public final class Routing {

    /**
     * Starts the initial routing with the Main Menu view. Only called once, in Main.start .
     * @param stage The stage to start with.
     * @throws IOException
     */
    public static void startMainMenu(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainmenu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 768);
        stage.setTitle("KU Tower Defence");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Opens the Main Menu view in the same window.
     * @param node The UI element calling this method.
     * @throws IOException
     */
    public static void openMainMenu(Parent node) throws IOException {
        open(node, "mainmenu-view.fxml", "KU Tower Defence");
    }

    /**
     * Opens the Edit Mode (Map Editor) view in the same window.
     * @param node The UI element calling this method.
     * @throws IOException
     */
    public static void openEditMode(Parent node) throws IOException {
        open(node, "editmode-view.fxml", "Edit Mode");
    }

    /**
     * Opens the Options Menu view in the same window.
     * @param node The UI element calling this method.
     * @throws IOException
     */
    public static void openOptionsMenu(Parent node) throws IOException {
        open(node, "optionsmenu-view.fxml", "Options");
    }

    /**
     * The internal method for routing. Do not touch!
     * @param node The UI element calling this method.
     * @param layoutPath Path to the FXML layout file.
     * @param title Title of the new view.
     * @throws IOException
     */
    private static void open(Parent node, String layoutPath, String title) throws IOException {
        Stage currentStage = (Stage) node.getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(Routing.class.getResource(layoutPath));
        Parent editorRoot = fxmlLoader.load();

        Scene editorScene = new Scene(editorRoot, 1280, 768);
        currentStage.setScene(editorScene);
        currentStage.setTitle(title);
        currentStage.setResizable(false);
    }
}
