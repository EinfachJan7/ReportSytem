package com.abyssaby.report.listeners;

import com.abyssaby.report.AbyssabyReportPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    
    private final AbyssabyReportPlugin plugin;

    public PlayerQuitListener(AbyssabyReportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Spieler ist jetzt offline - können trotzdem noch gemeldet werden
    }
}
