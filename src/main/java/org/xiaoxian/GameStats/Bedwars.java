package org.xiaoxian.GameStats;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.xiaoxian.AriaBot.AriaHost;
import static org.xiaoxian.AriaBot.AriaKey;
import static org.xiaoxian.GameStats.PlayerStats.getRanks;

public class Bedwars {

    public static String getBedwars(String player) {
        try {
            String apiUrl = "http://" + AriaHost + "/bedwars.php?key=" + AriaKey + "&player=" + player;
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
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
                String id = playerObj.getString("id");
                String firstplay = playerObj.getString("firstplay");
                String lastplay = playerObj.getString("lastplay");
                int level = playerObj.getInt("level");
                int xp = playerObj.getInt("xp");
                int next_cost = playerObj.getInt("next_cost");
                int wins = statsObj.getInt("wins");
                int looses = statsObj.getInt("looses");
                double wl = (double) wins / looses;
                int kills = statsObj.getInt("kills");
                int deaths = statsObj.getInt("deaths");
                double kd = (double) kills / deaths;
                int final_kills = statsObj.getInt("final_kills");
                int final_deaths = statsObj.getInt("final_deaths");
                double fkdr = (double) final_kills / final_deaths;
                int beds_destroyed = statsObj.getInt("beds_destroyed");

                return String.format(
                        "%s %s\n" +
                                "| 等级: %d\n" +
                                "| 经验/升级所需: %d/%d\n" +
                                "| 胜/败: %d / %d\n" +
                                "| W/L: %.3f\n" +
                                "| 击杀/死亡: %d / %d\n" +
                                "| K/D: %.3f\n" +
                                "| 终杀/终死: %d / %d\n" +
                                "| FKDR: %.3f\n" +
                                "| 拆床: %d\n" +
                                "首次游玩: %s\n" +
                                "最后游玩: %s",
                        rank, id, level, xp, next_cost, wins, looses, wl, kills, deaths, kd,
                        final_kills, final_deaths, fkdr, beds_destroyed, firstplay, lastplay
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
