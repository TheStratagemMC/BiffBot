package com.thestratagemmc.biffbot;

import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.MessageHandler;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import com.thestratagemmc.droolchat.Channel;
import com.thestratagemmc.droolchat.ChatBus;
import com.thestratagemmc.droolchat.DroolChat;
import com.thestratagemmc.droolchat.bot.Bot;
import com.thestratagemmc.droolchat.bot.BotInfo;
import com.thestratagemmc.droolchat.event.ChannelChatEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axel on 5/23/2016.
 */
public class BBDroolChat extends JavaPlugin implements Listener {
    private Channel global;
    private Bot biff;
    private static BBDroolChat instance;

    public void onEnable(){
        instance = this;
        biff = new BiffBot();
        global = DroolChat.getInstance().getChannelStore().getChannel("global");
        DroolChat.getInstance().getBotStore().registerBot(this, "Biff", biff);
        MessageHandler h = new MessageHandler(BiffPlugin.getPlugin()) {
            @Override
            public void reply(final String message) {
                ChatBus.sendChatMessage(global, biff.getSender(), message, null);
            }

            @Override
            public void reply(final TextComponent component) {
               ChatBus.sendChatMessage(global, biff.getSender(), component.getText(), null);
            }

            @Override
            public int getChatSize() {
                return Bukkit.getOnlinePlayers().size();
            }

            @Override
            public List<String> getChatMembers() {
                return null;
            }

            @Override
            public boolean isPersonalChat() {
                return false;
            }
        };

        BiffPlugin.getPlugin().setDefaultMessageHandler(h);
        BiffPlugin.getPlugin().useMinecraftChat = false;
    }

    public class BiffBot extends Bot {
        private BotInfo info;



        @Override
        public BotInfo getInfo() {
            if (info == null) info = new BotInfo("Biff","No one likes me.", ChatColor.RED, "0.0");
            return info;
        }

        @Override
        public String respondToPM(Player player, String s) {
            return null;
        }

        @EventHandler
        public void chat(final ChannelChatEvent event){
            if (!event.getChannel().hasBots()) return;
            final BiffStimuliEvent e = new BiffStimuliEvent(BiffPlugin.getPlugin(), new MessageHandler(BiffPlugin.getPlugin()) {

                @Override
                public void reply(String message) {
                    ChatBus.sendChatMessage(event.getChannel(), biff.getSender(), message, event.getOrigin());
                }

                @Override
                public void reply(TextComponent component) {
                    ChatBus.sendChatMessage(event.getChannel(), biff.getSender(), component.getText(), event.getOrigin());
                }

                @Override
                public int getChatSize() {
                    return 0;
                }

                @Override
                public List<String> getChatMembers() {
                    return null;
                }

                @Override
                public boolean isPersonalChat() {
                    return false;
                }
            }, event.getMessage(), (event.getOrigin() != null) ? BiffPlugin.getPlugin().getUserStore().getByMinecraft(event.getOrigin().getUniqueId()) : null);
            getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {
                @Override
                public void run() {
                    getServer().getPluginManager().callEvent(e);
                }
            });;

        }
    }
}
