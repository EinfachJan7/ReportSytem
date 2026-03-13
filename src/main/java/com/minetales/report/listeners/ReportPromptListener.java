package com.minetales.report.listeners;

import com.minetales.report.MinetalesReportPlugin;
import com.minetales.report.models.Report;
import com.minetales.report.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Höre auf Chat von Spielern die eine Begründung für "Sonstiges" eingeben
 */
public class ReportPromptListener implements Listener {
    
    private final MinetalesReportPlugin plugin;
    private final MessageManager messageManager;
    private final Map<UUID, String> waitingForReason = new HashMap<>();
    
    public ReportPromptListener(MinetalesReportPlugin plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Markiere Spieler als wartend auf "Sonstiges"-Begründung
     */
    public void waitForReason(Player player, String reportedName) {
        waitingForReason.put(player.getUniqueId(), reportedName);
        messageManager.send(player, "report.enter_reason");
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        // Prüfe ob dieser Spieler auf eine Begründung wartet
        if (!waitingForReason.containsKey(playerUUID)) {
            return;
        }
        
        event.setCancelled(true);
        
        String reportedName = waitingForReason.remove(playerUUID);
        String reason = "Sonstiges: " + event.getMessage();
        
        // Überprüfe Cooldown
        if (plugin.getReportManager().hasCooldown(player)) {
            long remaining = plugin.getReportManager().getCooldownRemaining(player) / 1000;
            messageManager.send(player, "report.cooldown", "seconds", String.valueOf(remaining));
            return;
        }
        
        // Erstelle Report
        Report report = plugin.getReportManager().createReport(
            player.getName(),
            reportedName,
            reason
        );
        
        // Prüfe Erfolg
        if (report == null) {
            messageManager.send(player, "error.report_creation_failed");
            return;
        }
        
        plugin.getReportManager().setCooldown(player);
        
        // Sende zu Discord
        plugin.getDiscordManager().sendReportNotification(report, 0, 0);
        
        // Benachrichtige Admins
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission(plugin.getConfigManager().getReportAdminPermission())) {
                messageManager.send(staff, "report.staff_notification", 
                    "reporter", report.getReporter(),
                    "reported", report.getReported(),
                    "reason", report.getReason(),
                    "id", report.getReportId()
                );
            }
        }
        
        messageManager.send(player, "report.success", "id", report.getReportId());
    }
}
