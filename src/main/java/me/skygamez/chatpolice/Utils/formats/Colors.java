package me.skygamez.chatpolice.Utils.formats;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colors {
    public static final Pattern hexPattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String format(String string) {
        Matcher match = hexPattern.matcher(string);
        while (match.find()) {
            String color = string.substring(match.start(), match.end());
            string = string.replace(color, ChatColor.of(color) + "");
            match = hexPattern.matcher(string);
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
