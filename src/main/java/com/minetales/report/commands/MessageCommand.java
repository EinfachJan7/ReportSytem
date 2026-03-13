package com.minetales.report.commands;

import com.minetales.report.MinetalesReportPlugin;
import com.minetales.report.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand implements CommandExecutor {
    
    private final MinetalesReportPlugin plugin;
    private final MessageManager messageManager;

    public MessageCommand(MinetalesReportPlugin plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl nutzen!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("report.admin")) {
            messageManager.send(player, "command.no_permission");
            return true;
        }

        if (args.length < 2) {
            messageManager.send(player, "message.usage");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "set":
                handleSet(player, args);
                break;
            case "get":
                handleGet(player, args);
                break;
            case "reload":
                handleReload(player);
                break;
            default:
                messageManager.send(player, "message.usage");
        }

        return true;
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 3) {
            messageManager.send(player, "message.set.no_args");
            return;
        }

        String key = args[1];
        String value = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));

        try {
            plugin.getMessageManager().setMessage(key, value);
            messageManager.send(player, "message.set.success", "key", key);
        } catch (Exception e) {
            messageManager.send(player, "message.set.error", "error", e.getMessage());
        }
    }

    private void handleGet(Player player, String[] args) {
        if (args.length < 2) {
            messageManager.send(player, "message.get.no_args");
            return;
        }

        String key = args[1];
        String value = messageManager.getString(key);

        if (value == null || value.isEmpty()) {
            messageManager.send(player, "message.get.not_found", "key", key);
            return;
        }

        messageManager.send(player, "message.get.success", "key", key, "value", value);
    }

    private void handleReload(Player player) {
        try {
            messageManager.reload();
            messageManager.send(player, "message.reload.success");
        } catch (Exception e) {
            messageManager.send(player, "message.reload.error", "error", e.getMessage());
        }
    }
}
