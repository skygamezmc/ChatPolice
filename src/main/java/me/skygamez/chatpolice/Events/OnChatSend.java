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

public class OnChatSend implements Listener {

    ChatPolice chatPolice;

    public List<Player> cooldown = new ArrayList<>();
    public HashMap<Player, String> LastMessage = new HashMap<>();

    public OnChatSend(ChatPolice chatPolice) {
        this.chatPolice = chatPolice;
    }

    @EventHandler
    public void OnAsyncPlayerChat(AsyncPlayerChatEvent e) {
        boolean messageBlocked = false;

        if (chatPolice.ChatLocked && !e.getPlayer().hasPermission(chatPolice.config.getString("permissions.bypass-lockdown"))) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(chatPolice.config.getString("messages.chat-denied-locked").replace('&', '§'));
            return;
        }

        if (cooldown.contains(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(chatPolice.config.getString("messages.cooldown").replace('&', '§'));
            messageBlocked = true;
            return;
        }

        if (!e.getPlayer().hasPermission(chatPolice.config.getString("permissions.bypass-spam"))) {
            if (LastMessage.containsKey(e.getPlayer())) {
                System.out.println(LastMessage.get(e.getPlayer()));
                if (LastMessage.get(e.getPlayer()).equals(e.getMessage())) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(chatPolice.config.getString("messages.spam").replace('&', '§'));
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
                    if (e.getMessage().toLowerCase().contains(filterCheck.toLowerCase())) {
                        messageFiltered = true;
                        String censor = "";
                        for (int i = 0; i < filterCheck.length(); i++) {
                            censor += chatPolice.config.getString("filter-character");
                        }

                        filteredMessage = filteredMessage.replaceAll("(?i)" + filterCheck, censor);
                    }
                }
                if (chatPolice.config.getBoolean("notify-staff") || messageFiltered) {
                    Bukkit.broadcast(ChatColor.GOLD + "User: " + e.getPlayer().getName() + " Sent a filtered message saying: " + e.getMessage(), chatPolice.config.getString("permissions.staff-notification"));
                }
                if (chatPolice.config.getBoolean("block-entire-message") && messageFiltered)  {
                    messageBlocked = true;
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(chatPolice.config.getString("messages.message-blocked").replace('&', '§'));
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
}
