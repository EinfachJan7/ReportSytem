package com.minetales.report.listeners;

import com.minetales.report.MinetalesReportPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    
    private final MinetalesReportPlugin plugin;

    public PlayerQuitListener(MinetalesReportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Spieler ist jetzt offline - können trotzdem noch gemeldet werden
    }
}
