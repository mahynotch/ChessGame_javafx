package com.sustech.chess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SaveLoad {
    public static boolean writeJsonFile(JSONObject out, String path) throws IOException {
        String s = out.toString();
        FileOutputStream fos = new FileOutputStream(path);
        OutputStreamWriter os = new OutputStreamWriter(fos);
        BufferedWriter w = new BufferedWriter(os);
        w.write(s);
        w.close();
        return true;
    }

    public static JSONArray readJsonArrayFile(String place) throws JSONException {
        String str = "";
        try {
            InputStream path = new Object() {
                public InputStream getPath() {
                    return this.getClass().getResourceAsStream("/save/save.json");
                }
            }.getPath();
//            FileInputStream inputStream = new FileInputStream(new File(new URI(path)));
            InputStreamReader inputStreamReader = new InputStreamReader(path, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                str += tempString;
            }
            JSONArray demo = new JSONArray(str);
            return demo;
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public static JSONObject readJsonObjectFile(String place) throws JSONException {
        StringBuilder SB = new StringBuilder();
        try {
            String Path = place;
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                SB.append(tempString);
            }
            JSONObject demo = new JSONObject(SB.toString());
            return demo;
        } catch (Exception e) {
            return null;
        }
    }
}
