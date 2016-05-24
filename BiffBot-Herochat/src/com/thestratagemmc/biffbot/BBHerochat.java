package com.thestratagemmc.biffbot;

import com.dthielke.herochat.*;
import com.google.common.base.Joiner;
import com.mojang.authlib.GameProfile;
import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.MessageHandler;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by Axel on 2/7/2016.
 */
public class BBHerochat extends JavaPlugin implements Listener {

    private HashMap<String,String> registeredFormats = new HashMap<>();

    public void onEnable(){
        getServer().getPluginManager().registerEvents(this,this);
        BiffPlugin.getPlugin().useMinecraftChat = false;

        saveConfig();
        saveDefaultConfig();

        ConfigurationSection channels = getConfig().getConfigurationSection("channels");
        for (String key : channels.getKeys(false)){
            registeredFormats.put(key, channels.getString(key));
        }

        final Channel global = Herochat.getChannelManager().getChannel("global");
        MessageHandler h = new MessageHandler(BiffPlugin.getPlugin()) {
            @Override
            public void reply(final String message) {
                runSync(new Runnable() {
                            @Override
                            public void run() {

                                String title = ChatColor.RED + "Biff";
                                //Bukkit.broadcast(title,title);


                                //String msg = global.getColor()+"["+global.getNick()+"] "+title+ChatColor.getByChar('7')+": "+message;
                                String msg = ChatColor.translateAlternateColorCodes('&',getFormat(global.getName().toLowerCase())
                                        .replace("{color}",global.getColor().toString()).replace("{nick}", global.getNick())
                                        .replace("{sender}", title).replace("{message}",message));
                                for (Chatter chatter : global.getMembers()){
                                    Player p = chatter.getPlayer();
                                    if (p != null){
                                        p.sendMessage(msg);
                                    }
                                }



                            }
                        }
                );

            }

            @Override
            public void reply(final TextComponent component) {
                runSync(new Runnable() {
                            @Override
                            public void run() {
                                //Bukkit.broadcast("test","test");
                                //String title = BiffPlugin.getPlugin().getMessages().getFirst("biffTitle");
                                String title = ChatColor.RED + "Biff";
                                //Bukkit.broadcast(title,title);
                                TextComponent out = new TextComponent(ChatColor.translateAlternateColorCodes('&',getFormat(global.getName().toLowerCase())
                                        .replace("{color}",global.getColor().toString()).replace("{nick}", global.getNick())
                                        .replace("{sender}", title).replace("{message}","")));
                                out.addExtra(component);
                                for (Chatter chatter : global.getMembers()){
                                    Player p = chatter.getPlayer();
                                    if (p != null){
                                        p.spigot().sendMessage(out);
                                    }
                                }
                            }
                        }
                );
            }

            @Override
            public int getChatSize() {
                return global.getMembers().size();
            }

            @Override
            public List<String> getChatMembers() {
                List<String> members = new ArrayList<>();
                for (Chatter chatter : global.getMembers()){
                    members.add(chatter.getName());
                }
                return members;
            }

            @Override
            public boolean isPersonalChat() {
                return false;
            }
        };

        BiffPlugin.getPlugin().setDefaultMessageHandler(h);

        /*CraftServer server = (CraftServer)Bukkit.getServer();
        WorldServer world = ((CraftWorld)Bukkit.getWorld("world")).getHandle();
        server.getOnlinePlayers().add(new CraftPlayer(server, new EntityPlayer(MinecraftServer.getServer(), world, new GameProfile(UUID.randomUUID(), BiffPlugin.getPlugin().getMessages().getRandom("biffTitle")), new PlayerInteractManager(world))));
        Bukkit.getLogger().warning("skafjskdfsf");*/
    }



