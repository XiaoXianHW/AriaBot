package org.xiaoxian;

import net.mamoe.mirai.console.MiraiConsole;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.xiaoxian.Listener.GroupMsgListener;

import java.io.File;

public final class AriaBot extends JavaPlugin {
    public static final AriaBot INSTANCE = new AriaBot();
    public static String dataPath;
    public static String atVer = "1.2";
    public static String AriaHost = "127.0.0.1:23333";
    public static String AriaKey = "apikey";
    public static long startTime = 0;
    public static int BackMsgNumber = 0;
    public static int SendMsgNumber = 0;

    private AriaBot() {
        super(new JvmPluginDescriptionBuilder("org.xiaoxian.AriaBot", "1.2")
                .name("AriaBot")
                .author("XiaoXian")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("———————————————————————————");
        getLogger().info("AriaBot v" + atVer + " Loading...");
        getLogger().info("Author: XiaoXian");
        getLogger().info("Email: xiaoxian@axtn.net");
        getLogger().info("———————————————————————————");

        // 获取数据目录
        String dataDir = MiraiConsole.INSTANCE.getPluginManager().getPluginsDataPath().toString();
        File folder = new File(dataDir, "org.xiaoxian.ariabot");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        dataPath = folder.getAbsolutePath();
        getLogger().info("AriaBot数据存储目录: " + dataPath);

        /* 消息监听处理 */
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class,new GroupMsgListener());
        getLogger().info("[事件] 注册群聊消息监听Event（GroupMessageEvent.class）");

        getLogger().info("———————————————————————————");

        startTime = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        CommandManager.INSTANCE.unregisterAllCommands(AriaBot.INSTANCE);
        getLogger().info("AriaBot v" + atVer + " Disable");
        getLogger().info("Thanks for using!");
    }
}