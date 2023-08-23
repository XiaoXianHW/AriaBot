package org.xiaoxian.GameStats;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.xiaoxian.AriaBot.AriaHost;
import static org.xiaoxian.AriaBot.AriaKey;
import static org.xiaoxian.GameStats.PlayerStats.getRanks;

public class KnockbackFFA {
    public static String getKnockbackFFA(String player) {
        try {
            String apiUrl = "http://" + AriaHost + "/kbffa.php?key=" + AriaKey + "&player=" + player;
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
                JSONObject playerObj = jsonResponse.getJSONObject("player");
                JSONObject statsObj = jsonResponse.getJSONObject("stats");

                // 获取玩家统计数据
                String rankP = getRanks(player);
                String rank = playerObj.getString("rank");
                int points = playerObj.getInt("points");
                int kills = statsObj.getInt("kills");
                int deaths = statsObj.getInt("deaths");

                return String.format(
                        "%s %s\n" +
                                "| 段位: %s\n" +
                                "| 积分: %d\n" +
                                "| 击杀数: %d\n" +
                                "| 死亡数: %d",
                        rankP, player, rank, points, kills, deaths
                );
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
