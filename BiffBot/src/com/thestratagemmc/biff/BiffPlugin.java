package com.thestratagemmc.biff;

import com.google.common.base.Joiner;
import com.thestratagemmc.biff.api.*;
import com.thestratagemmc.biff.data.*;
import com.thestratagemmc.biff.event.BiffSpeakEvent;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import com.thestratagemmc.biff.listener.LearnedNamedEntity;
import com.thestratagemmc.biff.listener.ServerAmount;
import com.thestratagemmc.biff.listener.ShutUpListener;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by Axel on 1/29/2016.
 */
public class BiffPlugin extends JavaPlugin implements Listener {
    public boolean useMinecraftChat = true;
    private final BiffSettings settings;
    private final BiffMessages messages;
    private BiffBrain brain;
    private final BiffResults solutions;
    private final NERDataStore namedEntities;
    private final BiffUserStore userStore;
    private final ConversationStore conversationStore;
    private static BiffPlugin instance;
    private final HashMap<UUID, NamedEntityFactory> learningEntities = new HashMap<>();
    private final HashMap<String,NamedEntityType> entityTypes = new HashMap<>();
    private MessageHandler defaultMessageHandler;

    public BiffPlugin(){
        userStore = new BiffUserStore();
        settings = new BiffSettings();
        messages = new BiffMessages();
        solutions = new BiffResults();
        namedEntities = new NERDataStore();
        conversationStore = new ConversationStore();

    }
    public void onEnable(){
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);


        messages.registerDefault("biffNames", "biff","biffbot","server bot");
        messages.registerDefault("biffTitle", ChatColor.RED + "Biff" + ChatColor.RESET);
        messages.registerDefault("unsureIfShouldRespond","Not sure if that was directed at me.","Was I supposed to respond to that? I'm working on improving my language processing. I could not understand.");

        userStore.init(this);
        settings.init(this);
        messages.init(this);
        namedEntities.init(this);
        brain = new BiffBrain();

        //getServer().getPluginManager().registerEvents(new ServerAmount(this), this);
        //getServer().getPluginManager().registerEvents(new LearnedNamedEntity(this), this);
        getServer().getPluginManager().registerEvents(new ShutUpListener(this), this);

        NETBuilder builder = new NETBuilder("server");
        builder.registerDisplayName("server","server");
        builder.setKeywords("server","ip");
        builder.setNEA(new NamedEntityAttribute() {
            @Override
            public String getName() {
                return "ip";
            }

            @Override
            public NERWord getWord() {
                return new NERWord(QuestionType.INFO_ABOUT,true, "ip");
            }

            @Override
            public void apply(NamedEntityFactory factory, String value) {
                factory.setAttributes(getName(), value);
            }

            @Override
            public String searchFor(BiffRequest request) {
                return getBrain().findIpAddressOrWebsite(request.getOriginalMessage());
            }
        });
        entityTypes.put("server", builder.get());

