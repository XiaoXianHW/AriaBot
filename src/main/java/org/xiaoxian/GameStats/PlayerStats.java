package org.xiaoxian.GameStats;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.xiaoxian.AriaBot.AriaHost;
import static org.xiaoxian.AriaBot.AriaKey;

public class PlayerStats {
    public static String getStats(String player) {
        try {
            String apiUrl = "http://" + AriaHost + "/player.php?key=" + AriaKey + "&player=" + player;
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                // 获取玩家统计数据
                String uuid = jsonResponse.getJSONObject("player").getString("uuid");
                String group = jsonResponse.getJSONObject("player").getString("group");
                String displayName = jsonResponse.getJSONObject("player").getString("displayname");
                String rank = jsonResponse.getJSONObject("player").getString("rank");
                boolean isBanned = jsonResponse.getJSONObject("player").getBoolean("isBanned");
                boolean isMuted = jsonResponse.getJSONObject("player").getBoolean("isMuted");
                String firstLogin = jsonResponse.getJSONObject("player").getString("firstLogin");
                String lastLogin = jsonResponse.getJSONObject("player").getString("lastLogin");

                return (rank + displayName +
                        "\n| 权限: " + group +
                        "\n| 封禁: " + (isBanned ? "是" : "否") +
                        "\n| 禁言: " + (isMuted ? "是" : "否") +
                        "\n首次登录: " + firstLogin +
                        "\n最后登录: " + lastLogin);
            } else {
                if (responseCode == 422) {
                    return ("查询的玩家不存在");
                }
                return ("API调用出错，返回代码: " + responseCode);
            }
        } catch (Exception e) {
            return ("ERROR：" + e.getMessage() + "\n请通过/反馈 " + e.getMessage() + "反馈此问题");
        }
    }

    public static String getRanks(String player) {
        try {
            String apiUrl = "http://" + AriaHost + "/player.php?key=" + AriaKey + "&player=" + player;
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                String rank = jsonResponse.getJSONObject("player").getString("rank");

                return (rank);
            } else {
                if (responseCode == 422) {
                    return ("查询的玩家不存在");
                }
                return ("API调用出错，返回代码: " + responseCode);
            }
        } catch (Exception e) {
            return ("ERROR：" + e.getMessage());
        }
    }
}
