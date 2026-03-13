package com.minetales.report.commands;

import com.minetales.report.MinetalesReportPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReportCommandTabCompleter implements TabCompleter {
    
    private final MinetalesReportPlugin plugin;
    
    public ReportCommandTabCompleter(MinetalesReportPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!(sender instanceof Player)) {
            return completions;
        }
        
        // Erstes Argument = Spielername
        if (args.length == 1) {
            String playerNamePrefix = args[0].toLowerCase();
            
            // Alle Online-Spieler hinzufügen
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(playerNamePrefix)) {
                    completions.add(player.getName());
                }
            }
            
            return completions;
        }
        
        // Zweites Argument = Report-Grund
        if (args.length == 2) {
            String reasonPrefix = args[1].toLowerCase();
            
            for (String reason : plugin.getConfigManager().getReportReasons()) {
                if (reason.toLowerCase().startsWith(reasonPrefix)) {
                    completions.add(reason);
                }
            }
            
            return completions;
        }
        
        return completions;
    }
}
