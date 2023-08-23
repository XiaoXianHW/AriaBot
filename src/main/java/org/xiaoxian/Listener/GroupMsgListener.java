package org.xiaoxian.Listener;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.xiaoxian.TimeMute.MuteManager;

import java.time.LocalTime;
import java.util.function.Consumer;

import static org.xiaoxian.AriaBot.*;
import static org.xiaoxian.GameStats.Bedwars.getBedwars;
import static org.xiaoxian.GameStats.KnockbackFFA.getKnockbackFFA;
import static org.xiaoxian.GameStats.MLGRush.getMLGRush;
import static org.xiaoxian.GameStats.PlayerStats.getStats;
import static org.xiaoxian.core.BotInfo.onGetBotInfo;

public class GroupMsgListener implements Consumer<GroupMessageEvent> {
    @Override
    public void accept(GroupMessageEvent event) {
        // 提取消息内容
        BackMsgNumber++;
        MessageChain messageChain = event.getMessage();
        String content = messageChain.contentToString();

        // Aira管理群
        if (event.getGroup().getId() == 905698529 || event.getGroup().getId() == 198921528) {
            if (content.startsWith("/acinfo")) {
                event.getGroup().sendMessage(new MessageChainBuilder().append(new QuoteReply(event.getMessage())).append(onGetBotInfo()).build());
                SendMsgNumber++;
            }
        }

        // 开关全体禁言
        if (content.startsWith("/au start")) {
            new MuteManager(dataPath);
            event.getGroup().sendMessage(new MessageChainBuilder().append("[MuteManager] 已初始化并开启计时器 \n- 使用/au mute [HH:mm]设置全体禁言时间\n- 使用/au unmute [HH:mm]设置解除全体禁言时间").build());
            SendMsgNumber++;
        }
        if (content.equals("/au stop")) {
            MuteManager.stopMuteTimer();
            event.getGroup().sendMessage(new MessageChainBuilder().append("[MuteManager] 已停止计时器并关闭定时禁言").build());
            SendMsgNumber++;
        }

        // 定时全体禁言
        if (content.startsWith("/au mute ")) {
            String time = content.substring(9);
            if (time.length() == 4) time = "0" + time;
            MuteManager.setMuteTime(event.getGroup().getId(), LocalTime.parse(time));
            event.getGroup().sendMessage(new MessageChainBuilder().append("[MuteManager] 已设置全体禁言时间为: ").append(time).build());
            SendMsgNumber++;
        }

        if (content.startsWith("/au unmute ")) {
            String time = content.substring(11);
            if (time.length() == 4) time = "0" + time;
            MuteManager.setUnmuteTime(event.getGroup().getId(), LocalTime.parse(time));
            event.getGroup().sendMessage(new MessageChainBuilder().append("[MuteManager] 已设置解除全体禁言时间为: ").append(time).build());
            SendMsgNumber++;
        }

        // 玩家信息查询群
        if (event.getGroup().getId() == 451558725 || event.getGroup().getId() == 198921528) {
            if (content.startsWith("/ac ")) {
                MessageChain chain = new MessageChainBuilder()
                        .append(new QuoteReply(event.getMessage())).append("====Aria 玩家信息====\n").append(getStats(content.substring(4)))
                        .build();
                event.getGroup().sendMessage(chain);
                SendMsgNumber++;
            }

            if (content.startsWith("/bw ")) {
                MessageChain chain = new MessageChainBuilder()
                        .append(new QuoteReply(event.getMessage())).append("====Aria 起床战争====\n").append(getBedwars(content.substring(4)))
                        .build();
                event.getGroup().sendMessage(chain);
                SendMsgNumber++;
            }

            if (content.startsWith("/kbffa ")) {
                MessageChain chain = new MessageChainBuilder()
                        .append(new QuoteReply(event.getMessage())).append("====Aria 击退战场====\n").append(getKnockbackFFA(content.substring(7)))
                        .build();
                event.getGroup().sendMessage(chain);
                SendMsgNumber++;
            }

            if (content.startsWith("/rush ")) {
                MessageChain chain = new MessageChainBuilder()
                        .append(new QuoteReply(event.getMessage())).append("====Aria MLGRush====\n").append(getMLGRush(content.substring(6)))
                        .build();
                event.getGroup().sendMessage(chain);
                SendMsgNumber++;
            }
        }
    }
}
