package com.canja.kutowerdefence.domain;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapService {
private static final MapService INSTANCE = new MapService();

private MapService() {}

public static MapService getInstance() {
    return INSTANCE;
}

public List<File> getSavedMaps(){
    File mapsDir= new File("src/main/resources/maps");
    if(!mapsDir.exists() || !mapsDir.isDirectory()){
        return Collections.emptyList();
    }
    File[] files = mapsDir.listFiles((dir, name) -> name.endsWith(".kutdmap"));

    return files == null ? Collections.emptyList() : Arrays.asList(files);
}

}