        NETBuilder b = new NETBuilder("nickname");
        b.registerDisplayName("nickname","nickname");
        b.setKeywords("nickname","username");
        b.setNEA(new NamedEntityAttribute() {
            @Override
            public String getName() {
                return "username";
            }

            @Override
            public NERWord getWord() {
                return new NERWord(QuestionType.INFO_ABOUT, true, "username");
            }

            @Override
            public void apply(NamedEntityFactory factory, String value) {
                factory.setAttributes(getName(), value);
            }

            @Override
            public String searchFor(BiffRequest request) {
                for (String word : request.getWordMap().keySet()){
                    if (Bukkit.getOfflinePlayer(word).hasPlayedBefore()){
                        return word;
                    }
                }
                return null;
            }
        });
        entityTypes.put("nickname",b.get());
        defaultMessageHandler = new MessageHandler(this) {
            @Override
            public void reply(String message) {
                Bukkit.broadcastMessage(ChatColor.RED+"Biff"+ChatColor.getByChar('7')+": "+message);
            }

            @Override
            public void reply(TextComponent component) {
                for (Player player : Bukkit.getOnlinePlayers()){
                    player.spigot().sendMessage(component);
                }
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
    }

    public BiffBrain getBrain(){
        return brain;
    }
    public BiffSettings getSettings(){
        return settings;
    }
    public BiffMessages getMessages(){
        return messages;
    }
    public BiffResults getSolutionsManager(){ return solutions; }

    public MessageHandler getDefaultMessageHandler() {
        return defaultMessageHandler;
    }

    public void setDefaultMessageHandler(MessageHandler defaultMessageHandler) {
        this.defaultMessageHandler = defaultMessageHandler;
    }

    public void dispatchMessage(String name, String why, final String message, UUID with){
        Bukkit.getPluginManager().callEvent(new BiffSpeakEvent(false, name, why, BiffConviction.COMPLETELY_SURE, new BiffSolutionHandler() {
            @Override
            public void run() {
                defaultMessageHandler.reply(message);
                Bukkit.broadcast("test: "+message, "test");
            }
        },null,null,with,defaultMessageHandler));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void chat(final AsyncPlayerChatEvent event){
        if (!useMinecraftChat) return;
        final BiffStimuliEvent be = new BiffStimuliEvent(this, new MessageHandler(this) {
            @Override
            public void reply(final String message) {
                runSync(new Runnable() {
                    @Override
                    public void run() {
                        for (Player player : event.getRecipients()){
                            player.sendMessage(String.format(event.getFormat(), ChatColor.RED+"Biff",message));
                        }
                    }
                });

            }

            @Override
            public void reply(final TextComponent component) {
                runSync(new Runnable() {
                    @Override
                    public void run() {
                        TextComponent out = new TextComponent(String.format(event.getFormat(), ChatColor.RED+"Biff"));
                        out.addExtra(component);
                        for (Player player : event.getRecipients()){
                            player.spigot().sendMessage(out);
                        }
                    }
                });
            }

            @Override
            public int getChatSize() {
                return event.getRecipients().size();
            }

            @Override
            public List<String> getChatMembers() {
                List<String> members = new ArrayList<>();
                for (Player p : event.getRecipients()){
                    members.add(p.getName());
                }
                return members;
            }

            @Override
            public boolean isPersonalChat() {
                return false;
            }

            @Override
            public boolean senderHasPermission(String permission){
                return event.getPlayer().hasPermission(permission);
            }
        }, event.getMessage(), userStore.getByMinecraft(event.getPlayer().getUniqueId()));
        /*if (event.isAsynchronous()){
            Bukkit.getPluginManager().callEvent(be);
        }
        else{*/
            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(be);
                }
            });
        //}
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void runSolution(BiffStimuliEvent event){
        List<BiffSolution> results = solutions.getSolutions(event.getRequest().getId());

        if (results.size() == 0){
            //die. no results, continue on
        }
        else if (results.size() == 1){
            Bukkit.getPluginManager().callEvent(results.get(0).getEvent(event.getHandler()));
        }
        else if (results.size() > 1){
            List<BiffSolution> tie = new ArrayList<>();
            BiffSolution topResult = null;
            for (BiffSolution sol : results){
                if (sol.conviction.getWeight() < 6) continue;
                if (topResult == null) {
                    topResult = sol;
                    continue;
                }
                if (sol.conviction.getWeight() > topResult.conviction.getWeight()){
                    topResult = sol;
                    if (tie.size() > 0){
                        if (tie.get(0).conviction.getWeight() < sol.conviction.getWeight()) tie.clear();
                    }
                }
                if (sol.conviction.getWeight() == topResult.conviction.getWeight()) {
                    tie.add(topResult);
                    tie.add(sol);
                }
            }
            if (tie.size() > 0){
                //default behavior, may be customizable later?
                if (event.getHandler().getChatSize() < 5){
                    event.getHandler().reply(messages.getRandom("unsureIfShouldRespond"));
                    return;
                }
            }
            if (topResult != null){
                Bukkit.getPluginManager().callEvent(topResult.getEvent(event.getHandler()));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void speak(BiffSpeakEvent event){
        if (event.isCancelled()) return;
        event.getHandler().run();
        getConversationStore().continueConversation(event.getWith(), event.getMessage());
    }

    @EventHandler
    public void join(final PlayerJoinEvent event){
        final BiffUser user = userStore.getByMinecraft(event.getPlayer().getUniqueId());
        if (user != null){
            if (user.getName().equalsIgnoreCase(event.getPlayer().getName())) return;
        }

        getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                synchronized (userStore){
                    BiffUser _user;
                    if (user == null){
                        _user = new BiffUser(event.getPlayer().getName(), UUID.randomUUID(), event.getPlayer().getUniqueId());
                    }
                    else _user = new BiffUser(event.getPlayer().getName(), user.getBiffId(), event.getPlayer().getUniqueId(), user.getKnownNicknames(), user.getOtherAccountNames());
                    userStore.update(_user);
                }
            }
        });
    }

    public NERDataStore getNamedEntities(){
        return namedEntities;
    }

    public ConversationStore getConversationStore(){
        return conversationStore;
    }
    public BiffUserStore getUserStore(){
        return userStore;
    }

    public NamedEntityFactory currentFactory(UUID user){
        return learningEntities.get(user);
    }

    public void setFactory(UUID user, NamedEntityFactory fac){
        learningEntities.put(user, fac);
    }
    public static BiffPlugin getPlugin(){
        return instance;
    }

    public NamedEntityType getType(String name){
        return entityTypes.get(name);
    }

    public Collection<NamedEntityType> getTypes(){
        return entityTypes.values();
    }
    public void registerNamedEntityType(NamedEntityType type){
        entityTypes.put(type.getName(), type);
    }
}
