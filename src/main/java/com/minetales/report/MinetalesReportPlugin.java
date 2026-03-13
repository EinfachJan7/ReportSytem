package com.minetales.report;

import com.minetales.report.commands.ReportCommand;
import com.minetales.report.commands.ReloadCommand;
import com.minetales.report.commands.ReportCommandTabCompleter;
import com.minetales.report.commands.ReportsCommand;
import com.minetales.report.commands.ReportsCommandTabCompleter;
import com.minetales.report.commands.MessageCommand;
import com.minetales.report.gui.ReportReasonGUI;
import com.minetales.report.listeners.PlayerJoinListener;
import com.minetales.report.listeners.PlayerQuitListener;
import com.minetales.report.listeners.ReportPromptListener;
import com.minetales.report.managers.ReportManager;
import com.minetales.report.managers.DatabaseManager;
import com.minetales.report.managers.DiscordManager;
import com.minetales.report.utils.ConfigManager;
import com.minetales.report.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.TimeZone;

public class MinetalesReportPlugin extends JavaPlugin {
    
    private static MinetalesReportPlugin instance;
    private ReportManager reportManager;
    private DatabaseManager databaseManager;
    private DiscordManager discordManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private ReportReasonGUI reportReasonGUI;
    private ReportPromptListener reportPromptListener;
    private FloodgateApi floodgateApi;

    @Override
    public void onEnable() {
        instance = this;
        
        // Setze Timezone auf Berlin
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
        
        long startTime = System.currentTimeMillis();
        
        // Erstelle Config
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        saveDefaultConfig();
        
        // Initialize Manager
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        databaseManager = new DatabaseManager(this);
        discordManager = new DiscordManager(this);
        reportReasonGUI = new ReportReasonGUI(this, messageManager);
        reportPromptListener = new ReportPromptListener(this, messageManager);
        
        // Starte Datenbank
        if (!databaseManager.initialize()) {
            getLogger().severe("Konnte Datenbank nicht initialisieren!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialisiere ReportManager NACH Datenbank
        reportManager = new ReportManager(this);
        
        // Lade Floodgate API falls verfügbar
        try {
            floodgateApi = FloodgateApi.getInstance();
            getLogger().info("✓ FloodgateAPI geladen!");
        } catch (Exception e) {
            getLogger().warning("✗ FloodgateAPI nicht verfügbar (nur Java Edition)");
            floodgateApi = null;
        }
        
        // Registriere Commands
        registerCommands();
        
        // Registriere Event Listener
        registerListeners();
        
        long duration = System.currentTimeMillis() - startTime;
        getLogger().info("═══════════════════════════════════════════");
        getLogger().info("✓ AbyssBay Report System aktiviert!");
        getLogger().info("✓ Server: " + configManager.getServerName());
        getLogger().info("✓ Discord: " + (discordManager.isWebhookValid() ? "✓ Verbunden" : "✗ Fehler"));
        getLogger().info("✓ Bedrock-Support: " + (floodgateApi != null ? "✓ Aktiv" : "✗ Inaktiv"));
        getLogger().info("✓ Startup Zeit: " + duration + "ms");
        getLogger().info("═══════════════════════════════════════════");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
        getLogger().info("AbyssBay Report System deaktiviert");
    }

    private void registerCommands() {
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("report").setTabCompleter(new ReportCommandTabCompleter(this));
        getCommand("reports").setExecutor(new ReportsCommand(this));
        getCommand("reports").setTabCompleter(new ReportsCommandTabCompleter(this));
        getCommand("reportreload").setExecutor(new ReloadCommand(this, messageManager));
        getCommand("reportmessage").setExecutor(new MessageCommand(this));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
    }

    public static MinetalesReportPlugin getInstance() {
        return instance;
    }

    public ReportManager getReportManager() {
        return reportManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ReportReasonGUI getReportReasonGUI() {
        return reportReasonGUI;
    }

    public ReportPromptListener getReportPromptListener() {
        return reportPromptListener;
    }

    public FloodgateApi getFloodgateApi() {
        return floodgateApi;
    }

    public boolean isBedrock(String playerName) {
        if (floodgateApi == null) return false;
        return playerName.startsWith(configManager.getBedrockPrefix());
    }
}
