package com.minetales.report.listeners;

import com.minetales.report.MinetalesReportPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    
    private final MinetalesReportPlugin plugin;

    public PlayerJoinListener(MinetalesReportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Spieler ist jetzt verfügbar zum reporten
    }
}
