package com.minetales.report.utils;

import com.minetales.report.MinetalesReportPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUtils {
    
    private final MinetalesReportPlugin plugin;

    public PlayerUtils(MinetalesReportPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isBedrock(String playerName) {
        return playerName.startsWith(".");
    }

    public boolean isJava(String playerName) {
        return !isBedrock(playerName);
    }

    public String getPlayerType(String playerName) {
        return isBedrock(playerName) ? "Bedrock Edition" : "Java Edition";
    }

    public String getPlayerStatus(String playerName) {
        Player onlinePlayer = Bukkit.getPlayer(playerName);
        
        if (isBedrock(playerName)) {
            return onlinePlayer != null ? "🟢 Online (Bedrock)" : "📱 Offline (Bedrock)";
        } else {
            return onlinePlayer != null ? "🟢 Online (Java)" : "🔴 Offline (Java)";
        }
    }

    public boolean isPlayerOnline(String playerName) {
        return Bukkit.getPlayer(playerName) != null;
    }
}
