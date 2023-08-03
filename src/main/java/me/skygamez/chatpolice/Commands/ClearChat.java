package me.skygamez.chatpolice.Commands;

import me.skygamez.chatpolice.ChatPolice;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI;

public class ClearChat implements CommandExecutor {

    ChatPolice chatPolice;

    public ClearChat(ChatPolice chatPolice) {
        this.chatPolice = chatPolice;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (chatPolice.config.getBoolean("staff-bypass-clearchat")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission(chatPolice.config.getString("permissions.staff"))) {
                    for (int i = 0; i < 100; i++) {
                        player.sendMessage("");
                    }
                    player.sendMessage(chatPolice.placeholders.setPlaceholders(player, chatPolice.config.getString("messages.clearchat").replace('&', '§')));
                }
            }
        }
        return false;
    }
}
