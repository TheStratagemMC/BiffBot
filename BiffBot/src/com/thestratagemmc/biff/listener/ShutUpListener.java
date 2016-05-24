package com.thestratagemmc.biff.listener;

import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.*;
import com.thestratagemmc.biff.event.BiffEvent;
import com.thestratagemmc.biff.event.BiffListener;
import com.thestratagemmc.biff.event.BiffSpeakEvent;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import com.thestratagemmc.biff.history.Conversation;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Axel on 2/10/2016.
 */
public class ShutUpListener extends BiffListener {
    private static final Pattern punctuationRegex = Pattern.compile("[\\Q][(){},.;!?<>%\\E]");
    Set<Long> lastResponses = new HashSet<>();
    private final int tenSeconds = 1000*3;
    private final int lockExpire = 1000*60*5;
    private final int maxForTenSeconds = 25;
    private final int banTime = 1000 * 15;
    private final Map<UUID,List<Spoke>> spokes = new HashMap<>();
    private final Map<UUID,Long> ban = new HashMap<>();
    private final Map<UUID,Integer> bans = new HashMap<>();

    public ShutUpListener(BiffPlugin plugin) {
        super(plugin);
        plugin.getMessages().registerDefault("playerMessagingTooMuch","You've requested me (whether or not you realize it) too many times recently. Please wait a few seconds.",
                "I will not talk anymore for a little bit. I've been talking too much!","I've talked too much. I'm going to quiet down a little now.");
        plugin.getMessages().registerDefault("playerMessagingTooSame","You're sending me the same messages too often.");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void speak(final BiffSpeakEvent event){
        //Bukkit.broadcast("test","test");

        Conversation convo= plugin.getConversationStore().getConversation(event.getWith());
        if (convo == null) return;
        String lastMessage = convo.getLastUserMessage();
        if (lastMessage != null){
            Map<String,Word> wordMap = new HashMap<>();
            String[] words = punctuationRegex.matcher(lastMessage).replaceAll("").split("\\s+");
            for (int i = 0; i < words.length; i++){
                String word = words[i].toLowerCase();
                wordMap.put(word, new Word(i, word));  //for logic
            }


            double score = plugin.getBrain().compareMaps(wordMap, event.getInputMap());
            if (score > 0.8){
                event.setCancelled(true);
                Bukkit.getPluginManager().callEvent(new BiffSolution("ShutUpListener", BiffConviction.COMPLETELY_SURE, "Told player to shut up.",
                        new BiffSolutionHandler() {
                            @Override
                            public void run() {
                                BiffUser user = plugin.getUserStore().get(event.getWith());

                                event.getMessageHandler().reply((user == null) ? user.getName()+". " : ""+plugin.getMessages().getRandom("playerMessagingTooSame"));
                            }
                        },null, null, null).getEvent(event.getMessageHandler()));
                addBan(event.getWith());
                ban.put(event.getWith(), System.currentTimeMillis());
                return;
            }
        }




        if (ban.containsKey(event.getWith())){
            if (System.currentTimeMillis() - ban.get(event.getWith()) > getBanTime(event.getWith())){
                ban.remove(event.getWith());
            }
            else event.setCancelled(true);
        }
        for (Long l : lastResponses){
            if ((System.currentTimeMillis() - l.longValue()) > tenSeconds) lastResponses.remove(l);
        }
        if (lastResponses.size() >= maxForTenSeconds) {
            event.setCancelled(true);
            return;
        }

        List<Spoke> newSpoke = new ArrayList<>();
        if (spokes.containsKey(event.getWith())){
            List<Spoke> list = spokes.get(event.getWith());
            List<Spoke> relevant = new ArrayList<>();
            for (Spoke spoke : list){
                if (System.currentTimeMillis() - spoke.getTime() < tenSeconds){
                    relevant.add(spoke);
                }
            }
            newSpoke.addAll(relevant);
            if (relevant.size() > 2){
                //Bukkit.broadcast("test2","test2");
                event.setCancelled(true);

                    Bukkit.getPluginManager().callEvent(new BiffSolution("ShutUpListener", BiffConviction.COMPLETELY_SURE, "Told player to shut up.",
                            new BiffSolutionHandler() {
                                @Override
                                public void run() {
                                    BiffUser user = plugin.getUserStore().get(event.getWith());

                                    event.getMessageHandler().reply((user == null) ? user.getName()+". " : ""+plugin.getMessages().getRandom("playerMessagingTooMuch"));
                                }
                            },null, null, null).getEvent(event.getMessageHandler()));
                addBan(event.getWith());
                ban.put(event.getWith(), System.currentTimeMillis());

                return;
            }
        }




        //add new
        newSpoke.add(new Spoke(System.currentTimeMillis(),event.getEventName()));
        spokes.put(event.getWith(), newSpoke);
    }

    public int getBanTime(UUID who){
        if (bans.containsKey(who)) return banTime * bans.get(who);
        else{
            return banTime;
        }
    }

    public void addBan(UUID who){
        if (System.currentTimeMillis() - ban.get(who) > lockExpire) {
            bans.put(who, 1);
        }
        if (bans.containsKey(who)) bans.put(who,bans.get(who) + 1);
        else bans.put(who, 1);
    }

    public class Spoke{
        private Long time;
        private String name;

        public Spoke(Long time, String name) {
            this.time = time;
            this.name = name;
        }

        public Long getTime() {
            return time;
        }

        public String getName() {
            return name;
        }
    }



}
