package me.skygamez.chatpolice.Commands;

import me.skygamez.chatpolice.ChatPolice;
import me.skygamez.chatpolice.Utils.formats.Colors;
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
        if (chatPolice.isChatLocked()) {
            chatPolice.setChatLocked(true);
            sender.sendMessage(chatPolice.getPlaceholders().setPlaceholders(sender.getServer().getPlayer(sender.getName()), Colors.format(chatPolice.getConfiguration().getString("messages.unlocked-chat"))));
        } else {
            chatPolice.setChatLocked(false);
            sender.sendMessage(chatPolice.getPlaceholders().setPlaceholders(sender.getServer().getPlayer(sender.getName()), Colors.format(chatPolice.getConfiguration().getString("messages.locked-chat"))));
        }
        return false;
    }
}