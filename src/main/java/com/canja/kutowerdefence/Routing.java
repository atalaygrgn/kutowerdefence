package com.canja.kutowerdefence;

import com.canja.kutowerdefence.controller.GamePlayController;
import com.canja.kutowerdefence.controller.OptionController;
import com.canja.kutowerdefence.controller.WaveController;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.ui.GamePlayView;
import com.canja.kutowerdefence.ui.OptionsMenuView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
     * Opens the game screen with the selected map.
     * @param gameSession The game session to inject into the GamePlayView controller.
     * @throws IOException
     */
    public static void openGamePlay(GameSession gameSession) throws IOException {   
        FXMLLoader fxmlLoader = open("gameplay-view.fxml", "KU Tower Defence");
        GamePlayView view = fxmlLoader.getController();
        GamePlayController controller = new GamePlayController(gameSession);
        WaveController waveController = new WaveController(gameSession);
        controller.setView(view);
        waveController.setView(view);
        view.setController(controller, waveController);

        /* 
        FXMLLoader fxmlLoader = new FXMLLoader(Routing.class.getResource("gameplay-view.fxml"));
        Parent root = fxmlLoader.load();

        GamePlayView view = fxmlLoader.getController();
        GamePlayController controller = new GamePlayController(gameSession);
        controller.setView(view);
        view.setController(controller);

        Scene scene = new Scene(root, 1280, 768);
        route.push(scene);
        titles.push("KU Tower Defence");
        mainStage.setScene(scene);
        mainStage.setTitle("KU Tower Defence");
        mainStage.setResizable(false);
        */
    }

    public static void openGamePlayFromSave(GameSession gameSession, List<int[]> towerInfo) throws IOException{
        FXMLLoader fxmlLoader = open("gameplay-view.fxml", "KU Tower Defence");
        GamePlayView view = fxmlLoader.getController();
        GamePlayController controller = new GamePlayController(gameSession);
        WaveController waveController = new WaveController(gameSession);
        controller.setView(view);
        waveController.setView(view);
        view.setController(controller, waveController);
        view.reloadTowers(towerInfo, gameSession);
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
        FXMLLoader fxmlLoader = open("optionsmenu-view.fxml", "Options");
        OptionsMenuView view = fxmlLoader.getController();
        OptionController controller = new OptionController();
        view.setController(controller);
    }

    /**
     * The internal method for routing. Do not touch!
     * @param layoutPath Path to the FXML layout file.
     * @param title Title of the new view.
     * @throws IOException
     */
    private static FXMLLoader open(String layoutPath, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Routing.class.getResource(layoutPath));
        Parent editorRoot = fxmlLoader.load();

        Scene scene = new Scene(editorRoot, 1280, 768);
        route.push(scene);
        titles.push(title); // Store new title
        mainStage.setScene(scene);
        mainStage.setTitle(title);
        mainStage.setResizable(false);

        return fxmlLoader;
    }
}