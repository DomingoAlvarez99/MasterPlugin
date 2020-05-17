package com.masterplugin.event;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.masterplugin.Main;
import com.masterplugin.client.HttpClient;
import com.masterplugin.client.Observer;
import com.masterplugin.model.ItemModel;
import com.masterplugin.util.Color;

public class OnPlayerInteractListener implements Listener {
	
    private static final Character KEY_ALT_COLOR = '&';

    public OnPlayerInteractListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.WALL_SIGN) {
                Player bPlayer = event.getPlayer();
                onWallSignInteract(block, bPlayer);
            }
            if (event.getClickedBlock().getType() == Material.CHEST && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Player bPlayer = event.getPlayer();
                onChestInteract(block, bPlayer);
            }
        }
    }

    private void onChestInteract(Block block, Player bPlayer) {
        Block attached = block.getRelative(block.getFace(block));
        if (attached.getType() == org.bukkit.Material.CHEST) {
            Chest chest = (Chest) attached.getLocation().getBlock().getState();
            ItemStack[] elements = chest.getBlockInventory().getContents();
            Map<Boolean, List<ItemStack>> items = getChestItems(elements, bPlayer);
            Map.Entry<Boolean, List<ItemStack>> entry = items.entrySet().iterator().next();
            boolean isCorrect = entry.getKey();
            if (isCorrect) {
                System.out.println(entry.getValue().toString());
                System.out.println(entry.getValue().get(0).getData().getItemType());
                chest.getInventory().removeItem(new ItemStack(entry.getValue().get(0).getType(), 1, entry.getValue().get(0).getDurability()));
            }
        }
    }

    private void onWallSignInteract(Block block, Player bPlayer) {
        Sign sign = (Sign) block.getState().getData();
        Block attached = block.getRelative(sign.getAttachedFace());
        if (attached.getType() == org.bukkit.Material.CHEST) {
            Chest chest = (Chest) attached.getLocation().getBlock().getState();
            ItemStack[] elements = chest.getBlockInventory().getContents();
            Map<Boolean, List<ItemStack>> items = getChestItems(elements, bPlayer);
            Map.Entry<Boolean, List<ItemStack>> entry = items.entrySet().iterator().next();
            boolean isCorrect = entry.getKey();
            if (isCorrect) {
                System.out.println(entry.getValue().toString());
                System.out.println(entry.getValue().get(0).getData().getItemType());
                chest.getInventory().removeItem(new ItemStack(entry.getValue().get(0).getType(), 1, entry.getValue().get(0).getDurability()));
            }
        }
    }
    
    public boolean checkIfContains(List<String> lines, String what) {
        boolean contains = false;
    	for (String line : lines) {
			if (line.startsWith(what)) {
				contains = true;
				break;
			}
		}
    	return contains;
    }

    @EventHandler
    public void onSignEdit(SignChangeEvent sign) {
        Player player = sign.getPlayer();
        List<String> lines = Arrays.asList(sign.getLines());
        Sign s = (Sign) sign.getBlock().getState().getData();
        Block attached = sign.getBlock().getRelative(s.getAttachedFace());
        if (attached.getType() == Material.CHEST && checkIfContains(lines, "price:") && checkIfContains(lines, "sell")) {
            int price = 0;
            for (String line : sign.getLines()) {
                if (line.contains("price:")) {
                    try {
                        price = Integer.parseInt(line.split(":")[1].trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            int line = 0;
            Iterable<String> idk = Splitter.fixedLength(15).split(player.getName());
            List<Object> pieces = StreamSupport.stream(idk.spliterator(), false)
                    .collect(Collectors.toList());
            for (Object piece : pieces) {
                sign.setLine(line, piece.toString());
                line++;
            }
            Chest chest = (Chest) attached.getState();
            ItemStack[] elements = chest.getBlockInventory().getContents();
            Map<Boolean, List<ItemStack>> items = getChestItems(elements, player);
            Map.Entry<Boolean, List<ItemStack>> entry = items.entrySet().iterator().next();
            boolean isCorrect = entry.getKey();
            String name = "unknown";
            player.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_TURQUOISE + entry.getValue().get(0).getType()));
            if (isCorrect) {
                name = initGetItemsResponse(entry.getValue().get(0).getType().name(), entry.getValue().get(0).getDurability(), player);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + "You can't sell this item."));
            }
            sign.setLine(line, name);
            line++;
            sign.setLine(line, String.valueOf(price));
        }
    }

    private String getMaterialName(List<ItemModel> items, short durability) {
        String name = "unknown";
        for (ItemModel i : items) {
            System.out.println(i.getName() + " AAAAAAA");
            System.out.println(i.getDurability()+ " AAAAAAA");
            if (i.getDurability() == durability) {
                name = i.getName();
                break;
            }
        }
        return name;
    }

    private String initGetItemsResponse(String type, final short durability, final Player player) {
        final String[] name = {"unknown"};
        if (type.equalsIgnoreCase("cobble_wall")) {
            type = "cobblestone_wall";
        }
        if (type.equalsIgnoreCase("skull_item")) {
            type = "skull";
        }
        if (type.equalsIgnoreCase("red_rose")) {
            type = "red_flower";
        }
        if (type.equalsIgnoreCase("note_block")) {
            type = "noteblock";
        }
        if (type.equalsIgnoreCase("piston_base")) {
            type = "piston";
        }
        System.out.println(type.toLowerCase().trim());
        HttpClient.get("items/getByType/" + type.toLowerCase().trim(), new Observer<String>() {
            @Override
            public void onSuccess(String data) {
                try {
                    System.out.println("getItems " + data);
                    Type collectionType = new TypeToken<Collection<ItemModel>>() {
                    }.getType();
                    List<ItemModel> items = new Gson().fromJson(data, collectionType);
                    player.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_TURQUOISE + items.toString()));
                    name[0] = getMaterialName(items, durability);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("getItems " + throwable.getMessage()); 
            }
        });
        return name[0];
    }

    private Map<Boolean, List<ItemStack>> getChestItems(ItemStack[] elements, Player bPlayer) {
        Map<Boolean, List<ItemStack>> items = new HashMap<>();
        boolean isCorrect = false;
        List<ItemStack> filteredElements = new ArrayList<>();
        System.out.println("SI");
        if (elements.length > 0) {
            for (ItemStack i : elements) {
                if (i != null) {
                    filteredElements.add(i);
                }
            }
            int cont = 0;
            for (ItemStack i : filteredElements) {
                if (filteredElements.size() == 1) {
                    isCorrect = true;
                }
                if (filteredElements.size() > cont + 1 && i.getData().getItemType() == filteredElements.get(cont + 1).getData().getItemType()) {
                    isCorrect = true;
                }
                cont++;
            }
            System.out.println(isCorrect);
        } else {
            bPlayer.sendMessage(ChatColor.translateAlternateColorCodes(KEY_ALT_COLOR, Color.DARK_RED + "There are no items."));
        }
        items.put(isCorrect, filteredElements);
        return items;
    }
}