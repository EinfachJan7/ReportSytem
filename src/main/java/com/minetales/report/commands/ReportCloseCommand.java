package com.minetales.report.commands;

import com.minetales.report.MinetalesReportPlugin;
import com.minetales.report.models.Report;
import com.minetales.report.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCloseCommand implements CommandExecutor {
    
    private final MinetalesReportPlugin plugin;
    private final MessageManager messageManager;

    public ReportCloseCommand(MinetalesReportPlugin plugin) {
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

        if (!player.hasPermission("report.admin")) {
            messageManager.send(player, "command.no_permission");
            return true;
        }

        if (args.length == 0) {
            messageManager.send(player, "reports.close.no_args");
            return true;
        }

        String reportId = args[0];
        String closeReason = "Bearbeitet";
        
        if (args.length > 1) {
            closeReason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        }

        Report report = plugin.getReportManager().getReport(reportId);

        if (report == null) {
            messageManager.send(player, "reports.close.not_found", "id", reportId);
            return true;
        }

        if (!report.getStatus().equalsIgnoreCase("OPEN")) {
            messageManager.send(player, "reports.close.already_closed", "id", reportId);
            return true;
        }

        boolean success = plugin.getReportManager().closeReport(reportId, player.getName(), closeReason);

        if (success) {
            // Benachrichtige nur den ausführenden Admin
            messageManager.send(player, "reports.close.success", 
                "id", reportId, 
                "reason", closeReason
            );
            
            // Benachrichtige den Reporter (Ersteller des Reports)
            org.bukkit.entity.Player reporter = org.bukkit.Bukkit.getPlayer(report.getReporter());
            if (reporter != null && reporter.isOnline()) {
                messageManager.send(reporter, "report.report_closed",
                    "id", reportId,
                    "closed_by", player.getName(),
                    "reason", closeReason,
                    "player", report.getReported()
                );
            }
        } else {
            messageManager.send(player, "reports.close.error", "error", "Datenbankfehler");
        }

        return true;
    }
}
