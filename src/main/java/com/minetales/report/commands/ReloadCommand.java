package com.minetales.report.commands;

import com.minetales.report.MinetalesReportPlugin;
import com.minetales.report.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * ReloadCommand - Lädt Configuration und Messages neu
 */
public class ReloadCommand implements CommandExecutor {
    
    private final MinetalesReportPlugin plugin;
    private final MessageManager messageManager;

    public ReloadCommand(MinetalesReportPlugin plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Nur Admin-Permission
        if (!sender.hasPermission("report.admin")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                messageManager.send(player, "reload.no-permission");
            }
            return true;
        }

        try {
            // Lade config.yml neu
            plugin.reloadConfig();
            plugin.getConfigManager().reload();
            
            // Lade messages.yml neu
            messageManager.reload();
            
            // Lade GUI neu
            plugin.getReportReasonGUI().reload();
            
            // Sende Success-Nachricht
            if (sender instanceof Player) {
                Player player = (Player) sender;
                messageManager.send(player, "reload.success");
            } else {
                plugin.getLogger().info("✓ Configuration und Messages neu geladen");
            }
            
            return true;
        } catch (Exception e) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                messageManager.send(player, "reload.error");
            } else {
                plugin.getLogger().severe("✗ Fehler beim Neuladen: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }
}
