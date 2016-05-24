package com.thestratagemmc.biff.api;

import com.thestratagemmc.biff.BiffPlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

/**
 * Created by Axel on 1/29/2016.
 */
public abstract class MessageHandler {
    private BiffPlugin plugin;

    public MessageHandler(BiffPlugin plugin){
        this.plugin = plugin;
    }

    protected void runSync(Runnable run){
        plugin.getServer().getScheduler().runTask(plugin, run);
    }
    public abstract void reply(final String message);
    public abstract void reply(final TextComponent component);
    public abstract int getChatSize();
    public abstract List<String> getChatMembers();
    public abstract boolean isPersonalChat();
    public boolean senderHasPermission(String permission){ //override this
        return true;
    }

}
