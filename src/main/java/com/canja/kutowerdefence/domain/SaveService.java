package com.canja.kutowerdefence.domain;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SaveService {
    private static final SaveService INSTANCE = new SaveService();

    public static SaveService getInstance() {
        return INSTANCE;
    }

    public static List<File> getSaveFiles(){
        File saveDir= new File("src/main/resources/saves");
        if(!saveDir.exists() || !saveDir.isDirectory()){
            return Collections.emptyList();
        }
        File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".kutdsave"));

        return files == null ? Collections.emptyList() : Arrays.asList(files);
    }
}
