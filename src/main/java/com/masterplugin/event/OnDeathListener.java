package com.masterplugin.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.google.gson.Gson;
import com.masterplugin.Main;
import com.masterplugin.client.HttpClient;
import com.masterplugin.client.Observer;
import com.masterplugin.model.DeathModel;
import com.masterplugin.model.PlayerModel;
import com.masterplugin.util.CustomDate;

public class OnDeathListener implements Listener {

    public OnDeathListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player && event.getEntity().getKiller() != null) {
            Player player = event.getEntity();
            Player killer = player.getKiller();
            PlayerModel playerModel = new PlayerModel(getPlayerByUuid(player.getUniqueId().toString()).getId());
            PlayerModel killerModel = new PlayerModel(getPlayerByUuid(killer.getUniqueId().toString()).getId());
            DeathModel death = new DeathModel(CustomDate.getCurrentDate(), event.getDeathMessage(), killerModel, playerModel);
            createDeath(death);
        }
	}
	
	private PlayerModel getPlayerByUuid(String uuid) {
		final PlayerModel[] players = { null };
		HttpClient.get("player/getByUuid/" + uuid, new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				Bukkit.getConsoleSender().sendMessage("onDeathListener" + data);
				final PlayerModel player = new Gson().fromJson(data, PlayerModel.class);
				players[0] = player;
			}

			@Override
			public void onFailure(Throwable throwable) {
				Bukkit.getConsoleSender().sendMessage(throwable.getMessage());
			}
		});
		return players[0];
	}
	
	private void createDeath(DeathModel death) {
		HttpClient.post("death", death, new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				Bukkit.getConsoleSender().sendMessage("onDeathListener" + data);
				final DeathModel death = new Gson().fromJson(data, DeathModel.class);
				Bukkit.getConsoleSender().sendMessage(death.toString());
			}

			@Override
			public void onFailure(Throwable throwable) {
				Bukkit.getConsoleSender().sendMessage(throwable.getMessage());
			}
		});
	}
    
}
