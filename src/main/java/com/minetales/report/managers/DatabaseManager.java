package com.minetales.report.managers;

import com.minetales.report.MinetalesReportPlugin;
import com.minetales.report.models.Report;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;

/**
 * Database Manager - MySQL Integration mit HikariCP Connection Pool
 */
public class DatabaseManager {
    
    private final MinetalesReportPlugin plugin;
    private HikariDataSource dataSource;
    private boolean dbEnabled;

    public DatabaseManager(MinetalesReportPlugin plugin) {
        this.plugin = plugin;
        this.dbEnabled = false;
    }

    public boolean initialize() {
        try {
            if (!plugin.getConfigManager().isDatabaseEnabled()) {
                plugin.getLogger().info("✓ Datenbank deaktiviert - verwende nur Discord");
                dbEnabled = false;
                return true;
            }

            String host = plugin.getConfigManager().getDatabaseHost();
            int port = plugin.getConfigManager().getDatabasePort();
            String database = plugin.getConfigManager().getDatabaseName();
            String user = plugin.getConfigManager().getDatabaseUser();
            String password = plugin.getConfigManager().getDatabasePassword();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + 
                    "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Berlin&allowMultiQueries=true");
            config.setUsername(user);
            config.setPassword(password);
            config.setMaximumPoolSize(plugin.getConfigManager().getDatabaseMaxPoolSize());
            config.setMinimumIdle(plugin.getConfigManager().getDatabaseMinIdle());
            config.setConnectionTimeout(plugin.getConfigManager().getDatabaseConnectionTimeout());
            config.setIdleTimeout(plugin.getConfigManager().getDatabaseIdleTimeout());
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setAutoCommit(true);

            dataSource = new HikariDataSource(config);
            
            // Teste Connection
            try (Connection conn = dataSource.getConnection()) {
                plugin.getLogger().info("✓ MySQL Verbindung erfolgreich!");
            }

            // Erstelle Tabellen
            createTables();
            
            dbEnabled = true;
            plugin.getLogger().info("✓ Datenbank initialisiert (" + database + "@" + host + ")");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("✗ Fehler bei Datenbankinitialisierung: " + e.getMessage());
            e.printStackTrace();
            dbEnabled = false;
            return false;
        }
    }

    private void createTables() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String createReportsTable = "CREATE TABLE IF NOT EXISTS reports (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "report_id VARCHAR(50) UNIQUE NOT NULL, " +
                    "reporter VARCHAR(128) NOT NULL, " +
                    "reported VARCHAR(128) NOT NULL, " +
                    "reason VARCHAR(256) NOT NULL, " +
                    "server VARCHAR(128) NOT NULL, " +
                    "status VARCHAR(20) NOT NULL DEFAULT 'OPEN', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "closed_at TIMESTAMP NULL, " +
                    "closed_by VARCHAR(128), " +
                    "close_reason TEXT, " +
                    "INDEX idx_status (status), " +
                    "INDEX idx_reported (reported), " +
                    "INDEX idx_created (created_at), " +
                    "INDEX idx_report_id (report_id)" +
                    ")";
            
            stmt.execute(createReportsTable);
            plugin.getLogger().info("✓ Reports-Tabelle erstellt/vorhanden");
        }
    }

    public void saveReport(Report report) throws SQLException {
        if (!dbEnabled) return;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO reports (report_id, reporter, reported, reason, server, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, report.getReportId());
            stmt.setString(2, report.getReporter());
            stmt.setString(3, report.getReported());
            stmt.setString(4, report.getReason());
            stmt.setString(5, report.getServer());
            stmt.setString(6, report.getStatus());
            
            stmt.executeUpdate();
            plugin.getLogger().info("✓ Report in Datenbank gespeichert: " + report.getReportId());
        }
    }

    public Report getReport(String reportId) throws SQLException {
        if (!dbEnabled) return null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM reports WHERE report_id = ?")) {
            
            stmt.setString(1, reportId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return resultSetToReport(rs);
                }
            }
        }
        return null;
    }

    public List<Report> getOpenReports() throws SQLException {
        return getReportsByStatus("OPEN");
    }

    public List<Report> getAllReports() throws SQLException {
        if (!dbEnabled) return new ArrayList<>();

        List<Report> reports = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM reports ORDER BY created_at DESC")) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(resultSetToReport(rs));
                }
            }
        }
        return reports;
    }

    public List<Report> getReportsByStatus(String status) throws SQLException {
        if (!dbEnabled) return new ArrayList<>();

        List<Report> reports = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM reports WHERE status = ? ORDER BY created_at DESC")) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(resultSetToReport(rs));
                }
            }
        }
        return reports;
    }

    public List<Report> getReportsByReported(String reportedName) throws SQLException {
        if (!dbEnabled) return new ArrayList<>();

        List<Report> reports = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM reports WHERE reported = ? ORDER BY created_at DESC")) {
            
            stmt.setString(1, reportedName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(resultSetToReport(rs));
                }
            }
        }
        return reports;
    }

    public void closeReport(String reportId, String closedBy, String closeReason) throws SQLException {
        if (!dbEnabled) return;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE reports SET status = 'CLOSED', closed_at = NOW(), closed_by = ?, close_reason = ? WHERE report_id = ?")) {
            
            stmt.setString(1, closedBy);
            stmt.setString(2, closeReason);
            stmt.setString(3, reportId);
            
            stmt.executeUpdate();
        }
    }

    public int getOpenReportCount() throws SQLException {
        if (!dbEnabled) return 0;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) as count FROM reports WHERE status = 'OPEN'")) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    private Report resultSetToReport(ResultSet rs) throws SQLException {
        Report report = new Report(
                rs.getString("report_id"),
                rs.getString("reporter"),
                rs.getString("reported"),
                rs.getString("reason"),
                rs.getString("server")
        );
        
        report.setStatus(rs.getString("status"));
        
        Timestamp closedAt = rs.getTimestamp("closed_at");
        if (closedAt != null) {
            report.close(
                    rs.getString("closed_by"),
                    rs.getString("close_reason")
            );
        }
        
        return report;
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("✓ Datenbankverbindung geschlossen");
        }
    }

    public boolean isEnabled() {
        return dbEnabled;
    }
}
