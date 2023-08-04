package me.skygamez.chatpolice;

import me.skygamez.chatpolice.Commands.ChatLockdown;
import me.skygamez.chatpolice.Commands.ChatPoliceCommand;
import me.skygamez.chatpolice.Commands.ClearChat;
import me.skygamez.chatpolice.Events.OnChatSend;
import me.skygamez.chatpolice.Metrics.Metrics;
import me.skygamez.chatpolice.Utils.PlaceholderAPI.Placeholders;
import me.skygamez.chatpolice.Utils.Webhook.WebhookPresets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ChatPolice extends JavaPlugin {

    public Configuration config;
    public List<String> filteredWords = new ArrayList<>();

    public WebhookPresets webhookPresets;

    public boolean ChatLocked;

    public boolean hasPlaceholderAPI = false;

    public Placeholders placeholders;

    @Override
    public void onEnable() {
        // Plugin startup logic

        //Config Setup
        saveDefaultConfig();
        LoadConfiguration();

        //Event Setup
        getServer().getPluginManager().registerEvents(new OnChatSend(this), this);

        //Command Setup
        getCommand("chatpolice").setExecutor(new ChatPoliceCommand(this));
        getCommand("chatpolice").setPermission(config.getString("permissions.reload"));
        getCommand("chatpolice").setPermissionMessage(config.getString("messages.no-permission"));

        getCommand("clearchat").setExecutor(new ClearChat(this));
        getCommand("clearchat").setPermission(config.getString("permissions.clearchat"));
        getCommand("clearchat").setPermissionMessage(config.getString("messages.no-permission"));

        getCommand("chatlockdown").setExecutor(new ChatLockdown(this));
        getCommand("chatlockdown").setPermission(config.getString("permissions.lockdown"));
        getCommand("chatlockdown").setPermissionMessage(config.getString("messages.no-permission"));

        placeholders = new Placeholders(this);

        webhookPresets = new WebhookPresets(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            hasPlaceholderAPI = true;
        }

        int pluginId = 18801; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);

        getLogger().info("§e----§6----------------------------------§e----");
        getLogger().info("&7 ChatPolice has started successfully!");
        getLogger().info("&7 Version: " + getDescription().getVersion());
        getLogger().info("§e----§6----------------------------------§e----");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void LoadConfiguration() {
        reloadConfig();
        config = getConfig();
        filteredWords.clear();
        filteredWords.addAll(config.getStringList("filtered-words"));
    }
}
