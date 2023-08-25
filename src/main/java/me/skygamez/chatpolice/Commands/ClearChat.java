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
        if (chatPolice.getConfiguration().getBoolean("staff-bypass-clearchat")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission(chatPolice.getConfiguration().getString("permissions.staff"))) {
                    for (int i = 0; i < 100; i++) {
                        player.sendMessage("");
                    }
                    player.sendMessage(chatPolice.getPlaceholders().setPlaceholders(player, chatPolice.getConfiguration().getString("messages.clearchat").replace('&', '§')));
                }
            }
        }
        return false;
    }
}
