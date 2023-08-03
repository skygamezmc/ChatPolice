package me.skygamez.chatpolice.Commands;

import me.skygamez.chatpolice.ChatPolice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatLockdown implements CommandExecutor {

    ChatPolice chatPolice;

    public ChatLockdown(ChatPolice chatPolice) {
        this.chatPolice = chatPolice;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (chatPolice.ChatLocked) {
            chatPolice.ChatLocked = false;
            sender.sendMessage(chatPolice.placeholders.setPlaceholders(sender.getServer().getPlayer(sender.getName()), chatPolice.config.getString("messages.unlocked-chat").replace('&', '§')));
        } else {
            chatPolice.ChatLocked = true;
            sender.sendMessage(chatPolice.placeholders.setPlaceholders(sender.getServer().getPlayer(sender.getName()), chatPolice.config.getString("messages.locked-chat").replace('&', '§')));
        }
        return false;
    }
}