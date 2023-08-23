package org.xiaoxian.TimeMute;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static org.xiaoxian.TimeMute.MuteConfig.getGroupConfigs;
import static org.xiaoxian.core.BotInfo.onGetOneQQNumber;

public class MuteManager {

    private final Bot bot = Bot.getInstance(onGetOneQQNumber());
    private final Map<Long, String> lastActionMinute = new HashMap<>();
    private static Timer timer;

    public MuteManager(String dataPath) {
        new MuteConfig(dataPath + "/AllMuteConfig.json");
        setupTimer();
    }

    private void setupTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String currentMinute = LocalTime.now().toString().substring(0, 5);
                for (Map.Entry<String, Map<String, String>> entry : getGroupConfigs().entrySet()) {
                    Long groupId = Long.parseLong(entry.getKey());
                    Map<String, String> config = entry.getValue();

                    String lastMinute = lastActionMinute.getOrDefault(groupId, "");
                    if (lastMinute.equals(currentMinute)) {
                        continue;
                    }

                    String muteTime = config.get("muteTimes");
                    if (muteTime != null && muteTime.equals(currentMinute)) {
                        Group group = bot.getGroup(groupId);
                        if (group != null) {
                            group.sendMessage("[TimeMute] 已开启全体禁言");
                            group.getSettings().setMuteAll(true);
                            lastActionMinute.put(groupId, currentMinute);
                        }
                    }

                    String unmuteTime = config.get("unmuteTimes");
                    if (unmuteTime != null && unmuteTime.equals(currentMinute)) {
                        Group group = bot.getGroup(groupId);
                        if (group != null) {
                            group.getSettings().setMuteAll(false);
                            group.sendMessage("[TimeMute] 已解除全体禁言！");
                            lastActionMinute.put(groupId, currentMinute);
                        }
                    }
                }
            }
        }, 0, 1000);
    }

    public static void stopMuteTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static void setMuteTime(Long groupId, LocalTime time) {
        MuteConfig.setMuteTime(groupId, time.toString());
    }

    public static void setUnmuteTime(Long groupId, LocalTime time) {
        MuteConfig.setUnmuteTime(groupId, time.toString());
    }
}
