package com.canja.kutowerdefence.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class LevelManager {
    public static final int maxLevel = 1;

    public static int getCurrentLevel() {
        try {
            String path = "src/main/resources/campaign/level.kutd";
            File file = new File(path);

            String content = new String(Files.readAllBytes(file.toPath()));
            Gson gson = new Gson();
            int level = gson.fromJson(content, int.class);

            return level;

        } catch (IOException e) {
            System.err.println("Failed to get level info: " + e.getMessage());

            return 99999;
        }
    }

    public static void saveLevel(int level) {
        String path = "src/main/resources/campaign/level.kutd";

        Gson gson = new GsonBuilder().create();

        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(level, writer);
            System.out.println("Level info saved to " + path);
        } catch (IOException e) {
            System.err.println("Failed to save info: " + e.getMessage());
        }
    }

    public static List<Object> extractLevelInfo(int level) throws IOException{
        String mapPath = "src/main/resources/campaign/levels/level_" + level + "/map.kutdmap";
        String optionsPath = "src/main/resources/campaign/levels/level_" + level + "/options.kutdopt";
        String wavePath = "src/main/resources/campaign/levels/level_" + level + "/wave.kutdwave";

        File mapFile = new File(mapPath);
        File optionFile = new File(optionsPath);
        File waveFile = new File(wavePath);

        Gson gson = new Gson();

        String content = new String(Files.readAllBytes(optionFile.toPath()));
        int[] options = gson.fromJson(content, int[].class);
        
        content = new String(Files.readAllBytes(waveFile.toPath()));
        Type listOfIntArrayType = new TypeToken<List<int[]>>() {}.getType();
        List<int[]> waves = gson.fromJson(content, listOfIntArrayType);

        List<Object> list = new ArrayList<>();

        list.add(mapFile);
        list.add(options);
        list.add(waves);

        return list;
    }
}
