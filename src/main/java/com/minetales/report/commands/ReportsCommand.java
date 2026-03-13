package com.minetales.report.commands;

import com.minetales.report.MinetalesReportPlugin;
import com.minetales.report.models.Report;
import com.minetales.report.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportsCommand implements CommandExecutor {
    
    private final MinetalesReportPlugin plugin;
    private final MessageManager messageManager;
    private static final int ITEMS_PER_PAGE = 5;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public ReportsCommand(MinetalesReportPlugin plugin) {
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

        if (!player.hasPermission("report.admin") && !player.hasPermission("report.staff")) {
            messageManager.send(player, "command.no_permission");
            return true;
        }

        if (args.length == 0) {
            handleHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "check":
                handleCheck(player, args);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "close":
                handleClose(player, args);
                break;
            case "view":
                handleView(player, args);
                break;
            default:
                messageManager.send(player, "reports.check.no_args");
        }

        return true;
    }

    private void handleCheck(Player player, String[] args) {
        int page = 1;
        
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                messageManager.send(player, "command.invalid_id", "id", args[1]);
                return;
            }
        }

        List<Report> reports = plugin.getReportManager().getOpenReports();
        
        if (reports.isEmpty()) {
            messageManager.send(player, "reports.check.no_reports");
            return;
        }

        int maxPage = (int) Math.ceil((double) reports.size() / ITEMS_PER_PAGE);
        if (page > maxPage) page = maxPage;

        int startIdx = (page - 1) * ITEMS_PER_PAGE;
        int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, reports.size());

        messageManager.send(player, "reports.check.list_header");
        
        for (int i = startIdx; i < endIdx; i++) {
            Report report = reports.get(i);
            String date = report.getCreatedAt().format(dateFormatter);
            
            messageManager.send(player, "reports.check.item",
                "id", report.getReportId(),
                "reporter", report.getReporter(),
                "reported", report.getReported(),
                "reason", report.getReason(),
                "date", date
            );
        }

        messageManager.send(player, "reports.check.item_count", "count", String.valueOf(reports.size()));
        messageManager.send(player, "reports.check.pagination", "page", String.valueOf(page), "maxpage", String.valueOf(maxPage));
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            messageManager.send(player, "reports.info.no_args");
            return;
        }

        String reportId = args[1];
        Report report = plugin.getReportManager().getReport(reportId);

        if (report == null) {
            messageManager.send(player, "reports.close.not_found", "id", reportId);
            return;
        }

        messageManager.send(player, "reports.info.header");
        messageManager.send(player, "reports.info.id", "id", report.getReportId());
        messageManager.send(player, "reports.info.reporter", "reporter", report.getReporter());
        messageManager.send(player, "reports.info.reported", "reported", report.getReported());
        messageManager.send(player, "reports.info.reason", "reason", report.getReason());
        messageManager.send(player, "reports.info.server", "server", report.getServer());
        messageManager.send(player, "reports.info.status", "status", report.getStatus());
        messageManager.send(player, "reports.info.created", "created", report.getCreatedAt().format(dateFormatter));

        if (report.getClosedAt() != null) {
            messageManager.send(player, "reports.info.closed", "closed", report.getClosedAt().format(dateFormatter));
            messageManager.send(player, "reports.info.closed_by", "closed_by", report.getClosedBy());
            messageManager.send(player, "reports.info.close_reason", "close_reason", report.getCloseReason());
        }
    }

    private void handleClose(Player player, String[] args) {
        if (args.length < 2) {
            messageManager.send(player, "reports.close.no_args");
            return;
        }

        String reportId = args[1];
        String closeReason = "Bearbeitet";
        
        if (args.length > 2) {
            closeReason = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
        }

        Report report = plugin.getReportManager().getReport(reportId);

        if (report == null) {
            messageManager.send(player, "reports.close.not_found", "id", reportId);
            return;
        }

        if (!report.getStatus().equalsIgnoreCase("OPEN")) {
            messageManager.send(player, "reports.close.already_closed", "id", reportId);
            return;
        }

        boolean success = plugin.getReportManager().closeReport(reportId, player.getName(), closeReason);

        if (success) {
            // Benachrichtige nur den ausführenden Admin
            messageManager.send(player, "reports.close.success", 
                "id", reportId, 
                "reason", closeReason
            );
            
            // Benachrichtige den Reporter (Ersteller des Reports)
            Player reporter = org.bukkit.Bukkit.getPlayer(report.getReporter());
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
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            messageManager.send(player, "reports.view.no_args");
            return;
        }

        String playerName = args[1];
        List<Report> reports = plugin.getReportManager().getReportsByReported(playerName);

        if (reports.isEmpty()) {
            messageManager.send(player, "reports.view.no_reports", "player", playerName);
            return;
        }

        messageManager.send(player, "reports.view.header", "player", playerName);
        messageManager.send(player, "reports.view.count", "count", String.valueOf(reports.size()));
        
        int openCount = 0;
        int closedCount = 0;
        
        for (Report report : reports) {
            String date = report.getCreatedAt().format(dateFormatter);
            String status = report.getStatus();
            
            if (status.equalsIgnoreCase("OPEN")) {
                openCount++;
                messageManager.send(player, "reports.view.item_open",
                    "id", report.getReportId(),
                    "reporter", report.getReporter(),
                    "reason", report.getReason(),
                    "date", date
                );
            } else {
                closedCount++;
                messageManager.send(player, "reports.view.item_closed",
                    "id", report.getReportId(),
                    "reporter", report.getReporter(),
                    "reason", report.getReason(),
                    "date", date,
                    "closed_by", report.getClosedBy() != null ? report.getClosedBy() : "Unbekannt"
                );
            }
        }

        messageManager.send(player, "reports.view.summary", "open", String.valueOf(openCount), "closed", String.valueOf(closedCount));
    }

    private void handleHelp(Player player) {
        messageManager.send(player, "reports.help.header");
        messageManager.send(player, "reports.help.check");
        messageManager.send(player, "reports.help.info");
        messageManager.send(player, "reports.help.close");
        messageManager.send(player, "reports.help.view");
        
        if (player.hasPermission("report.admin")) {
            messageManager.send(player, "reports.help.admin_header");
            messageManager.send(player, "reports.help.reload");
            messageManager.send(player, "reports.help.message");
        }
    }
}
