package com.canja.kutowerdefence.controller;

import com.canja.kutowerdefence.Routing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.scene.control.*;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class OptionController {
    public void toggleButton(Button clickedButton) {
        String buttonId = clickedButton.getId();
        int value;

        switch (buttonId) {
            case "waveNumberButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 1) % 11;

                if (value == 0) value++;

                clickedButton.setText("" + value);
                break;
            case "waveDelayButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 30) % 120;

                if (value == 0) value += 30;
                
                clickedButton.setText("" + value);
                break;
            case "waveGroupDelayButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 5) % 20;

                if (value == 0) value += 5;

                clickedButton.setText("" + value);
                break;
            case "enemySpawnDelayButton":
                value = Integer.parseInt(clickedButton.getText());
                
                if (value == 1) value++;
                else if (value == 2) value = 5;
                else value = 1;

                clickedButton.setText("" + value);
                break;
            case "goldAmountButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value * 2) % 2000;

                if (value == 0) value += 250;

                clickedButton.setText("" + value);
                break;
            case "goblinRewardButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value * 2) % 80;

                if (value == 0) value += 10;

                clickedButton.setText("" + value);
                break;
            case "knightRewardButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value * 2) % 200;

                if (value == 0) value += 25;

                clickedButton.setText("" + value);
                break;
            case "archerTowerCostButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value * 2) % 1000;

                if (value == 0) value += 125;

                clickedButton.setText("" + value);
                break;
            case "artilleryTowerCostButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 500) % 2000;

                if (value == 0) value += 500;

                clickedButton.setText("" + value);
                break;
            case "mageTowerCostButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 250) % 1000;

                if (value == 0) value += 250;

                clickedButton.setText("" + value);
                break;
            case "playerHitpointButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 1) % 6;

                if (value == 0) value = 3;

                clickedButton.setText("" + value);
                break;
            case "goblinHitpointButton":
            case "knightHitpointButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 100) % 600;

                if (value == 0) value = 100;

                clickedButton.setText("" + value);
                break;
            case "arrowDamageButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 10) % 40;

                if (value == 0) value = 10;

                clickedButton.setText("" + value);
                break;
            case "artilleryDamageButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 25) % 55;

                if (value == 0) value = 25;

                clickedButton.setText("" + value);
                break;
            case "spellDamageButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 5) % 20;

                if (value == 0) value = 5;

                clickedButton.setText("" + value);
                break;
            case "towerRangeButton":
            case "AOERangeButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 1) % 6;

                if (value == 0) value = 2;

                clickedButton.setText("" + value);
                break;
            case "goblinSpeedButton":
            case "knightSpeedButton":
                value = Integer.parseInt(clickedButton.getText());
                value = (value + 1) % 11;

                if (value == 0) value = 1;

                clickedButton.setText("" + value);
                break;
            default:
                break;
        }
    }

    public void restoreDefaultSettings(List<Button> buttons) {
        for (Button button : buttons) {
            int defaultValue = (int) button.getUserData();
            button.setText("" + defaultValue);
        }
    }

    public void exitButtonOnClick() {
        try {
            Routing.returnToPreviousScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveButtonOnClick(List<Button> buttons) throws IOException{
        File saveDirectory = new File("src/main/resources/options");
        String saveName = "options.kutdopt";
        int[] optionValues = new int[buttons.size()];
        int index = 0;

        for (Button button : buttons) {
            int value = Integer.parseInt(button.getText());
            optionValues[index++] = value;
        }

        if (!saveDirectory.exists()) saveDirectory.mkdirs();

        File saveFile = new File(saveDirectory, saveName);
        Gson gson = new GsonBuilder().create();

        try (FileWriter writer = new FileWriter(saveFile)) {
            gson.toJson(optionValues, writer);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Options are saved successfully");
            alert.showAndWait();
        }
    }
}
