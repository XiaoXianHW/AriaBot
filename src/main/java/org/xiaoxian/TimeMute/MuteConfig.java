package org.xiaoxian.TimeMute;

import org.json.JSONObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MuteConfig {
    private static final Map<String, Map<String, String>> groupConfigs = new HashMap<>();
    private static String filePath2;

    public MuteConfig(String filePath) {
        filePath2 = filePath;
        File configFile = new File(filePath);
        if (!configFile.exists()) {
            createDefaultConfig();
        }
        try (FileReader reader = new FileReader(configFile)) {
            JSONObject json = new JSONObject(reader);
            for (String key : json.keySet()) {
                JSONObject groupConfigJson = json.getJSONObject(key);
                Map<String, String> groupConfig = new HashMap<>();
                for (String configKey : groupConfigJson.keySet()) {
                    groupConfig.put(configKey, groupConfigJson.getString(configKey));
                }
                groupConfigs.put(key, groupConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDefaultConfig() {
        JSONObject defaultConfig = new JSONObject();
        try (FileWriter writer = new FileWriter(filePath2)) {
            writer.write(defaultConfig.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Map<String, String>> getGroupConfigs() {
        return groupConfigs;
    }

    public static void setMuteTime(Long groupId, String time) {
        Map<String, String> groupConfig = groupConfigs.getOrDefault(String.valueOf(groupId), new HashMap<>());
        groupConfig.put("muteTimes", time);
        groupConfigs.put(String.valueOf(groupId), groupConfig);
        saveConfig();
    }

    public static void setUnmuteTime(Long groupId, String time) {
        Map<String, String> groupConfig = groupConfigs.getOrDefault(String.valueOf(groupId), new HashMap<>());
        groupConfig.put("unmuteTimes", time);
        groupConfigs.put(String.valueOf(groupId), groupConfig);
        saveConfig();
    }

    private static void saveConfig() {
        JSONObject json = new JSONObject(groupConfigs);
        try (FileWriter writer = new FileWriter(filePath2)) {
            writer.write(json.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}