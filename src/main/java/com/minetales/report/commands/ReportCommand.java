package com.minetales.report.commands;

import com.minetales.report.MinetalesReportPlugin;
import com.minetales.report.models.Report;
import com.minetales.report.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {
    
    private final MinetalesReportPlugin plugin;
    private final MessageManager messageManager;

    public ReportCommand(MinetalesReportPlugin plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl nutzen!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(plugin.getConfigManager().getReportUsePermission())) {
            messageManager.send(player, "report.no_permission");
            return true;
        }

        if (args.length == 0) {
            messageManager.send(player, "report.usage");
            messageManager.send(player, "report.reasons.header");
            plugin.getConfigManager().getReportReasons().forEach(reason -> 
                player.sendMessage("  §8- §7" + reason)
            );
            return true;
        }

        String reportedName = args[0];

        // Nur 1 Argument - Öffne Gründe-GUI
        if (args.length == 1) {
            plugin.getReportReasonGUI().openReasonGUI(player, reportedName);
            return true;
        }

        // 2+ Argumente - Verarbeite Report
        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        // Validiere Grund
        if (!plugin.getConfigManager().isValidReason(reason)) {
            messageManager.send(player, "report.invalid_reason");
            return true;
        }

        // Wenn "Sonstiges", frage nach Begründung
        if (reason.equals("Sonstiges")) {
            plugin.getReportPromptListener().waitForReason(player, reportedName);
            return true;
        }

        // Überprüfe Cooldown
        if (plugin.getReportManager().hasCooldown(player)) {
            long remaining = plugin.getReportManager().getCooldownRemaining(player) / 1000;
            messageManager.send(player, "report.cooldown", "seconds", String.valueOf(remaining));
            return true;
        }

        // Überprüfe ob Spieler existiert
        OfflinePlayer reported = Bukkit.getOfflinePlayer(reportedName);

        // Selbstreport-Schutz
        if (reportedName.equalsIgnoreCase(player.getName())) {
            if (!player.isOp()) {
                messageManager.send(player, "report.self_report");
                return true;
            }
        }

        // Erstelle Report
        Report report = plugin.getReportManager().createReport(
            player.getName(),
            reportedName,
            reason
        );

        // Prüfe ob Report erfolgreich erstellt wurde
        if (report == null) {
            messageManager.send(player, "error.report_creation_failed");
            return true;
        }

        plugin.getReportManager().setCooldown(player);

        // Sende zu Discord
        plugin.getDiscordManager().sendReportNotification(report, 0, 0);

        // Benachrichtige Admins/Staff mit Permission
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("report.admin") || staff.hasPermission("report.staff")) {
                messageManager.send(staff, "admin.report_created", 
                    "reporter", player.getName(),
                    "reported", reportedName,
                    "reason", reason);
            }
        }

        // Erfolgs-Nachricht mit Platzhaltern
        messageManager.send(player, "report.success");
        messageManager.send(player, "report.report_id", "id", report.getReportId());
        messageManager.send(player, "report.player", "player", reportedName);
        messageManager.send(player, "report.reason", "reason", reason);
        
        return true;
    }

}
