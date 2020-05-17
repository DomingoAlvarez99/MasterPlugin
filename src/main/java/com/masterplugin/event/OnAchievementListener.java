package com.masterplugin.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import com.google.gson.Gson;
import com.masterplugin.Main;
import com.masterplugin.client.HttpClient;
import com.masterplugin.client.Observer;
import com.masterplugin.model.AchievementModel;
import com.masterplugin.model.PlayerModel;
import com.masterplugin.util.CustomDate;

public class OnAchievementListener implements Listener {

    public OnAchievementListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
	@EventHandler
	public void onPlayerArchievementAward(PlayerAchievementAwardedEvent e) {
		if (e.getPlayer() instanceof Player) {
			Player bPlayer = e.getPlayer();
			PlayerModel player = new PlayerModel(getPlayerByUuid(bPlayer.getUniqueId().toString()).getId());
			AchievementModel achievement = new AchievementModel(e.getAchievement().toString(), CustomDate.getCurrentDate(), player);
			createAchievement(achievement);
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
	
	private void createAchievement(AchievementModel achievement) {
		HttpClient.post("achievement", achievement, new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				Bukkit.getConsoleSender().sendMessage("onAchievementAwatd: " + data);
				final AchievementModel achievement = new Gson().fromJson(data, AchievementModel.class);
				Bukkit.getConsoleSender().sendMessage(achievement.toString());
			}

			@Override
			public void onFailure(Throwable throwable) {
				Bukkit.getConsoleSender().sendMessage(throwable.getMessage());
			}
		});
	}
	
}