    @EventHandler
    public void channelChat(final ChannelChatEvent event){
        if (event.getChannel().getName().startsWith("convo") && event.getChannel().getMembers().size()< 3) return; //skip on /msg conversations
        final BiffStimuliEvent e = new BiffStimuliEvent(BiffPlugin.getPlugin(), new MessageHandler(BiffPlugin.getPlugin()) {
            @Override
            public void reply(final String message) {
                runSync(new Runnable() {
                            @Override
                            public void run() {

                                String title = ChatColor.RED + "Biff";
                                //Bukkit.broadcast(title,title);
                                String msg = event.getChannel().getColor()+"["+event.getChannel().getNick()+"] "+title+ChatColor.getByChar('7')+": "+message;
                                    int people = 0;
                                    for (Chatter chatter : event.getChannel().getMembers()){
                                        Player p = chatter.getPlayer();
                                        if (p != null){
                                            if (event.getChannel().isLocal() || event.getChannel().getName().equalsIgnoreCase("local")){
                                                //Bukkit.broadcast("test","test");
                                                if (!event.getSender().getPlayer().getWorld().getName().equalsIgnoreCase(p.getWorld().getName())) continue;
                                                if (event.getSender().getPlayer().getLocation().distance(p.getLocation()) > event.getChannel().getDistance()) continue;
                                                people++;
                                            }
                                            p.sendMessage(msg);
                                        }
                                    }
                               // Bukkit.broadcast("Sent message in local to "+people+" people.", "test");



                            }
                        }
                );

            }

            @Override
            public void reply(final TextComponent component) {
                runSync(new Runnable() {
                            @Override
                            public void run() {
                                //Bukkit.broadcast("test","test");
                                //String title = BiffPlugin.getPlugin().getMessages().getFirst("biffTitle");
                                String title = ChatColor.RED + "Biff";
                                //Bukkit.broadcast(title,title);
                                TextComponent out = new TextComponent(event.getChannel().getColor()+"["+event.getChannel().getNick()+"] "+title+ChatColor.getByChar('7')+": ");
                                out.addExtra(component);
                                int people = 0;
                                for (Chatter chatter : event.getChannel().getMembers()){
                                    Player p = chatter.getPlayer();
                                    if (p != null){
                                        if (event.getChannel().isLocal() || event.getChannel().getName().equalsIgnoreCase("local")){
                                            //Bukkit.broadcast("test","test");
                                            if (!event.getSender().getPlayer().getWorld().getName().equalsIgnoreCase(p.getWorld().getName())) continue;
                                            if (event.getSender().getPlayer().getLocation().distance(p.getLocation()) > event.getChannel().getDistance()) continue;
                                            people++;
                                        }
                                        p.spigot().sendMessage(out);
                                    }
                                }
                            }
                        }
                );
            }

            @Override
            public int getChatSize() {
                return event.getChannel().getMembers().size();
            }

            @Override
            public List<String> getChatMembers() {
                List<String> members = new ArrayList<>();
                for (Chatter chatter : event.getChannel().getMembers()){
                    members.add(chatter.getName());
                }
                return members;
            }

            @Override
            public boolean isPersonalChat() {
                return false;
            }
        }, event.getMessage(), BiffPlugin.getPlugin().getUserStore().getByMinecraft(event.getSender().getPlayer().getUniqueId()));
        getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                getServer().getPluginManager().callEvent(e);
            }
        });

    }

    /*@EventHandler
    public void commandEvent(PlayerCommandPreprocessEvent event){
        for (String title : BiffPlugin.getPlugin().getMessages().get("biffNames")){
            if (event.getMessage().startsWith("/msg "+title+" ")){
                Chatter biff = new StandardChatter(new ChatterStorage() {
                    @Override
                    public void flagUpdate(Chatter chatter) {
                        //
                    }

                    @Override
                    public Chatter load(String s) {
                        return null;
                    }

                    @Override
                    public void removeChatter(Chatter chatter) {

                    }

                    @Override
                    public void update() {

                    }

                    @Override
                    public void update(Chatter chatter) {

                    }
                }, new BiffPlayer());
                String[] split = event.getMessage().split(" ");
                String message = Joiner.on(" ").join(Arrays.copyOfRange(split, 2, split.length));
                Chatter sender = Herochat.getChatterManager().getChatter(event.getPlayer());

                ConversationChannel convo = new ConversationChannel(sender, biff,Herochat.getChannelManager());
                String channelName = "convoBiff"+sender.getName();
                if (!Herochat.getChannelManager().hasChannel(channelName)){
                    Herochat.getChannelManager().addChannel(convo);
                }

                ChannelChatEvent e = new ChannelChatEvent(sender, convo
                Bukkit.getPluginManager().callEvent(e);
            }

        }
    }*/

    public String getFormat(String channel){
        if (registeredFormats.containsKey(channel)){
            return channel;
        }
        if (registeredFormats.containsKey("all")){
            return registeredFormats.get("all");
        }
        return "{color}[{nick}] &f{sender}: &7{message}";
    }
}
