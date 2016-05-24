package com.thestratagemmc.biff.api;

import com.thestratagemmc.biff.event.BiffStimuliEvent;
import org.bukkit.Bukkit;

/**
 * Created by Axel on 2/12/2016.
 */
public class DefaultLearnCallback implements LearnCallback{

    private BiffStimuliEvent event;

    public DefaultLearnCallback(BiffStimuliEvent event){
        this.event = event;
    }

    @Override
    public void callback() {
        Bukkit.getPluginManager().callEvent(event);
    }
}
