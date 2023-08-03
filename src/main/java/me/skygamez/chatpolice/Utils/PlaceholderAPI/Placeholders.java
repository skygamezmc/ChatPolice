package me.skygamez.chatpolice.Utils.PlaceholderAPI;

import me.clip.placeholderapi.PlaceholderAPI;
import me.skygamez.chatpolice.ChatPolice;
import org.bukkit.entity.Player;

public class Placeholders {

    ChatPolice chatPolice;

    public Placeholders(ChatPolice chatPolice) {
        this.chatPolice = chatPolice;
    }

    public String setPlaceholders(Player player, String text) {
        if (chatPolice.hasPlaceholderAPI) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }
}
