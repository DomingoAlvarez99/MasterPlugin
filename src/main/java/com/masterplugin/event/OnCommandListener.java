package com.masterplugin.event;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.gson.Gson;
import com.masterplugin.Main;
import com.masterplugin.client.HttpClient;
import com.masterplugin.client.Observer;
import com.masterplugin.model.PermissionModel;
import com.masterplugin.model.PlayerModel;
import com.masterplugin.model.WarpModel;
import com.masterplugin.util.AfterCRUD;
import com.masterplugin.util.AfterChangeColor;
import com.masterplugin.util.ChangeColor;
import com.masterplugin.util.Color;
import com.masterplugin.util.ColorsHandler;

public class OnCommandListener {
	private static final String PLAYER_NOT_FOUND = "Player not found";
	private static final String COMMAND_TOO_FEW = "Too few arguments";
	private static final String COMMAND_TOO_MANY = "Too many arguments";
	private static final String COMMAND_LIST_MESSAGE = "Below is a list of all the commands";
	private static final String RANK_MEMBER = "member";
	private static final String RANK_OWNER = "owner";
	private CommandSender sender;
	private Command command;
	private Main main;
	private String[] args;
	private static final Character KEY_ALT_COLOR = '&';
	private static final long COLOR_DELAY = 300;
	private static final String COMMAND_ERROR = "Wrong arguments";
	private static final String COMMAND_PERMISSION = "You don't have enough permissions";

	public OnCommandListener(Main main, CommandSender sender, Command command, String[] args) {
		this.main = main;
		this.sender = sender;
		this.command = command;
		this.args = args;
		change();
		list();
		ping();
		chunksLoaded();
		tpRandom();
		create();
		delete();
		warp();
		seen();
	}

	private void updateColor(final Player p, final String color, final String field) {
		getPlayer(p, new AfterCRUD() {
			@Override
			public void onSuccess(String data) {
				final PlayerModel player = new Gson().fromJson(data, PlayerModel.class);
				if (field.equalsIgnoreCase("name")) {
					player.setNameColor(color);
				} else {
					player.setPrefixColor(color);
				}
				Set<Thread> threads = ChangeColor.getAllStackTraces().keySet();
				for (Thread t : threads) {
					String name = t.getName();
					if (name.equalsIgnoreCase(p.getName())) {
						ChangeColor changeColor = (ChangeColor) t;
						changeColor.setAfterChangeColor(new AfterChangeColor() {
							@Override
							public void doTheJob() {
								setPlayerListName(player, p);
							}
						});
						changeColor.setStop(true);
					}
				}
				if (player.getId() > 0) {
					updatePlayer(p, player);
					setPlayerListName(player, p);
				}
			}
		});
	}

