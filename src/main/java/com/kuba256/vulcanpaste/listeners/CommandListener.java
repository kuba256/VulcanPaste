package com.kuba256.vulcanpaste.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/logs")) return;

        event.setCancelled(true);

        String[] args = event.getMessage().split(" ");
        if (args.length == 2) {
            Bukkit.dispatchCommand(event.getPlayer(), "plogs " + args[1]);
        } else {
            Bukkit.dispatchCommand(event.getPlayer(), "plogs");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConsoleCommand(ServerCommandEvent event) {
        if (!event.getCommand().startsWith("logs")) return;

        event.setCancelled(true);

        String[] args = event.getCommand().split(" ");

        if (args.length == 2) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plogs " + args[1]);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plogs");
        }
    }
}
