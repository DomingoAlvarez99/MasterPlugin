package com.masterplugin.util;

import org.bukkit.event.player.PlayerJoinEvent;

public interface BeforeCRUD {
    void doTheJob(PlayerJoinEvent event);
}
