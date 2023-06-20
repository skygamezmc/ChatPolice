package me.skygamez.chatpolice.Utils.Webhook;

import me.skygamez.chatpolice.ChatPolice;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebhookPresets {

    ChatPolice chatPolice;

    public WebhookPresets(ChatPolice chatPolice) {
        this.chatPolice = chatPolice;
    }

    public void NewChat(Player player, String message) {
        DiscordWebhook webhook = new DiscordWebhook(chatPolice.config.getString("chat-webhook-link"));
        webhook.setUsername(chatPolice.config.getString("chat-webhook-name"));
        webhook.setAvatarUrl(chatPolice.config.getString("chat-webhook-avatar"));
        webhook.setContent(player.getName() + ": " + message);
        try {
            webhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}
