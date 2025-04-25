package com.canja.kutowerdefence;

import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

/**
 Routing API for easy scene navigation! <br>
 How to use: In the UI elements' event method you want to use e.g. exitButtonOnClick(), <br>
 <code>
 try {   // Internal routing methods of JavaFX throws IOException, so try/catch is mandatory
 Routing.openEditMode(); // call the static method
 } catch (IOException e) {
 ... // whatever you want to do if things go wrong
 }
 </code> <br>
 */
public final class Routing {

    private static final Stack<Scene> route = new Stack<>();
    private static final Stack<String> titles = new Stack<>();
    private static Stage mainStage;

    /**
     * Starts the initial routing with the Main Menu view. Only called once, in Main.start .
     * @param stage The stage to start with.
     * @throws IOException
     */
    public static void startMainMenu(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainmenu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 768);
        String title = "KU Tower Defence";
        mainStage.setTitle(title);

        route.push(scene);
        titles.push(title);

        Image cursorImage = new Image(new File("src/main/resources/assets/ui/cursor.png").toURI().toString());
        scene.setCursor(new ImageCursor(cursorImage));

        mainStage.setScene(scene);
        mainStage.setResizable(false);

        mainStage.show();
    }

    /**
     * Returns to the previous scene. Convenient for exit button events.
     * @throws IOException
     */
    public static void returnToPreviousScene() throws IOException {
        if (route.size() <= 1) {
            return; // Don't pop the last scene
        }

        route.pop();
        titles.pop();

        mainStage.setScene(route.peek());
        mainStage.setTitle(titles.peek());
    }

    /**
     * Opens the Main Menu view in the same window.
     * @throws IOException
     */
    public static void openMainMenu() throws IOException {
        open("mainmenu-view.fxml", "KU Tower Defence");
    }

    /**
     * Opens the Edit Mode (Map Editor) view in the same window.
     * @throws IOException
     */
    public static void openEditMode() throws IOException {
        open("editmode-view.fxml", "Edit Mode");
    }

    /**
     * Opens the Options Menu view in the same window.
     * @throws IOException
     */
    public static void openOptionsMenu() throws IOException {
        open("optionsmenu-view.fxml", "Options");
    }

    /**
     * The internal method for routing. Do not touch!
     * @param layoutPath Path to the FXML layout file.
     * @param title Title of the new view.
     * @throws IOException
     */
    private static void open(String layoutPath, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Routing.class.getResource(layoutPath));
        Parent editorRoot = fxmlLoader.load();

        Scene scene = new Scene(editorRoot, 1280, 768);
        route.push(scene);
        titles.push(title); // Store new title
        mainStage.setScene(scene);
        mainStage.setTitle(title);
        mainStage.setResizable(false);
    }
}
