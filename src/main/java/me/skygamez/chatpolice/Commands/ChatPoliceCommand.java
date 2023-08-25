package me.skygamez.chatpolice.Commands;

import me.skygamez.chatpolice.ChatPolice;
import me.skygamez.chatpolice.Utils.formats.Colors;
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
            sender.sendMessage(chatPolice.getPlaceholders().setPlaceholders(sender.getServer().getPlayer(sender.getName()), Colors.format(chatPolice.getConfiguration().getString("messages.reloaded"))));
            return true;
        }
        return false;
    }
}
