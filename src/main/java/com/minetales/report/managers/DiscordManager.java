package com.minetales.report.managers;

import com.google.gson.JsonObject;
import com.minetales.report.MinetalesReportPlugin;
import com.minetales.report.models.Report;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class DiscordManager {
    
    private final MinetalesReportPlugin plugin;
    private final String webhookUrl;
    private final String adminRoleId;
    private final int embedColor;
    private boolean webhookValid = false;

    public DiscordManager(MinetalesReportPlugin plugin) {
        this.plugin = plugin;
        this.webhookUrl = plugin.getConfigManager().getWebhookUrl();
        this.adminRoleId = plugin.getConfigManager().getAdminRoleId();
        this.embedColor = plugin.getConfigManager().getEmbedColor();
        this.webhookValid = validateWebhook();
    }

    private boolean validateWebhook() {
        return webhookUrl != null && !webhookUrl.isEmpty() && webhookUrl.contains("discord.com");
    }

    public void sendReportNotification(Report report, int totalReports, int reasonCount) {
        if (!webhookValid) {
            plugin.getLogger().severe("❌ [DEBUG] Webhook URL ungültig oder nicht konfiguriert!");
            plugin.getLogger().severe("❌ [DEBUG] URL: " + (webhookUrl == null ? "NULL" : (webhookUrl.isEmpty() ? "LEER" : "SET")));
            return;
        }

        plugin.getLogger().info("[DEBUG] Report Notification wird erstellt...");
        plugin.getLogger().info("[DEBUG] - Reporter: " + report.getReporter());
        plugin.getLogger().info("[DEBUG] - Reported: " + report.getReported());
        plugin.getLogger().info("[DEBUG] - Reason: " + report.getReason());
        
        JsonObject embed = createReportEmbed(report, totalReports, reasonCount);
        
        plugin.getLogger().info("[DEBUG] Embed JSON: " + embed.toString().substring(0, Math.min(200, embed.toString().length())) + "...");
        
        sendWebhookMessage(report.getReportId(), embed);
    }

    public void sendCloseNotification(Report report) {
        if (!webhookValid) return;

        JsonObject embed = createCloseEmbed(report);
        updateWebhookMessage(report.getReportId(), embed);
    }

    private JsonObject createReportEmbed(Report report, int totalReports, int reasonCount) {
        JsonObject embed = new JsonObject();
        
        embed.addProperty("title", "🚨 SPIELER REPORT 🚨");
        embed.addProperty("description", "Ein Spieler wurde vom Report-System gemeldet");
        embed.addProperty("color", embedColor);
        
        // Thumbnail
        JsonObject thumbnail = new JsonObject();
        thumbnail.addProperty("url", "https://minotar.net/avatar/" + report.getReported() + "/64");
        embed.add("thumbnail", thumbnail);
        
        // Fields als JSON Array
        com.google.gson.JsonArray fieldsArray = new com.google.gson.JsonArray();
        
        // Report ID
        JsonObject field1 = new JsonObject();
        field1.addProperty("name", "📋 Report ID");
        field1.addProperty("value", "`" + report.getReportId() + "`");
        field1.addProperty("inline", true);
        fieldsArray.add(field1);
        
        // Server
        JsonObject field2 = new JsonObject();
        field2.addProperty("name", "🌐 Server");
        field2.addProperty("value", "`" + report.getServer() + "`");
        field2.addProperty("inline", true);
        fieldsArray.add(field2);
        
        // Reporter
        JsonObject field3 = new JsonObject();
        field3.addProperty("name", "👤 Reporter");
        field3.addProperty("value", "`" + report.getReporter() + "`");
        field3.addProperty("inline", false);
        fieldsArray.add(field3);
        
        // Gemeldeter Spieler
        JsonObject field4 = new JsonObject();
        field4.addProperty("name", "⚠️ Spieler");
        field4.addProperty("value", "`" + report.getReported() + "`");
        field4.addProperty("inline", false);
        fieldsArray.add(field4);
        
        // Grund
        JsonObject field5 = new JsonObject();
        field5.addProperty("name", "📝 Grund");
        field5.addProperty("value", report.getReason());
        field5.addProperty("inline", true);
        fieldsArray.add(field5);
        
        embed.add("fields", fieldsArray);
        
        // Footer
        JsonObject footer = new JsonObject();
        footer.addProperty("text", "AbyssBay Report System");
        embed.add("footer", footer);
        
        // Timestamp
        embed.addProperty("timestamp", Instant.now().toString());
        
        return embed;
    }

    private JsonObject createCloseEmbed(Report report) {
        JsonObject embed = new JsonObject();
        
        embed.addProperty("title", "✅ REPORT GESCHLOSSEN ✅");
        embed.addProperty("color", 10040115);  // Grün
        
        JsonObject footer = new JsonObject();
        footer.addProperty("text", "AbyssBay Report System");
        embed.add("footer", footer);
        
        embed.addProperty("timestamp", Instant.now().toString());
        
        return embed;
    }

    private void sendWebhookMessage(String reportId, JsonObject embed) {
        try {
            JsonObject message = new JsonObject();
            message.addProperty("content", "<@&" + adminRoleId + ">");
            
            // Erstelle JSON Array für embeds
            com.google.gson.JsonArray embedsArray = new com.google.gson.JsonArray();
            embedsArray.add(embed);
            message.add("embeds", embedsArray);
            
            String jsonString = message.toString();
            
            plugin.getLogger().info("[DEBUG] Sende Webhook Nachricht (" + jsonString.length() + " bytes)");
            plugin.getLogger().info("[DEBUG] Webhook URL: " + webhookUrl.substring(0, Math.min(50, webhookUrl.length())) + "...");
            
            URL url = new URL(webhookUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "AbyssBay-ReportBot/1.0");
            
            // Schreibe JSON Daten
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                os.flush();
            }
            
            // Lese Response
            int statusCode = connection.getResponseCode();
            plugin.getLogger().info("[DEBUG] Discord Response Status: " + statusCode);
            
            if (statusCode >= 200 && statusCode < 300) {
                plugin.getLogger().info("✅ [DEBUG] Report #" + reportId + " zu Discord gesendet!");
            } else {
                String errorMessage = readInputStream(connection.getErrorStream());
                plugin.getLogger().severe("❌ [DEBUG] Discord Fehler (" + statusCode + "): " + errorMessage);
            }
            
            connection.disconnect();
        } catch (Exception e) {
            plugin.getLogger().severe("❌ [DEBUG] Fehler beim Senden zu Discord: " + e.getMessage());
            plugin.getLogger().severe("❌ [DEBUG] Exception: " + e.getClass().getName());
            e.printStackTrace();
        }
    }
    
    private String readInputStream(java.io.InputStream is) {
        if (is == null) return "No error details";
        try {
            java.util.Scanner scanner = new java.util.Scanner(is).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void updateWebhookMessage(String reportId, JsonObject embed) {
        // Discord Webhook Nachricht aktualisieren
        // Benötigt Message ID - würde über Datenbank gespeichert
    }

    public boolean isWebhookValid() {
        return webhookValid;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getAdminRoleId() {
        return adminRoleId;
    }
}
