package com.abyssaby.report.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public class ConfigManager {
    
    private final FileConfiguration config;
    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    // Discord Settings
    public String getWebhookUrl() {
        return config.getString("discord.webhook-url", "");
    }

    public String getAdminRoleId() {
        return config.getString("discord.admin-role-id", "");
    }

    // Server Settings
    public String getServerName() {
        return config.getString("server.name", "AbyssBay Main");
    }

    public boolean isBedrockTrackingEnabled() {
        return config.getBoolean("server.bedrock-tracking", true);
    }

    // Report Settings
    public int getCooldownSeconds() {
        return config.getInt("report.cooldown-seconds", 60);
    }

    public int getMaxReportsPerPlayer() {
        return config.getInt("report.max-reports-per-player", 10);
    }

    public int getAutoDeleteAfterHours() {
        return config.getInt("report.auto-delete-after-hours", 7);
    }

    public List<String> getReportReasons() {
        return config.getStringList("report.reasons");
    }

    // Permissions
    public String getReportUsePermission() {
        return config.getString("permissions.report-use", "report.use");
    }

    public String getReportAdminPermission() {
        return config.getString("permissions.report-admin", "report.admin");
    }

    // Database
    public boolean isDatabaseEnabled() {
        return config.getBoolean("database.enabled", true);
    }

    public String getDatabaseType() {
        return config.getString("database.type", "mysql");
    }

    public String getDatabaseHost() {
        return config.getString("database.host", "localhost");
    }

    public int getDatabasePort() {
        return config.getInt("database.port", 3306);
    }

    public String getDatabaseName() {
        return config.getString("database.name", "report_system");
    }

    public String getDatabaseUser() {
        return config.getString("database.user", "root");
    }

    public String getDatabasePassword() {
        return config.getString("database.password", "password");
    }

    public int getDatabaseMaxPoolSize() {
        return config.getInt("database.max-pool-size", 10);
    }

    public int getDatabaseMinIdle() {
        return config.getInt("database.min-idle", 5);
    }

    public long getDatabaseConnectionTimeout() {
        return config.getLong("database.connection-timeout", 30000);
    }

    public long getDatabaseIdleTimeout() {
        return config.getLong("database.idle-timeout", 600000);
    }

    public int getDatabaseAutoSaveInterval() {
        return config.getInt("database.auto-save-interval", 300);
    }

    // Discord Embed Customization
    public int getEmbedColor() {
        return config.getInt("discord.embed.color", 3092790);
    }

    public String getEmbedTitle() {
        return config.getString("discord.embed.title", "🚨 SPIELER REPORT 🚨");
    }

    public String getEmbedDescription() {
        return config.getString("discord.embed.description", "Ein Spieler wurde vom Report-System gemeldet");
    }

    public String getEmbedAuthorName() {
        return config.getString("discord.embed.author-name", "AbyssBay Report System");
    }

    public String getEmbedAuthorIconUrl() {
        return config.getString("discord.embed.author-icon-url", "");
    }

    public String getEmbedThumbnailUrl() {
        return config.getString("discord.embed.thumbnail-url", "https://minotar.net/avatar/{player}/64");
    }

    public String getEmbedAvatarUrl() {
        return config.getString("discord.embed.avatar-url", "https://minotar.net/avatar/{player}/128");
    }

    public String getEmbedFooter() {
        return config.getString("discord.embed.footer", "AbyssBay Report System");
    }

    public String getEmbedFooterIconUrl() {
        return config.getString("discord.embed.footer-icon-url", "");
    }

    public boolean isEmbedShowTimestamp() {
        return config.getBoolean("discord.embed.show-timestamp", true);
    }

    public String getOldDatabaseFile() {
        return config.getString("database.file", "plugins/AbyssabyReportSystem/database.db");
    }

    // Bedrock
    public String getBedrockPrefix() {
        return config.getString("bedrock.prefix", ".");
    }

    public boolean isDetectByPrefix() {
        return config.getBoolean("bedrock.detect-by-prefix", true);
    }

    public boolean isValidReason(String reason) {
        return getReportReasons().contains(reason);
    }

    public void reload() {
        plugin.reloadConfig();
    }
}
