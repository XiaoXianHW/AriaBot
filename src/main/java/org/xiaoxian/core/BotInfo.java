package org.xiaoxian.core;

import net.mamoe.mirai.Bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.xiaoxian.AriaBot.*;

public class BotInfo {

    static String systemName;
    static String cpuModel;
    static String ramSize;
    static String ramUseSize;
    static String ramFreeSize;
    static String cpuUse;
    static String ramUse;

    // 计算运行时间
    public static String setTime() {
        long milliseconds = System.currentTimeMillis() - startTime;
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        long sec = seconds % 60;
        long min = minutes % 60;
        long hr = hours % 24;

        return days + "天 " + hr + "时 " + min + "分 " + sec + "秒";
    }

    // 获取好友数量（默认获取登录的第一个QQ）
    public static int onGetFriendsListNumber() {
        int i = 0;
        try {
            List<Long> bots = onGetBotsList();
            if (bots.size() > 0) {
                i = Bot.getInstance(bots.get(0)).getFriends().getSize();
                return i;
            }
        } catch (NoSuchElementException ignored) {
        }
        return i;
    }

    // 获取群聊数量（默认获取登录的第一个QQ）
    public static int onGetGroupListNumber() {
        int i = 0;
        try {
            List<Long> bots = onGetBotsList();
            if (bots.size() > 0) {
                i = Bot.getInstance(bots.get(0)).getGroups().getSize();
            }
        } catch (NoSuchElementException ignored) {
        }
        return i;
    }

    // 获取所有登录的QQ
    public static List<Long> onGetBotsList() {
        List<Long> botIds = new ArrayList<>();
        for (Bot bot : Bot.getInstances()) {
            botIds.add(bot.getBot().getId());
        }
        return botIds;
    }

    // 获取登录的第一个QQ
    public static long onGetOneQQNumber() {
        long i = 0;
        try {
            List<Long> bots = onGetBotsList();
            if (bots.size() > 0) {
                i = bots.get(0);
            }
        } catch (NoSuchElementException ignored) {
        }
        return i;
    }

    // 获取Windows系统信息
    public static void onGetWinSystemInfo() {
        try {
            Process process;
            BufferedReader br;

            // CPU 型号
            process = Runtime.getRuntime().exec("wmic cpu get name");
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            br.readLine();
            cpuModel = br.readLine().trim();

            // 系统名
            process = Runtime.getRuntime().exec("wmic OS get Caption");
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            br.readLine();
            systemName = br.readLine().trim();

            // 总内存
            process = Runtime.getRuntime().exec("wmic os get TotalVisibleMemorySize");
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            br.readLine();
            ramSize = String.format("%.2f", Integer.parseInt(br.readLine().trim()) / 1024f / 1024f);

            // 空闲内存
            process = Runtime.getRuntime().exec("wmic os get FreePhysicalMemory");
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            br.readLine();
            ramFreeSize = String.format("%.2f", Integer.parseInt(br.readLine().trim()) / 1024f / 1024f);

            // 使用内存
            ramUseSize = String.format("%.2f", Double.parseDouble(ramSize) - Double.parseDouble(ramFreeSize));

            // 使用CPU
            process = Runtime.getRuntime().exec("typeperf \"\\Processor Information(_Total)\\% Processor Utility\" -sc 1");
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            br.readLine();
            String[] array = br.readLine().split(",");
            cpuUse = array[1].replace("\"", "").trim();
            Pattern pattern = Pattern.compile("\\d+\\.\\d+");
            Matcher matcher = pattern.matcher(cpuUse);
            if (matcher.find()) {
                cpuUse = String.format("%.2f", Double.parseDouble(matcher.group()));
            }

            // 内存使用率
            ramUse = String.format("%.2f", Double.parseDouble(ramUseSize) / Double.parseDouble(ramSize) * 100);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取Linux系统信息
    public static void onGetLinuxSystemInfo() {
        try {
            // 获取系统名称和版本
            ProcessBuilder osProcessBuilder = new ProcessBuilder("bash", "-c", "cat /etc/os-release | grep PRETTY_NAME");
            Process osProcess = osProcessBuilder.start();
            BufferedReader osbr = new BufferedReader(new InputStreamReader(osProcess.getInputStream()));
            systemName = osbr.readLine().split("=")[1].replace("\"", "").trim();
            if (systemName.contains(" ")) {
                String[] parts = systemName.split(" ");
                systemName = parts[0] + " " + parts[1];
            }

            // 获取CPU型号
            ProcessBuilder cpuProcessBuilder = new ProcessBuilder("bash", "-c", "cat /proc/cpuinfo | grep 'model name' | head -1\n");
            Process cpuProcess = cpuProcessBuilder.start();
            BufferedReader cpubr = new BufferedReader(new InputStreamReader(cpuProcess.getInputStream()));
            cpuModel = cpubr.readLine().split(":")[1].trim();

            // 获取RAM信息
            ProcessBuilder ramProcessBuilder = new ProcessBuilder("bash", "-c", "free -m | grep Mem:");
            Process ramProcess = ramProcessBuilder.start();
            BufferedReader rambr = new BufferedReader(new InputStreamReader(ramProcess.getInputStream()));
            String[] ramParts = rambr.readLine().split("\\s+");
            ramSize = String.format("%.2f", Integer.parseInt(ramParts[1]) / 1024.0);
            ramUseSize = String.format("%.2f", Integer.parseInt(ramParts[2]) / 1024.0);
            rambr.close();
            ramProcess.destroy();

            // 获取CPU使用率
            ProcessBuilder cpuUseProcessBuilder = new ProcessBuilder("bash", "-c", "vmstat | tail -1 | awk '{print $15}'");
            Process cpuUseProcess = cpuUseProcessBuilder.start();
            BufferedReader cpuUsebr = new BufferedReader(new InputStreamReader(cpuUseProcess.getInputStream()));
            cpuUse = String.valueOf(100 - Double.parseDouble(cpuUsebr.readLine().trim()));

            // 计算RAM使用率
            ramUse = String.format("%.2f", (Double.parseDouble(ramUseSize) / Double.parseDouble(ramSize) * 100));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String onGetBotInfo() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            onGetWinSystemInfo();
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            onGetLinuxSystemInfo();
        }

        return "=====AriaBot=====" +
                "\n系统: " + systemName +
                "\nCPU: " + cpuModel +
                "\nRAM: " + ramUseSize + "/" + ramSize + "GB" +
                "\nCPU使用: " + cpuUse + "%" +
                "\nRAM使用: " + ramUse + "%" +
                "\n\n群聊数: " + onGetGroupListNumber() +
                "\n好友数: " + onGetFriendsListNumber() +
                "\n发送消息数: " + SendMsgNumber +
                "\n接收消息数: " + BackMsgNumber +
                "\n\n正常运行:" +
                "\n" + setTime() +
                "\n======AriaBot v" + atVer + "=====";
    }
}
