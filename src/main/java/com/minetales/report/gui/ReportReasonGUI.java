package com.minetales.report.gui;

import com.minetales.report.MinetalesReportPlugin;
import com.minetales.report.utils.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportReasonGUI implements Listener {
    
    private final MinetalesReportPlugin plugin;
    private final MessageManager messageManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();
    private final Map<String, String> playerReports = new HashMap<>();
    private YamlConfiguration guiConfig;
    private Material backgroundMaterial;
    private String backgroundName;
    
    public ReportReasonGUI(MinetalesReportPlugin plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        loadGuiConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    private void loadGuiConfig() {
        File guiFile = new File(plugin.getDataFolder(), "gui.yml");
        
        if (!guiFile.exists()) {
            plugin.saveResource("gui.yml", false);
        }
        
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
        
        try {
            backgroundMaterial = Material.valueOf(guiConfig.getString("report-reasons.background-item", "GRAY_STAINED_GLASS_PANE"));
        } catch (Exception e) {
            backgroundMaterial = Material.GRAY_STAINED_GLASS_PANE;
        }
        
        backgroundName = parseMiniMessage(guiConfig.getString("report-reasons.background-name", " "));
    }
    
    /**
     * Wandelt MiniMessage String zu Legacy Text um
     */
    private String parseMiniMessage(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        try {
            Component component = miniMessage.deserialize(text);
            return legacySerializer.serialize(component);
        } catch (Exception e) {
            // Falls Parsing fehlschlägt, gib Original zurück
            return text;
        }
    }
    
    /**
     * Lade das GUI neu (wird beim /reload Command aufgerufen)
     */
    public void reload() {
        loadGuiConfig();
    }
    
    /**
     * Öffne das Gründe-GUI für einen Spieler
     */
    public void openReasonGUI(Player player, String reportedName) {
        List<String> reasons = plugin.getConfigManager().getReportReasons();
        int rows = guiConfig.getInt("report-reasons.rows", 5);
        int size = rows * 9;
        
        String rawTitle = guiConfig.getString("report-reasons.title", "§c→ Report: {player} §c←")
            .replace("{player}", reportedName);
        String title = parseMiniMessage(rawTitle);
        
        Inventory gui = Bukkit.createInventory(null, size, title);
        
        // Fülle mit Background-Items
        for (int i = 0; i < size; i++) {
            gui.setItem(i, createBackgroundItem());
        }
        
        // Setze Report-Gründe auf configured Slots
        for (String reason : reasons) {
            setReasonItemToSlot(gui, reason);
        }
        
        // Speichere Report-Ziel
        playerReports.put(player.getUniqueId().toString(), reportedName);
        
        player.openInventory(gui);
    }
    
    /**
     * Erstelle ein Background-Item
     */
    private ItemStack createBackgroundItem() {
        ItemStack item = new ItemStack(backgroundMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(backgroundName);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Erstelle ein Item für einen Report-Grund
     */
    private ItemStack createReasonItem(String reason) {
        String reasonPath = "report-reasons.reasons." + reason;
        
        String materialName = guiConfig.getString(reasonPath + ".material", "PAPER");
        Material material;
        try {
            material = Material.valueOf(materialName);
        } catch (Exception e) {
            material = Material.PAPER;
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            String displayNameRaw = guiConfig.getString(reasonPath + ".name", "§e§l" + reason);
            String displayName = parseMiniMessage(displayNameRaw);
            meta.setDisplayName(displayName);
            
            List<String> lore = new ArrayList<>();
            List<String> configLore = guiConfig.getStringList(reasonPath + ".lore");
            if (!configLore.isEmpty()) {
                for (String loreLine : configLore) {
                    lore.add(parseMiniMessage(loreLine));
                }
            } else {
                lore.add(parseMiniMessage("§7Klicke um auszuwählen"));
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Setze Item auf configured Slot
     */
    public void setReasonItemToSlot(Inventory gui, String reason) {
        String reasonPath = "report-reasons.reasons." + reason;
        int slot = guiConfig.getInt(reasonPath + ".slot", -1);
        
        if (slot >= 0 && slot < gui.getSize()) {
            ItemStack item = createReasonItem(reason);
            gui.setItem(slot, item);
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        String reportedName = playerReports.get(player.getUniqueId().toString());
        
        // Nur GUI Events blockieren, wenn Spieler einen Report aktiv hat
        if (reportedName == null) {
            return;
        }
        
        event.setCancelled(true);
        
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        
        // Prüfe ob es ein Background-Item ist
        if (event.getCurrentItem().getType() == backgroundMaterial) {
            return;
        }
        
        // Bestimme den Grund basierend auf dem geklickten Slot
        int clickedSlot = event.getSlot();
        String reason = "Sonstiges";
        
        List<String> reasons = plugin.getConfigManager().getReportReasons();
        for (String r : reasons) {
            String reasonPath = "report-reasons.reasons." + r;
            int slot = guiConfig.getInt(reasonPath + ".slot", -1);
            if (slot == clickedSlot) {
                reason = r;
                break;
            }
        }
        
        player.closeInventory();
        
        // Wenn "Sonstiges", frage nach Begründung
        if (reason.equals("Sonstiges")) {
            plugin.getReportPromptListener().waitForReason(player, reportedName);
        } else {
            // Führe den Report aus
            player.performCommand("report " + reportedName + " " + reason);
        }
        
        // Entferne den Spieler aus der Map
        playerReports.remove(player.getUniqueId().toString());
    }
}
