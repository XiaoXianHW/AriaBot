package org.xiaoxian.GameStats;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.xiaoxian.AriaBot.AriaHost;
import static org.xiaoxian.AriaBot.AriaKey;
import static org.xiaoxian.GameStats.PlayerStats.getRanks;

public class MLGRush {
    public static String getMLGRush(String player) {
        try {
            String apiUrl = "http://" + AriaHost + "/rush.php?key=" + AriaKey + "&player=" + player;
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
                String rank = getRanks(player);
                int level = playerObj.getInt("level");
                String ranked = playerObj.getString("ranked");
                int wins = statsObj.getInt("wins");
                int bed_breaks = statsObj.getInt("bed_breaks");
                int games_played = statsObj.getInt("games_played");

                return String.format(
                        "%s %s\n" +
                                "| 等级: %d\n" +
                                "| 段位: %s\n" +
                                "| 胜利数: %d\n" +
                                "| 拆床数: %d\n" +
                                "| 游玩数: %d",
                        rank, player, level, ranked, wins, bed_breaks, games_played
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
