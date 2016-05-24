package com.thestratagemmc.biff.event;

import com.thestratagemmc.biff.BiffPlugin;
import org.bukkit.event.Listener;

/**
 * Created by Axel on 1/30/2016.
 */
public class BiffListener implements Listener {
    protected BiffPlugin plugin;

    public BiffListener(BiffPlugin plugin) {
        this.plugin = plugin;
    }

    protected BiffPlugin getPlugin(){
        return plugin;
    }
}
