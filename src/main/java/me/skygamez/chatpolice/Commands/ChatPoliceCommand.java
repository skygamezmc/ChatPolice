package me.skygamez.chatpolice.Commands;

import me.skygamez.chatpolice.ChatPolice;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChatPoliceCommand implements CommandExecutor {

    ChatPolice chatPolice;

    public ChatPoliceCommand(ChatPolice chatPolice) {
        this.chatPolice = chatPolice;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            sender.sendMessage(ChatColor.RED + "Usage: /chatpolice <arguments>");
            return true;
        }

        if (args[0].equals("reload")) {
            chatPolice.LoadConfiguration();
            sender.sendMessage(chatPolice.placeholders.setPlaceholders(sender.getServer().getPlayer(sender.getName()), chatPolice.config.getString("messages.reloaded").replace('&', '§')));
            return true;
        }
        return false;
    }
}
