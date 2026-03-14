package com.abyssaby.report.utils;

import com.abyssaby.report.AbyssabyReportPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * MessageManager - Laden und Formatieren von Nachrichten mit MiniMessage
 */
public class MessageManager {
    
    private final AbyssabyReportPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private YamlConfiguration messages;
    private File messagesFile;

    public MessageManager(AbyssabyReportPlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        
        // Erstelle messages.yml falls nicht vorhanden
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reload() {
        loadMessages();
        plugin.getLogger().info("✓ Messages.yml neu geladen");
    }

    /**
     * Ersetzt den {PREFIX} Platzhalter mit dem konfigurierten Prefix
     */
    private String replacePrefix(String message) {
        String prefix = messages.getString("prefix", "");
        return message.replace("{PREFIX}", prefix);
    }

    /**
     * Gibt eine formatierte Nachricht als Component zurück
     */
    public Component get(String path) {
        String message = messages.getString(path, "");
        message = replacePrefix(message);
        return miniMessage.deserialize(message);
    }

    /**
     * Gibt eine formatierte Nachricht mit Platzhaltern zurück
     */
    public Component get(String path, Map<String, String> placeholders) {
        String message = messages.getString(path, "");
        
        // Ersetze Platzhalter
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        message = replacePrefix(message);
        return miniMessage.deserialize(message);
    }

    /**
     * Convenience-Methode mit varargs für Platzhalter
     */
    public Component get(String path, String... replacements) {
        String message = messages.getString(path, "");
        
        // Ersetze Platzhalter (pairs: key, value, key, value, ...)
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        
        message = replacePrefix(message);
        return miniMessage.deserialize(message);
    }

    /**
     * Sende eine Nachricht an einen Spieler
     */
    public void send(Player player, String path) {
        player.sendMessage(get(path));
    }

    public void send(Player player, String path, Map<String, String> placeholders) {
        player.sendMessage(get(path, placeholders));
    }

    public void send(Player player, String path, String... replacements) {
        player.sendMessage(get(path, replacements));
    }

    /**
     * Logge eine Nachricht
     */
    public void log(String path) {
        String message = messages.getString(path, "");
        plugin.getLogger().info(message);
    }

    /**
     * Gebe den Prefix zurück
     */
    public Component getPrefix() {
        return get("prefix");
    }

    /**
     * Formatiere eine Nachricht mit Prefix
     */
    public Component withPrefix(String path, Map<String, String> placeholders) {
        String prefix = messages.getString("prefix", "");
        String message = messages.getString(path, "");
        
        // Ersetze Platzhalter
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        return miniMessage.deserialize(prefix + " " + message);
    }

    public void sendWithPrefix(Player player, String path, Map<String, String> placeholders) {
        player.sendMessage(withPrefix(path, placeholders));
    }

    /**
     * Gibt eine Nachricht als String zurück (für Konsole)
     */
    public String getString(String path) {
        return messages.getString(path, "");
    }

    /**
     * Gibt eine Liste von Werten zurück
     */
    public java.util.List<String> getList(String path) {
        return messages.getStringList(path);
    }

    /**
     * Setze eine Message und speichere in YAML
     */
    public void setMessage(String path, String value) throws IOException {
        messages.set(path, value);
        messages.save(messagesFile);
        plugin.getLogger().info("✓ Message aktualisiert: " + path);
    }
}
