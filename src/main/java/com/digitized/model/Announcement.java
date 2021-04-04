package com.digitized.model;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Announcement extends JSONData {

    private String announcementTitle;
    private final String announcementDate;
    private String announcementContent;

    public Announcement() {
        announcementTitle = "";
        announcementDate = "";
        announcementTitle = "";
    }

    public Announcement(String title, String content) {
        announcementTitle = title;
        announcementContent = content;
        System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(System.currentTimeMillis());
        announcementDate = formatter.format(date);
    }

    // Getter
    public String getDate() {
        return this.announcementDate;
    }

    public String getTitle() {
        return this.announcementTitle;
    }

    public String getContent() {
        return this.announcementContent;
    }

    public VBox generateAnnouncement(String title, String date, String description) {
        VBox announcementBox = new VBox();
        announcementBox.getStyleClass().add("announcementBox");
        Text announcementTitle = new Text(title);
        Text announcementDate = new Text(date);
        Text announcementDescription = new Text(description);
        // add style class
        announcementTitle.getStyleClass().add("announcementTitle");
        announcementDate.getStyleClass().add("announcementDate");
        announcementDescription.getStyleClass().add("announcementContent");
        //
        announcementDescription.setWrappingWidth(400);
        announcementTitle.setWrappingWidth(400);
        announcementBox.getChildren().addAll(announcementTitle, announcementDate, announcementDescription);
        return announcementBox;
    }

    public VBox getAnnouncementData() {
        VBox v = new VBox();
        JSONArray data = retrieveJSONData("announcement.json");
        for (int i = 0; i < data.length(); i++) {
            String announcementTitle = data.getJSONObject(i).get("title").toString();
            String announcementDate = data.getJSONObject(i).get("date").toString();
            String announcementDescription = data.getJSONObject(i).get("description").toString();
            VBox box = generateAnnouncement(announcementTitle, announcementDate, announcementDescription);
            v.getChildren().add(box);
        }
        return v;
    }

    public void postAnnouncementDate() {
        JSONObject obj = new JSONObject();
        obj.put("title", this.announcementTitle);
        obj.put("date", this.announcementDate);
        obj.put("description", this.announcementContent);
        JSONArray data = retrieveJSONData("announcement.json");
        JSONArray newData = new JSONArray();
        for (int i = 0; i < data.length(); i++) {
            newData.put(data.getJSONObject(i));
        }
        newData.put(obj);
        try {
            Files.write(Paths.get(getJSONFilePath("announcement.json")), newData.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
