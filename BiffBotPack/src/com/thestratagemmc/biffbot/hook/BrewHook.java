package com.thestratagemmc.biffbot.hook;

import com.dre.brewery.BPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Axel on 2/12/2016.
 */
public class BrewHook {

    public String howDrunk(Player player){
        BPlayer p = BPlayer.get(player);
        if (p == null) return "not drunk";
        if (p.getDrunkeness() > 0){
            return p.getDrunkeness()+"% drunk";
        }
        else return "not drunk";
    }
}
