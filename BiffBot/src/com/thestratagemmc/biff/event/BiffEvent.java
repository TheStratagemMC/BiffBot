package com.thestratagemmc.biff.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Axel on 1/29/2016.
 */
public class BiffEvent extends Event {
    public BiffEvent(boolean async){
        super(async);
    }
    private static HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList(){
        return handlerList;
    }
}
