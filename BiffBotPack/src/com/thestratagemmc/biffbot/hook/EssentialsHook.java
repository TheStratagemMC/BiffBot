package com.thestratagemmc.biffbot.hook;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Axel on 2/12/2016.
 */
public class EssentialsHook {
    public Essentials plugin;

    public EssentialsHook(){
        plugin = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }
    /*
    Ideas:
    Make a listener just for Essentials.
    -- last seen
    -- last ip
    -- past accounts of (can do normally)
    -- turn godmode on for player, same with off
    -- give player godmode for x time
    -- give player flymode for x time: new type of handler? and then instead of making each one identical you could
    ----------- make one and give it a version for "godmode","flymode", etc. and check permissions. one listener and a lot of executors!!
    ----------------------do this for handling minecraft too, probably
    --
     */

    public long timeLastOnline(String name){
        User user = plugin.getOfflineUser(name);
        if (user == null) return -1;
         return user.getLastLogout();
    }

    public Location logoutLocation(String name){
        User user = plugin.getOfflineUser(name);
        if (user == null) return null;
        return user.getLogoutLocation();
    }
}
