package com.kuba256.vulcanpaste;

import com.kuba256.vulcanpaste.commands.PasteCommand;
import com.kuba256.vulcanpaste.listeners.CommandListener;
import org.bukkit.plugin.java.JavaPlugin;

public class VulcanPaste extends JavaPlugin {

    @Override
    public void onEnable() {
        registerEvents();
        registerCommands();
        setupConfig();
    }

    private void registerEvents() {
        if (getConfig().getBoolean("override-vulcan-command")) {
            getServer().getPluginManager().registerEvents(new CommandListener(), this);
        }
    }

    private void registerCommands() {
        getCommand("plogs").setExecutor(new PasteCommand(this));
    }

    private void setupConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    public String getMessage(String key) {
        return getConfig().getString("messages." + key).replace('&', '§');
    }

    public String getValue(String section, String key) {
        return getConfig().getString(section + "." + key);
    }
}
