package me.skygamez.chatpolice.Utils.updatechecker;

import me.skygamez.chatpolice.ChatPolice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
    private final URL url;
    private final ChatPolice localPlugin;

    public UpdateChecker(ChatPolice plugin) throws MalformedURLException {
        url = new URL("https://api.spigotmc.org/legacy/update.php?resource=110614");
        localPlugin = plugin;
    }

    public boolean updateRequired() throws IOException {
        URLConnection con = url.openConnection();
        BufferedReader apiReader = (new BufferedReader(new InputStreamReader(con.getInputStream())));
        String newVersion = apiReader.readLine();
        apiReader.close();
        return !localPlugin.getDescription().getVersion().equals(newVersion);
    }
}
