package com.bobbyl140.bungeecordlyricchat;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

public final class BungeeCordLyricChat extends Plugin {

    private List<String> messages;
    private String chatformat;

    @Override
    public void onEnable() {
        // Ensure config.yml exists, create it otherwise
        try {
            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                Files.copy(getResourceAsStream("config.yml"), configFile.toPath());
            }
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            chatformat = config.getString("chat-format");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Ensure lyrics.yml exists, create it otherwise
        try {
            File configFile = new File(getDataFolder(), "lyrics.yml");
            if (!configFile.exists()) {
                Files.copy(getResourceAsStream("lyrics.yml"), configFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        getProxy().getPluginManager().registerCommand(this, new Lyrics());
        this.getLogger().log(Level.INFO, "Plugin ready (configs loaded)!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private class Lyrics extends Command {

        private String lyricset;
        Lyrics() {
            super("lyrics");
        }
        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("lyrics.reload")) {
                    try {
                        File configFile = new File(getDataFolder(), "config.yml");
                        Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
                        chatformat = config.getString("chat-format");
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&2BLC&7]&r Config file reloaded!"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&2BLC&7]&4 An error occurred! Check your config syntax."));
                    }
                }
                else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are lacking the permission node 'lyrics.reload'!"));
                }
            }
            else if (sender.hasPermission("lyrics.use")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&2BLC&7]&r Executing..."));
                if (args.length == 0) {
                    lyricset = "default";
                } else {
                    lyricset = args[0].toLowerCase();
                }

                messages = loadConfigMessages(lyricset);
                for (String message : messages) {
                    for (ProxiedPlayer playerexe : ProxyServer.getInstance().getPlayers()) {
                        BaseComponent[] component = new ComponentBuilder(formatChatMessage(playerexe, message, chatformat)).create();
                        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                            player.sendMessage(component);
                        }
                    }
                    this.sleep();
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are lacking the permission node 'lyrics.use'!"));
            }

        }

        public Iterable<String> onTabComplete(CommandSender sender, String[] args) throws IOException {
            if (args.length == 1) {
                File configFile = new File(getDataFolder(), "lyrics.yml");
                Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
                Collection<String> keys = config.getKeys();
                List<String> keyList = new ArrayList<>(keys);
                keyList.add("reload");
                return keyList;
            }
            return Collections.emptyList();
        }

        private void sleep () {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String formatChatMessage(ProxiedPlayer player, String message, String format) {
        format = format.replaceAll("%PLAYER%", player.toString());
        format = format.replaceAll("%MESSAGE%", ChatColor.translateAlternateColorCodes('&', message));
        return format;
    }

    private List<String> loadConfigMessages(String key) {
        try {
            File configFile = new File(getDataFolder(), "lyrics.yml");
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            return config.getStringList(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}