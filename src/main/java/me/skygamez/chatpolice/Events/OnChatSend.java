package me.skygamez.chatpolice.Events;

import me.skygamez.chatpolice.ChatPolice;
import me.skygamez.chatpolice.Utils.formats.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnChatSend implements Listener {

    ChatPolice chatPolice;

    public List<Player> cooldown = new ArrayList<>();
    public HashMap<Player, String> LastMessage = new HashMap<>();

    public OnChatSend(ChatPolice chatPolice) {
        this.chatPolice = chatPolice;
    }
    Pattern whitespacePattern = Pattern.compile("\\s+");

    @EventHandler
    public void OnAsyncPlayerChat(AsyncPlayerChatEvent e) {

        boolean messageBlocked = false;
        String originalMessage = e.getMessage();
        String filteredMessage = e.getMessage();

        if (chatPolice.isChatLocked() && !e.getPlayer().hasPermission(chatPolice.getConfiguration().getString("permissions.bypass-lockdown"))) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(chatPolice.getPlaceholders().setPlaceholders(e.getPlayer(), Colors.format(chatPolice.getConfiguration().getString("messages.chat-denied-locked"))));
            messageBlocked = true;
            e.setCancelled(true);
        }

        if (cooldown.contains(e.getPlayer())) {
            e.getPlayer().sendMessage(chatPolice.getPlaceholders().setPlaceholders(e.getPlayer(), Colors.format(chatPolice.getConfiguration().getString("messages.cooldown"))));
            messageBlocked = true;
            e.setCancelled(true);
        }

        if (!e.getPlayer().hasPermission(chatPolice.getConfiguration().getString("permissions.bypass-spam"))) {
            if (LastMessage.containsKey(e.getPlayer())) {
                if (LastMessage.get(e.getPlayer()).equals(e.getMessage())) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(chatPolice.getPlaceholders().setPlaceholders(e.getPlayer(), Colors.format(chatPolice.getConfiguration().getString("messages.spam"))));
                    messageBlocked = true;
                }
                LastMessage.remove(e.getPlayer());
            }
            LastMessage.put(e.getPlayer(), e.getMessage());
        }
        if (!e.getPlayer().hasPermission(chatPolice.getConfiguration().getString("permissions.bypass-filter")) && !messageBlocked) {
            if (chatPolice.getConfiguration().getBoolean("enable-filter")) {

                boolean messageFiltered = false;

                for (String filterCheck : chatPolice.getFilteredWords()) {
                    String escapedFilterCheck = Pattern.quote(filterCheck);

                    Pattern wordPattern = Pattern.compile("(?i)" + escapedFilterCheck.replaceAll("\\s+", "\\\\s*").replaceAll("-", "\\\\-*"));

                    Matcher matcher = wordPattern.matcher(e.getMessage());
                    if (matcher.find()) {
                        messageFiltered = true;

                        filteredMessage = matcher.replaceAll(getCensorString(filterCheck));
                    }
                }
                if (chatPolice.getConfiguration().getBoolean("notify-staff") && messageFiltered) {
                    Bukkit.broadcast(ChatColor.GOLD + "User: " + e.getPlayer().getName() + " Sent a filtered message saying: " + e.getMessage(), chatPolice.getConfiguration().getString("permissions.staff-notification"));
                }
                if (chatPolice.getConfiguration().getBoolean("block-entire-message") && messageFiltered)  {
                    messageBlocked = true;
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(chatPolice.getPlaceholders().setPlaceholders(e.getPlayer(), Colors.format(chatPolice.getConfiguration().getString("messages.message-blocked"))));
                } else {
                    e.setMessage(filteredMessage);
                }

            }
        }


        if (chatPolice.getConfiguration().getBoolean("enable-discord-chatlog") && !messageBlocked) {
            if (chatPolice.getConfiguration().getBoolean("send-censored-message")) {
                chatPolice.getWebhookPresets().NewChat(e.getPlayer(), filteredMessage);
            } else {
                chatPolice.getWebhookPresets().NewChat(e.getPlayer(), originalMessage);
            }
        }


        if (chatPolice.getConfiguration().getBoolean("enable-cooldown") && !e.getPlayer().hasPermission(chatPolice.getConfiguration().getString("permissions.bypass-cooldown")) && !messageBlocked) {
            cooldown.add(e.getPlayer());
            Bukkit.getScheduler().runTaskLater(chatPolice, () -> {
                cooldown.remove(e.getPlayer());
            }, chatPolice.getConfiguration().getInt("cooldown-time") * 20);
        }





    }
    private String getCensorString(String word) {
        // Generate the censor string based on the length of the word
        StringBuilder censor = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            censor.append(chatPolice.getConfiguration().getString("filter-character"));
        }
        return censor.toString();
    }
}