	private void chunksLoaded() {
		Player p = (Player) sender;
		World w = p.getWorld();
		if (command.getName().equalsIgnoreCase("chunksLoaded")) {
			boolean chunksLoaded = hasPermission(p, "chunksLoaded");
			if (args.length == 0) {
				if (chunksLoaded) {
					p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
							Color.DARK_TURQUOISE + "Chunks loaded: " + Color.BOLD + w.getLoadedChunks().length));
				} else {
					p.sendMessage(
							ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_PERMISSION));
				}
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_MANY));
			}
		}
	}

	private void warp() {
		Player p = (Player) sender;
		if (command.getName().equalsIgnoreCase("warp")) {
			if (args.length == 1) {
				String warpName = args[0];
				checkWarp(p, warpName);
			} else if (args.length == 0) {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_FEW));
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_MANY));
			}
		}
	}

	private void checkWarp(Player bPlayer, String warpName) {
		final PlayerModel player = getPlayerByUuid(bPlayer);
		WarpModel warp = new WarpModel(warpName);
		if (player.getWarps().contains(warp)) {
			for (WarpModel w : player.getWarps()) {
				if (warp.equals(w)) {
					warp = w;
					break;
				}
			}
			long x = warp.getCoordinateX();
			long y = warp.getCoordinateY();
			long z = warp.getCoordinateZ();
			Location location = new Location(bPlayer.getWorld(), x, y, z);
			bPlayer.teleport(location);
			bPlayer.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.LIGHT_GREEN
					+ "You have been teleported to {" + warpName + "} x: " + x + " y: " + y + " z: " + z));
		} else {
			bPlayer.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
					Color.LIGHT_RED + "Name {" + warpName + "} not found, couldn't tp to the warp"));
		}
	}

	private void ping() {
		Player p = (Player) sender;
		if (command.getName().equalsIgnoreCase("ping")) {
			if (args.length <= 1) {
				boolean pingSelf = hasPermission(p, "ping.self");
				boolean pingEveryone = hasPermission(p, "ping.everyone");
				if (pingSelf || pingEveryone) {
					if (args.length == 1) {
						if (pingEveryone) {
							p = getPlayerByName(args[0]);
							if (p != null) {
								displayPingMessage((Player) sender, p, p.getName() + " has");
							} else {
								p = (Player) sender;
								p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
										Color.DARK_RED + PLAYER_NOT_FOUND));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
									Color.DARK_RED + COMMAND_PERMISSION));
						}
					} else {
						displayPingMessage(p, p, "You have");
					}
				} else {
					p.sendMessage(
							ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_PERMISSION));
				}
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_MANY));
			}
		}
	}

	private void seen() {
		Player p = (Player) sender;
		if (command.getName().equalsIgnoreCase("seen")) {
			if (args.length <= 1) {
				boolean seenSelf = hasPermission(p, "seen.self");
				boolean seenEveryone = hasPermission(p, "seen.everyone");
				if (seenSelf || seenEveryone) {
					if (args.length == 1) {
						if (seenEveryone) {
							p = getPlayerByName(args[0]);
							if (p != null) {
								displaySeenMessage((Player) sender, p);
							} else {
								p = (Player) sender;
								p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
										Color.DARK_RED + PLAYER_NOT_FOUND));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
									Color.DARK_RED + COMMAND_PERMISSION));
						}
					} else {
						displaySeenMessage(p, p);
					}
				} else {
					p.sendMessage(
							ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_PERMISSION));
				}
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_MANY));
			}
		}
	}

	private void displaySeenMessage(Player sender, Player seen) {
		PlayerModel player = getPlayerByUuid(seen);
		sender.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
				Color.LIGHT_GREEN + "Last seen: " + player.getLastLogin()));
	}

	private void create() {
		Player p = (Player) sender;
		if (command.getName().equalsIgnoreCase("create")) {
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("warp")) {
					canCreateWarp(p, args[1]);
				} else {
					p.sendMessage(
							ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_ERROR));
				}
			} else if (args.length < 2) {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_FEW));
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_MANY));
			}
		}
	}

	private void delete() {
		Player p = (Player) sender;
		if (command.getName().equalsIgnoreCase("delete")) {
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("warp")) {
					deleteWarp(p, args[1]);
				} else {
					p.sendMessage(
							ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_ERROR));
				}
			} else if (args.length < 2) {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_FEW));
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_MANY));
			}
		}
	}

	private void deleteWarp(final Player bPlayer, final String name) {
		final PlayerModel player = getPlayerByUuid(bPlayer);
		WarpModel warp = new WarpModel(name);
		if (player.getWarps().contains(warp)) {
			for (WarpModel w : player.getWarps()) {
				if (warp.equals(w)) {
					warp = w;
					break;
				}
			}
		} else {
			bPlayer.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
					Color.LIGHT_RED + "Name {" + name + "} not found, couldn't delete the warp"));
		}
		warp.setPlayer(new PlayerModel(player.getId()));
		deleteWarpResponse(bPlayer, warp);
	}

	private void deleteWarpResponse(final Player bPlayer, WarpModel warp) {
		HttpClient.delete("warp/" + warp.getId(), new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				Bukkit.getConsoleSender().sendMessage("Warp deleted: " + data);
				bPlayer.sendMessage(
						ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.LIGHT_GREEN + "Warp deleted"));
			}

			@Override
			public void onFailure(Throwable throwable) {
				Bukkit.getConsoleSender().sendMessage(throwable.getMessage());
			}
		});
	}

	private void canCreateWarp(final Player bPlayer, final String name) {
		final PlayerModel player = getPlayerByUuid(bPlayer);
		switch (player.getRank().getName().toLowerCase()) {
		case RANK_MEMBER:
			if (player.getWarps().isEmpty()) {
				createWarp(bPlayer, player, name);
			} else {
				bPlayer.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
						Color.LIGHT_RED + "You can't create more warps"));
			}
			break;
		case RANK_OWNER:
			if (player.getWarps().size() <= 1) {
				createWarp(bPlayer, player, name);
			} else {
				bPlayer.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
						Color.LIGHT_RED + "You can't create more warps"));
			}
			break;
		}
	}

	private void createWarp(final Player bPlayer, PlayerModel player, String name) {
		long x = bPlayer.getLocation().getBlockX();
		long y = bPlayer.getLocation().getBlockY();
		long z = bPlayer.getLocation().getBlockZ();
		WarpModel warp = new WarpModel(x, y, z, name, player);
		HttpClient.post("warp", warp, new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				Bukkit.getConsoleSender().sendMessage("Create warp: " + data);
				bPlayer.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
						Color.LIGHT_GREEN + "Warp " + args[1] + " created"));
			}

			@Override
			public void onFailure(Throwable throwable) {
				if (throwable.getMessage().contains("HTTP/1.1 409")) {
					bPlayer.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
							Color.LIGHT_RED + "You already have a warp with this name"));
				}
				Bukkit.getConsoleSender().sendMessage(throwable.getMessage());
			}
		});
	}

	private void tpRandom() {
		Player p = (Player) sender;
		if (command.getName().equalsIgnoreCase("tpRandom")) {
			if (args.length <= 1) {
				boolean tpRandomSelf = hasPermission(p, "ping.self");
				boolean tpRandomEveryone = hasPermission(p, "ping.everyone");
				if (tpRandomSelf || tpRandomEveryone) {
					if (args.length == 1) {
						if (tpRandomEveryone) {
							p = getPlayerByName(args[0]);
							if (p != null) {
								tp(p);
							} else {
								p = (Player) sender;
								p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
										Color.DARK_RED + PLAYER_NOT_FOUND));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
									Color.DARK_RED + COMMAND_PERMISSION));
						}
					} else {
						tp(p);
					}
				} else {
					p.sendMessage(
							ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_PERMISSION));
				}
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_MANY));
			}
		}
	}

	private void tp(Player bPlayer) {
		World w = bPlayer.getWorld();
		int pos = (int) Math.round(Math.random() * 1);
		int x = -1;
		int z = -1;
		if (pos == 0) {
			x = (int) Math.round(Math.random() * 10001) - 1;
			z = (int) Math.round(Math.random() * 10001) - 1;
		} else if (pos == 1) {
			x = (int) Math.round(Math.random() * -10001) + 1;
			z = (int) Math.round(Math.random() * -10001) + 1;
		}
		int y = 150;
		Location spawn = new Location(w, x, y, z);
		bPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 250, 10000));
		bPlayer.teleport(spawn);
	}

	private void displayPingMessage(Player sender, Player pinged, String who) {
		int ping = 0;
		try {
			Object entityPlayer = pinged.getClass().getMethod("getHandle").invoke(pinged);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		if (ping >= 0 && ping < 100) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
					Color.LIGHT_GREEN + who + Color.LIGHT_GREEN + Color.BOLD + " " + ping + Color.LIGHT_GREEN + " MS"));
		}
		if (ping >= 100 && ping < 200) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.LIGHT_YELLOW + who
					+ Color.LIGHT_YELLOW + Color.BOLD + " " + ping + Color.LIGHT_YELLOW + " MS"));
		}
		if (ping >= 200 && ping < 300) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
					Color.DARK_YELLOW + who + Color.DARK_YELLOW + Color.BOLD + " " + ping + Color.DARK_YELLOW + " MS"));
		}
		if (ping >= 300 && ping < 400) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
					Color.LIGHT_RED + who + Color.LIGHT_RED + Color.BOLD + " " + ping + Color.LIGHT_RED + " MS"));
		}
		if (ping >= 400) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.BLACK + who + " "
					+ Color.BLACK + Color.BOLD + Color.RANDOM + " " + ping + Color.BLACK + " MS"));
		}
	}

	private void list() {
		Player p = (Player) sender;
		if (command.getName().equalsIgnoreCase("list")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("colors")) {
					listColors(p);
				} else if (args[0].equalsIgnoreCase("commands")) {
					Map<String, Map<String, Object>> commands = main.getDescription().getCommands();
					p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
							Color.LIGHT_GRAY + COMMAND_LIST_MESSAGE));
					List<String> commandsList = new ArrayList<>();
					for (Map.Entry<String, Map<String, Object>> entry : commands.entrySet()) {
						for (Map.Entry<String, Object> entry2 : entry.getValue().entrySet()) {
							String command = "/" + Color.DARK_TURQUOISE + entry.getKey() + " " + Color.WHITE
									+ entry2.getValue().toString();
							p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, command));
							commandsList.add(command);
						}
					}
				} else if (args[0].equalsIgnoreCase("warps")) {
					listWarps(p);
				} else {
					p.sendMessage(
							ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_ERROR));
				}
			}
			if (args.length == 0) {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_FEW));
			}
			if (args.length > 1) {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_MANY));
			}
		}
	}

	private void listWarps(Player bPlayer) {
		final PlayerModel player = getPlayerByUuid(bPlayer);
		if (player.getWarps().isEmpty()) {
			bPlayer.sendMessage(
					ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + "You don't have any warp"));
		} else {
			String warps = "Warps: ";
			for (WarpModel warp : player.getWarps()) {
				warps += warp.getName() + ", ";
			}
			warps = warps.substring(0, warps.length() - 2);
			bPlayer.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.LIGHT_GREEN + warps));
		}
	}

	private void listColors(Player p) {
		String colors = "";
		String[] allColors = Color.getColors();
		for (int i = 0; i < allColors.length; i++) {
			colors += allColors[i] + ", ";
		}
		colors = colors.substring(0, colors.length() - 2);
		p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, colors));
	}

	public void updateColor(Player p) {
		boolean singleColorSelf = hasPermission(p, "change.single_color.self");
		boolean allColor = hasPermission(p, "change.all_color");
		final String color = Color.stringToColor(args[2]);
		if (singleColorSelf || allColor) {
			if (!color.isEmpty()) {
				if (args.length == 4) {
					if (allColor) {
						p = getPlayerByName(args[3]);
						if (p != null) {
							updateColor(p, color, args[0]);
						} else {
							p = (Player) sender;
							p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
									Color.DARK_RED + PLAYER_NOT_FOUND));
						}
					} else {
						p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
								Color.DARK_RED + COMMAND_PERMISSION));
					}
				} else {
					if (!allColor && args[2].equalsIgnoreCase("RAINBOW")) {
						p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
								Color.DARK_RED + COMMAND_PERMISSION));
					} else {
						updateColor(p, color, args[0]);
					}
				}
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_ERROR));
			}
		} else {
			p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_PERMISSION));
		}
	}

	public void change() {
		Player p = (Player) sender;
		if (command.getName().equalsIgnoreCase("change")) {
			if (args.length == 3 || args.length == 4) {
				if (args[0].equalsIgnoreCase("name")
						|| args[0].equalsIgnoreCase("prefix") && args[1].equalsIgnoreCase("color")) {
					updateColor(p);
				} else {
					p.sendMessage(
							ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_ERROR));
				}
			}
			if (args.length < 3) {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_FEW));
			}
			if (args.length > 4) {
				p.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + COMMAND_TOO_MANY));
			}
		}
	}

	public Player getPlayerByName(String name) {
		Player player = null;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(name)) {
				player = p;
			}
		}
		return player;
	}

	private PlayerModel getPlayerByUuid(Player bPlayer) {
		final PlayerModel[] players = { null };
		HttpClient.get("player/getByUuid/" + bPlayer.getUniqueId().toString(), new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				players[0] = new Gson().fromJson(data, PlayerModel.class);
			}

			@Override
			public void onFailure(Throwable throwable) {
				Bukkit.getConsoleSender().sendMessage(throwable.getMessage());
			}
		});
		return players[0];
	}

	public boolean hasPermission(Player bPlayer, final String name) {
		boolean per = false;
		PermissionModel permission = new PermissionModel(name);
		PlayerModel player = getPlayerByUuid(bPlayer);
		if (player.getRank().getPermissions().contains(permission)) {
			per = true;
		}
		return per;
	}

	private void getPlayer(Player bPlayer, final AfterCRUD afterCRUD) {
		HttpClient.get("player/getByUuid/" + bPlayer.getUniqueId().toString(), new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				afterCRUD.onSuccess(data);
			}

			@Override
			public void onFailure(Throwable throwable) {

			}
		});
	}

	private void setPlayerListName(final PlayerModel player, final Player bPlayer) {
		if (player.getNameColor().equalsIgnoreCase(Color.RAINBOW)
				&& player.getPrefixColor().equalsIgnoreCase(Color.RAINBOW)) {
			stopChangeColor(bPlayer);
			ChangeColor changeColor = new ChangeColor(new ColorsHandler() {
				@Override
				public void changeColor() {
					bPlayer.setPlayerListName(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
							player.writeColor(player.getPrefix() + player.getName()).replace("]", "] ")));
				}
			}, COLOR_DELAY);
			changeColor.setName(bPlayer.getName());
			changeColor.start();
		} else if (player.getNameColor().equalsIgnoreCase(Color.RAINBOW)
				&& !player.getPrefixColor().equalsIgnoreCase(Color.RAINBOW)) {
			stopChangeColor(bPlayer);
			ChangeColor changeColor = new ChangeColor(new ColorsHandler() {

				@Override
				public void changeColor() {
					bPlayer.setPlayerListName(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
							player.getPrefixColor() + player.getPrefix() + " " + player.writeColor(player.getName())));
				}
			}, COLOR_DELAY);
			changeColor.setName(bPlayer.getName());
			changeColor.start();
		} else if (!player.getNameColor().equalsIgnoreCase(Color.RAINBOW)
				&& player.getPrefixColor().equalsIgnoreCase(Color.RAINBOW)) {
			stopChangeColor(bPlayer);

			ChangeColor changeColor = new ChangeColor(new ColorsHandler() {
				@Override
				public void changeColor() {
					bPlayer.setPlayerListName(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
							player.writeColor(player.getPrefix()) + " " + player.getNameColor() + player.getName()));
				}
			}, COLOR_DELAY);
			changeColor.setName(bPlayer.getName());
			changeColor.start();
		} else {
			String name = player.getNameColor() + player.getName();
			String prefix = player.getPrefixColor() + player.getPrefix();
			bPlayer.setPlayerListName(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, prefix + " " + name));
		}

	}

	private void stopChangeColor(Player bPlayer) {
		Set<Thread> threads = ChangeColor.getAllStackTraces().keySet();
		for (Thread t : threads) {
			String name = t.getName();
			if (name.equalsIgnoreCase(bPlayer.getName())) {
				ChangeColor changeColor = (ChangeColor) t;
				changeColor.setStop(true);
			}
		}
	}

	private void updatePlayer(final Player bPlayer, final PlayerModel player) {
		HttpClient.update("player/" + player.getId(), player, new Observer<String>() {
			@Override
			public void onSuccess(String data) {
				System.out.println("player update " + data);
				if (player.getId() > 0) {
					bPlayer.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR,
							Color.DARK_GREEN + "The color has been changed correctly"));
				}
			}

			@Override
			public void onFailure(Throwable throwable) {

			}
		});
	}
}
