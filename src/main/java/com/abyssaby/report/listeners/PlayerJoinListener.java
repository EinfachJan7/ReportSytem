package com.abyssaby.report.listeners;

import com.abyssaby.report.AbyssabyReportPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    
    private final AbyssabyReportPlugin plugin;

    public PlayerJoinListener(AbyssabyReportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Spieler ist jetzt verfügbar zum reporten
    }
}
