package me.skygamez.chatpolice;

import lombok.Getter;
import lombok.Setter;
import me.skygamez.chatpolice.Commands.ChatLockdown;
import me.skygamez.chatpolice.Commands.ChatPoliceCommand;
import me.skygamez.chatpolice.Commands.ClearChat;
import me.skygamez.chatpolice.Events.OnChatSend;
import me.skygamez.chatpolice.Metrics.Metrics;
import me.skygamez.chatpolice.Utils.PlaceholderAPI.Placeholders;
import me.skygamez.chatpolice.Utils.Webhook.WebhookPresets;
import me.skygamez.chatpolice.Utils.formats.Colors;
import me.skygamez.chatpolice.Utils.updatechecker.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public final class ChatPolice extends JavaPlugin {

    @Getter
    private Configuration configuration;

    @Getter
    private List<String> filteredWords = new ArrayList<>();

    @Getter
    private WebhookPresets webhookPresets;

    @Getter
    @Setter
    private boolean ChatLocked;

    @Getter
    private boolean hasPlaceholderAPI = false;

    @Getter
    private Placeholders placeholders;

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
        getCommand("chatpolice").setPermission(configuration.getString("permissions.reload"));
        getCommand("chatpolice").setPermissionMessage(configuration.getString("messages.no-permission"));

        getCommand("clearchat").setExecutor(new ClearChat(this));
        getCommand("clearchat").setPermission(configuration.getString("permissions.clearchat"));
        getCommand("clearchat").setPermissionMessage(configuration.getString("messages.no-permission"));

        getCommand("chatlockdown").setExecutor(new ChatLockdown(this));
        getCommand("chatlockdown").setPermission(configuration.getString("permissions.lockdown"));
        getCommand("chatlockdown").setPermissionMessage(configuration.getString("messages.no-permission"));

        placeholders = new Placeholders(this);

        webhookPresets = new WebhookPresets(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            hasPlaceholderAPI = true;
        }

        int pluginId = 18801;
        Metrics metrics = new Metrics(this, pluginId);

        try {
            UpdateChecker updateChecker = new UpdateChecker(this);
            if (updateChecker.updateRequired()) {
                getLogger().info(Colors.format("§e----§6----------------------------------§e----"));
                getLogger().info(Colors.format("&7 ChatPolice has started successfully!"));
                getLogger().info(Colors.format("&7 Version: " + getDescription().getVersion()));
                getLogger().info(Colors.format("§e----§6----------------------------------§e----"));
            } else {
                getLogger().info(Colors.format("§e----§6----------------------------------§e----"));
                getLogger().info(Colors.format("&7 ChatPolice has started successfully!"));
                getLogger().info(Colors.format("&a          Update available!"));
                getLogger().info(Colors.format("§e----§6----------------------------------§e----"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void LoadConfiguration() {
        reloadConfig();
        configuration = getConfig();
        filteredWords.clear();
        filteredWords.addAll(configuration.getStringList("filtered-words"));
    }
}
