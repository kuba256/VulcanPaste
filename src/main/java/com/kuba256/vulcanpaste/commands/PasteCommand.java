package com.kuba256.vulcanpaste.commands;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.kuba256.vulcanpaste.VulcanPaste;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class PasteCommand implements CommandExecutor {

    private final VulcanPaste plugin;

    private final Gson gson;

    public PasteCommand(VulcanPaste plugin) {
        this.plugin = plugin;

        this.gson = new Gson();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!sender.hasPermission("vulcanpaste.paste")) {
                sender.sendMessage(plugin.getMessage("no-permission"));
                return;
            }

            if (args.length != 1) {
                sender.sendMessage(plugin.getMessage("usage"));
                return;
            }

            String player = args[0];
            String violations = fetchPlayerViolations(player);

            if (violations.equals("")) {
                sender.sendMessage(plugin.getMessage("no-violations").replace("%player%", player));
                return;
            }

            JSONObject jsonObject;
            HttpURLConnection connection = getConnection(player);

            try {
                jsonObject = gson.fromJson(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8), JSONObject.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String responseKey = jsonObject.get(plugin.getValue("http-request", "response-key")).toString();
            String url = plugin.getValue("urls", "paste").replace("%key%", responseKey);

            String message = plugin.getMessage("paste").replace("%player%", player).replace("%url%", url);

            sender.sendMessage(message);

            connection.disconnect();
        });

        return true;
    }

    private String fetchPlayerViolations(String player) {
        StringBuilder sb = new StringBuilder();
        File violationsFile = new File(plugin.getServer().getPluginManager().getPlugin("Vulcan").getDataFolder(), "violations.txt");
        byte[] data;
        String violations;

        try {
            data = Files.readAllBytes(Paths.get(violationsFile.getPath()));
            violations = new String(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scanner scanner = new Scanner(violations);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] words = line.split(" ");

            if (words[2].equalsIgnoreCase(player)) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private HttpURLConnection getConnection(String player) {
        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection) getUrl().openConnection();
            byte[] requestData = fetchPlayerViolations(player).getBytes(StandardCharsets.UTF_8);
            int requestDataLength = requestData.length;
            connection.setDoOutput(true);
            connection.setRequestMethod(plugin.getValue("http-request", "method"));
            connection.setRequestProperty("charset", "UTF-8");
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Length", String.valueOf(requestDataLength));
            connection.addRequestProperty("User-Agent", plugin.getValue("http-request", "user-agent"));

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(requestData);

            return connection;
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    private URL getUrl() {
        try {
            return new URL(plugin.getValue("urls", "endpoint"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}