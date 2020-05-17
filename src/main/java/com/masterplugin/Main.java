package com.masterplugin;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.masterplugin.client.HttpClient;
import com.masterplugin.client.Observer;
import com.masterplugin.event.OnAchievementListener;
import com.masterplugin.event.OnCommandListener;
import com.masterplugin.event.OnDeathListener;
import com.masterplugin.event.OnJoinListener;
import com.masterplugin.event.OnLeaveListener;
import com.masterplugin.event.OnPlayerInteractListener;
import com.masterplugin.model.ItemModel;
import com.masterplugin.model.PlayerModel;
import com.masterplugin.util.ChangeColor;
import com.masterplugin.util.Color;
import com.masterplugin.util.ColorsHandler;

public class Main extends JavaPlugin {
    private static final String ENABLED_MESSAGE = "------------------MasterPlugin enabled------------------";
    private static final String DISABLED_MESSAGE = "------------------MasterPluginDisabled disabled------------------";
    private static final Character KEY_ALT_COLOR = '&';
    private static final long COLOR_DELAY = 300;

    @Override
    public void onEnable() {
        super.onEnable();
        for (Player bPlayer : Bukkit.getOnlinePlayers()) {
            initPlayerResponse(bPlayer);
        }
        Bukkit.getConsoleSender().sendMessage(ENABLED_MESSAGE);
        new OnJoinListener(this);
        new OnDeathListener(this);
        new OnAchievementListener(this);
        new OnPlayerInteractListener(this);
        new OnLeaveListener(this);
    }
    
    public static void main(String[] args) {
		ItemModel.addItems();
	}

    private void initPlayerResponse(final Player bPlayer) {
        HttpClient.get("player/getByUuid/" + bPlayer.getUniqueId().toString(), new Observer<String>() {
            @Override
            public void onSuccess(String data) {
                System.out.println(data + "data");
                final PlayerModel player = new Gson().fromJson(data, PlayerModel.class);
                if (player.getId() > 0 && player.getNameColor().equalsIgnoreCase(Color.RAINBOW) || player.getPrefixColor().equalsIgnoreCase(Color.RAINBOW)) {
                    if (player.getNameColor().equalsIgnoreCase(Color.RAINBOW) && player.getPrefixColor().equalsIgnoreCase(Color.RAINBOW)) {
                        ChangeColor changeColor = new ChangeColor(new ColorsHandler() {
                            @Override
                            public void changeColor() {
                                bPlayer.setPlayerListName(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, player.writeColor(player.getPrefix() + player.getName()).replace("]", "] ")));
                            }
                        }, COLOR_DELAY);
                        changeColor.setName(bPlayer.getName());
                        changeColor.start();
                    }
                    if (player.getNameColor().equalsIgnoreCase(Color.RAINBOW) && !player.getPrefixColor().equalsIgnoreCase(Color.RAINBOW)) {
                        ChangeColor changeColor = new ChangeColor(new ColorsHandler() {
                            @Override
                            public void changeColor() {
                                bPlayer.setPlayerListName(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, player.getPrefixColor() + player.getPrefix() + " " + player.writeColor(player.getName())));
                            }
                        }, COLOR_DELAY);
                        changeColor.setName(bPlayer.getName());
                        changeColor.start();
                    }
                    if (!player.getNameColor().equalsIgnoreCase(Color.RAINBOW) && player.getPrefixColor().equalsIgnoreCase(Color.RAINBOW)) {
                        ChangeColor changeColor = new ChangeColor(new ColorsHandler() {
                            @Override
                            public void changeColor() {
                                bPlayer.setPlayerListName(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, player.writeColor(player.getPrefix()) + " " + player.getNameColor() + player.getName()));
                            }
                        }, COLOR_DELAY);
                        changeColor.setName(bPlayer.getName());
                        changeColor.start();
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }


    @Override
    public void onDisable() {
        super.onDisable();
        Bukkit.getConsoleSender().sendMessage(DISABLED_MESSAGE);
        for (Player p : Bukkit.getOnlinePlayers()) {
            Set<Thread> threads = Thread.getAllStackTraces().keySet();
            for (Thread t : threads) {
                String name = t.getName();
                if (name.equalsIgnoreCase(p.getPlayer().getName())) {
                    ChangeColor changeColor = (ChangeColor) t;
                    changeColor.setStop(true);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        new OnCommandListener(this, sender, command, args);
        return super.onCommand(sender, command, label, args);
    }

}
