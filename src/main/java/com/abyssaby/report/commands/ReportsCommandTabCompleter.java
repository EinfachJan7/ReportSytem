package com.abyssaby.report.commands;

import com.abyssaby.report.AbyssabyReportPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportsCommandTabCompleter implements TabCompleter {
    
    private final AbyssabyReportPlugin plugin;
    
    public ReportsCommandTabCompleter(AbyssabyReportPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!(sender instanceof Player)) {
            return completions;
        }

        Player player = (Player) sender;
        
        if (!player.hasPermission("report.admin")) {
            return completions;
        }

        // Erstes Argument = Subcommand
        if (args.length == 1) {
            String subcommandPrefix = args[0].toLowerCase();
            List<String> subcommands = Arrays.asList("check", "info", "close", "view");
            
            for (String subcommand : subcommands) {
                if (subcommand.startsWith(subcommandPrefix)) {
                    completions.add(subcommand);
                }
            }
            
            return completions;
        }

        // Zweites Argument = Report ID (für info und close) ODER Spieler (für view)
        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            
            if (subcommand.equals("view")) {
                // Für "view" zeige Online-Spieler
                String playerPrefix = args[1].toLowerCase();
                for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(playerPrefix)) {
                        completions.add(p.getName());
                    }
                }
            } else if (subcommand.equals("info") || subcommand.equals("close")) {
                List<com.abyssaby.report.models.Report> reports = plugin.getReportManager().getAllReports();
                String reportIdPrefix = args[1].toLowerCase();
                
                for (com.abyssaby.report.models.Report report : reports) {
                    if (report.getReportId().toLowerCase().startsWith(reportIdPrefix)) {
                        completions.add(report.getReportId());
                    }
                }
            }
            
            return completions;
        }

        return completions;
    }
}
