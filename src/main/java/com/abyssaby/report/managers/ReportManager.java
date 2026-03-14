package com.abyssaby.report.managers;

import com.abyssaby.report.AbyssabyReportPlugin;
import com.abyssaby.report.models.Report;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Report Manager - Verwaltet Reports mit MySQL Speicherung
 */
public class ReportManager {
    
    private final AbyssabyReportPlugin plugin;
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    public ReportManager(AbyssabyReportPlugin plugin) {
        this.plugin = plugin;
    }

    public Report createReport(String reporter, String reported, String reason) {
        String server = plugin.getConfigManager().getServerName();
        
        // Generiere eindeutige Report ID - REP-{1-10000}
        String reportId = generateUniqueReportId();
        Report report = new Report(reportId, reporter, reported, reason, server);
        
        // Speichere in Datenbank
        try {
            plugin.getDatabaseManager().saveReport(report);
            plugin.getLogger().info("✓ Report erstellt: " + reportId);
            return report;
        } catch (SQLException e) {
            plugin.getLogger().warning("✗ Fehler beim Speichern des Reports: " + e.getMessage());
            return null;
        }
    }

    private String generateUniqueReportId() {
        int maxAttempts = 100;
        for (int i = 0; i < maxAttempts; i++) {
            int randomNumber = (int)(Math.random() * 10000) + 1;
            String reportId = "REP-" + randomNumber;
            
            // Prüfe ob ID bereits existiert
            try {
                Report existing = getReport(reportId);
                if (existing == null) {
                    return reportId;
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Fehler beim Prüfen der Report ID: " + e.getMessage());
            }
        }
        
        // Fallback wenn alle Versuche fehlschlagen (sollte nicht vorkommen)
        throw new RuntimeException("Konnte keine eindeutige Report ID generieren");
    }

    public Report getReport(String reportId) {
        try {
            return plugin.getDatabaseManager().getReport(reportId);
        } catch (SQLException e) {
            plugin.getLogger().warning("✗ Fehler beim Abrufen des Reports: " + e.getMessage());
            return null;
        }
    }

    public List<Report> getOpenReports() {
        try {
            return plugin.getDatabaseManager().getOpenReports();
        } catch (SQLException e) {
            plugin.getLogger().warning("✗ Fehler beim Abrufen offener Reports: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Report> getAllReports() {
        try {
            return plugin.getDatabaseManager().getAllReports();
        } catch (SQLException e) {
            plugin.getLogger().warning("✗ Fehler beim Abrufen aller Reports: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Report> getReportsByReported(String reportedName) {
        try {
            return plugin.getDatabaseManager().getReportsByReported(reportedName);
        } catch (SQLException e) {
            plugin.getLogger().warning("✗ Fehler beim Abrufen von Reports: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean closeReport(String reportId, String closedBy, String closeReason) {
        try {
            plugin.getDatabaseManager().closeReport(reportId, closedBy, closeReason);
            return true;
        } catch (SQLException e) {
            plugin.getLogger().warning("✗ Fehler beim Schließen des Reports: " + e.getMessage());
            return false;
        }
    }

    public int getOpenReportCount() {
        try {
            return plugin.getDatabaseManager().getOpenReportCount();
        } catch (SQLException e) {
            plugin.getLogger().warning("✗ Fehler beim Zählen offener Reports: " + e.getMessage());
            return 0;
        }
    }

    public boolean hasCooldown(Player player) {
        if (!cooldowns.containsKey(player.getName())) {
            return false;
        }
        
        long lastReport = cooldowns.get(player.getName());
        long cooldownTime = plugin.getConfigManager().getCooldownSeconds() * 1000L;
        long timePassed = System.currentTimeMillis() - lastReport;
        
        return timePassed < cooldownTime;
    }

    public long getCooldownRemaining(Player player) {
        long lastReport = cooldowns.getOrDefault(player.getName(), 0L);
        long cooldownTime = plugin.getConfigManager().getCooldownSeconds() * 1000L;
        long timePassed = System.currentTimeMillis() - lastReport;
        return Math.max(0, cooldownTime - timePassed);
    }

    public void setCooldown(Player player) {
        cooldowns.put(player.getName(), System.currentTimeMillis());
    }

    public void cleanup() {
        cooldowns.clear();
    }
}
