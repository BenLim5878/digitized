package com.digitized.model;

import org.json.JSONArray;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONData implements JSONMethods{

    public JSONArray retrieveJSONData(String fileName) {
        JSONArray data = new JSONArray();
        // JSON file path
        String root = System.getProperty("user.dir");
        String FileName = fileName;
        String filePath = root + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + FileName;
        try {
            String contents = new String((Files.readAllBytes(Paths.get(filePath))));
            data = new JSONArray(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public String getJSONFilePath(String fileName){
        String root = System.getProperty("user.dir");
        String filePath = root + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + fileName;
        return  filePath;
    }
}
