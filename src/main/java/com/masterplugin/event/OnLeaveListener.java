package com.masterplugin.event;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.Gson;
import com.masterplugin.Main;
import com.masterplugin.client.HttpClient;
import com.masterplugin.client.Observer;
import com.masterplugin.model.PlayerModel;
import com.masterplugin.util.ChangeColor;
import com.masterplugin.util.CustomDate;

public class OnLeaveListener implements Listener {

    public OnLeaveListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Set<Thread> threads = ChangeColor.getAllStackTraces().keySet();
        for (Thread t : threads) {
            String name = t.getName();
            if (name.equalsIgnoreCase(event.getPlayer().getName())) {
                ChangeColor changeColor = (ChangeColor) t;
                changeColor.setStop(true);
            }
        }
        getPlayer(event.getPlayer());
    }

    private void getPlayer(final Player bPlayer) {
        HttpClient.get("player/getByUuid/" + bPlayer.getUniqueId().toString(), new Observer<String>() {
            @Override
            public void onSuccess(String data) {
                System.out.println(data + "data");
                PlayerModel player = new Gson().fromJson(data, PlayerModel.class);
				player.setLastLogin(CustomDate.getCurrentDate().toString());
                player.setTimePlayed(bPlayer.getStatistic(Statistic.PLAY_ONE_TICK) / 3600);
                updatePlayer(player);
            }

            @Override
            public void onFailure(Throwable throwable) {
            	Bukkit.getConsoleSender().sendMessage("GetPlayer " + throwable.getMessage());
            }
        });
    }
    
    public static void main(String[] args) {
		
	}
    
    private void updatePlayer(final PlayerModel player) {
        HttpClient.update("player/" + player.getId(), player, new Observer<String>() {
            @Override
            public void onSuccess(String data) {
            	Bukkit.getConsoleSender().sendMessage("UpdatePlayer: " + data);
            }

            @Override
            public void onFailure(Throwable throwable) {
            	Bukkit.getConsoleSender().sendMessage("UpdatePlayer: " + throwable.getMessage());
            }
        });
    }
}

