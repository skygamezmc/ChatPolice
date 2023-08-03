package me.skygamez.chatpolice.Events;

import me.skygamez.chatpolice.ChatPolice;
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

        if (chatPolice.ChatLocked && !e.getPlayer().hasPermission(chatPolice.config.getString("permissions.bypass-lockdown"))) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(chatPolice.placeholders.setPlaceholders(e.getPlayer(), chatPolice.config.getString("messages.chat-denied-locked").replace('&', '§')));
            messageBlocked = true;
            e.setCancelled(true);
        }

        if (cooldown.contains(e.getPlayer())) {
            e.getPlayer().sendMessage(chatPolice.placeholders.setPlaceholders(e.getPlayer(), chatPolice.config.getString("messages.cooldown").replace('&', '§')));
            messageBlocked = true;
            e.setCancelled(true);
        }

        if (!e.getPlayer().hasPermission(chatPolice.config.getString("permissions.bypass-spam"))) {
            if (LastMessage.containsKey(e.getPlayer())) {
                if (LastMessage.get(e.getPlayer()).equals(e.getMessage())) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(chatPolice.placeholders.setPlaceholders(e.getPlayer(), chatPolice.config.getString("messages.spam").replace('&', '§')));
                    messageBlocked = true;
                }
                LastMessage.remove(e.getPlayer());
            }
            LastMessage.put(e.getPlayer(), e.getMessage());
        }

        String filteredMessage = e.getMessage();
        if (!e.getPlayer().hasPermission(chatPolice.config.getString("permissions.bypass-filter")) && !messageBlocked) {
            if (chatPolice.config.getBoolean("enable-filter")) {

                filteredMessage = e.getMessage();
                boolean messageFiltered = false;

                for (String filterCheck : chatPolice.filteredWords) {
                    String escapedFilterCheck = Pattern.quote(filterCheck);

                    Pattern wordPattern = Pattern.compile("(?i)" + escapedFilterCheck.replaceAll("\\s+", "\\\\s*").replaceAll("-", "\\\\-*"));

                    Matcher matcher = wordPattern.matcher(e.getMessage());
                    if (matcher.find()) {
                        messageFiltered = true;

                        filteredMessage = matcher.replaceAll(getCensorString(filterCheck));
                    }
                }
                if (chatPolice.config.getBoolean("notify-staff") && messageFiltered) {
                    Bukkit.broadcast(ChatColor.GOLD + "User: " + e.getPlayer().getName() + " Sent a filtered message saying: " + e.getMessage(), chatPolice.config.getString("permissions.staff-notification"));
                }
                if (chatPolice.config.getBoolean("block-entire-message") && messageFiltered)  {
                    messageBlocked = true;
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(chatPolice.placeholders.setPlaceholders(e.getPlayer(), chatPolice.config.getString("messages.message-blocked").replace('&', '§')));
                } else {
                    e.setMessage(filteredMessage);
                }

            }
        }


        if (chatPolice.config.getBoolean("enable-discord-chatlog") && !messageBlocked) {
            chatPolice.webhookPresets.NewChat(e.getPlayer(), filteredMessage);
        }


        if (chatPolice.config.getBoolean("enable-cooldown") && !e.getPlayer().hasPermission(chatPolice.config.getString("permissions.bypass-cooldown")) && !messageBlocked) {
            cooldown.add(e.getPlayer());
            Bukkit.getScheduler().runTaskLater(chatPolice, () -> {
                cooldown.remove(e.getPlayer());
            }, chatPolice.config.getInt("cooldown-time") * 20);
        }





    }
    private String getCensorString(String word) {
        // Generate the censor string based on the length of the word
        StringBuilder censor = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            censor.append(chatPolice.config.getString("filter-character"));
        }
        return censor.toString();
    }
}
