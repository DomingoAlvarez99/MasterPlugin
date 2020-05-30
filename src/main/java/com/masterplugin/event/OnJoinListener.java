package com.masterplugin.event;

import com.google.gson.Gson;
import com.masterplugin.Main;
import com.masterplugin.client.HttpClient;
import com.masterplugin.client.Observer;
import com.masterplugin.model.PlayerModel;
import com.masterplugin.model.RankModel;
import com.masterplugin.util.ChangeColor;
import com.masterplugin.util.Color;
import com.masterplugin.util.ColorsHandler;
import com.masterplugin.util.CustomDate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinListener implements Listener {
	private static final Character KEY_ALT_COLOR = '&';
	private static final String DEFAULT_PREFIX = "[Member]";
	private static final String DEFAULT_PREFIX_COLOR = Color.MAGENTA;
	private static final String DEFAULT_NAME_COLOR = Color.LIGHT_TURQUOISE;
	private static final long COLOR_DELAY = 300;
	private static final String DEFAULT_RANK = "member";
	private static final String WELCOME_MESSAGE = "&3&lWelcome &6&lBack &e&lTo &c&lThe &a&lMain &d&lServer &f&l";

	public OnJoinListener(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
//        long l = event.getPlayer().getStatistic(Statistic.PLAY_ONE_TICK) / 3600;
//        long l2 = l / 20 / 60 / 60;
		getPlayer(event.getPlayer(), event);
	}

	private void createPlayer(final Player bPlayer, RankModel rank, final PlayerJoinEvent event) {
		final PlayerModel player = new PlayerModel(0l, rank, bPlayer.getName(), bPlayer.getUniqueId().toString(),
				DEFAULT_PREFIX, DEFAULT_PREFIX_COLOR, DEFAULT_NAME_COLOR, "", "",
				bPlayer.getAddress().getAddress().getHostAddress(), CustomDate.getCurrentDate(), null, true);
		HttpClient.post("player", player, new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				Bukkit.getConsoleSender().sendMessage("Create player " + data);
				setPlayerListName(player, bPlayer, event);
			}

			@Override
			public void onFailure(Throwable throwable) {
				Bukkit.getConsoleSender().sendMessage(throwable.getMessage());
			}
		});
	}

	private void getPlayer(final Player bPlayer, final PlayerJoinEvent event) {
		HttpClient.get("player/getByUuid/" + bPlayer.getUniqueId().toString(), new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				Bukkit.getConsoleSender().sendMessage("onPlayerJoin" + data);
				final PlayerModel player = new Gson().fromJson(data, PlayerModel.class);
				player.setOnline(true);
				setPlayerListName(player, bPlayer, event);
				updatePlayer(player);
			}

			@Override
			public void onFailure(Throwable throwable) {
				Bukkit.getConsoleSender().sendMessage(throwable.getMessage());
				getDefaultRank(bPlayer, event);
			}
		});
	}

	private void updatePlayer(final PlayerModel player) {
		HttpClient.update("player/" + player.getId(), player, new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				Bukkit.getConsoleSender().sendMessage("onPlayerJoin" + data);
			}

			@Override
			public void onFailure(Throwable throwable) {
				Bukkit.getConsoleSender().sendMessage(throwable.getMessage());
			}
		});
	}

	private void getDefaultRank(final Player bPlayer, final PlayerJoinEvent event) {
		HttpClient.get("rank/getByName/" + DEFAULT_RANK, new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				Bukkit.getConsoleSender().sendMessage(data);
				final RankModel rank = new Gson().fromJson(data, RankModel.class);
				Bukkit.getConsoleSender().sendMessage(data);
				createPlayer(bPlayer, rank, event);
			}

			@Override
			public void onFailure(Throwable throwable) {
				Bukkit.getConsoleSender().sendMessage(throwable.getMessage());
			}
		});
	}

	private void setPlayerListName(final PlayerModel player, final Player bPlayer, PlayerJoinEvent event) {
		if (player.getNameColor().equalsIgnoreCase(Color.RAINBOW)
				&& player.getPrefixColor().equalsIgnoreCase(Color.RAINBOW)) {
			ChangeColor changeColor = new ChangeColor(new ColorsHandler() {
				@Override
				public void changeColor() {
					bPlayer.setPlayerListName(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
							player.writeColor(player.getPrefix() + player.getName()).replace("]", "] ")));
				}
			}, COLOR_DELAY);
			changeColor.setName(bPlayer.getName());
			changeColor.start();
		}
		if (player.getNameColor().equalsIgnoreCase(Color.RAINBOW)
				&& !player.getPrefixColor().equalsIgnoreCase(Color.RAINBOW)) {
			ChangeColor changeColor = new ChangeColor(new ColorsHandler() {
				@Override
				public void changeColor() {
					bPlayer.setPlayerListName(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
							player.getPrefixColor() + player.getPrefix() + " " + player.writeColor(player.getName())));
				}
			}, COLOR_DELAY);
			changeColor.setName(bPlayer.getName());
			changeColor.start();
		}
		if (!player.getNameColor().equalsIgnoreCase(Color.RAINBOW)
				&& player.getPrefixColor().equalsIgnoreCase(Color.RAINBOW)) {
			ChangeColor changeColor = new ChangeColor(new ColorsHandler() {
				@Override
				public void changeColor() {
					bPlayer.setPlayerListName(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
							player.writeColor(player.getPrefix()) + " " + player.getNameColor() + player.getName()));
				}
			}, COLOR_DELAY);
			changeColor.setName(bPlayer.getName());
			changeColor.start();
		}
		String name = player.getPrefixFormat() + player.getNameColor() + player.getName();
		String prefix = player.getPrefixFormat() + player.getPrefixColor() + player.getPrefix();
		bPlayer.setPlayerListName(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, prefix + " " + name));
		setWelcomeMessage(prefix + " " + name, event);
	}

	public void setWelcomeMessage(String namePrefix, PlayerJoinEvent event) {
		int separators = namePrefix.length() / 5;
		String separator = "-";
		String nameSeparator = "";
		for (int i = 0; i < namePrefix.length(); i++) {
			nameSeparator += separator;
		}
		for (int i = 0; i < separators; i++) {
			nameSeparator += separator;
		}
		event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&4---------------------------" + nameSeparator
				+ "\n" + WELCOME_MESSAGE + namePrefix + "\n" + "&4---------------------------" + nameSeparator));
	}

}