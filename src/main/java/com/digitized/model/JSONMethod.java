package com.digitized.model;

import org.json.JSONArray;

interface JSONMethods {

    JSONArray retrieveJSONData(String fileName);

    String getJSONFilePath(String fileName);
}