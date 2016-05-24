package com.thestratagemmc.biffbot;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.BiffConviction;
import com.thestratagemmc.biff.api.BiffRequest;
import com.thestratagemmc.biff.api.BiffTestfor;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import com.thestratagemmc.biff.history.*;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Axel on 2/20/2016.
 */
public class BiffCleverBot extends JavaPlugin implements Listener {

    protected final Map<String,ChatterBotSession> sessions = new HashMap<>();
    protected final Map<UUID,BiffRequest> lastMessage = new HashMap<>();
    ChatterBot bot;

    public void onEnable(){
        getServer().getPluginManager().registerEvents(this, this);
        ChatterBotFactory fac = new ChatterBotFactory();
        try{
            bot = fac.create(ChatterBotType.CLEVERBOT);
        }catch(Exception e){
            e.printStackTrace();
            Bukkit.broadcastMessage("Could not create biffbot: "+e.getMessage());
        }
    }

    @EventHandler
    public void biffStim(BiffStimuliEvent event){
        BiffTestfor test = new BiffTestfor(event);

        if (!test.containsTag("biffNames")){

            BiffRequest last = lastMessage.get(event.getSender().getBiffId());
            if (last == null) return;
            boolean allowed = false;

            int score = 0;
            if ((last.getWordMap().containsKey("me") || last.getWordMap().containsKey("i"))){
                int temp = 0;
                if (last.getWordMap().containsKey("you") || last.getWordMap().containsKey("your") || last.getWordMap().containsKey("yours")){
                    temp++;
                }
                if (test.containsTag("mePronouns")){
                    score += 1 + temp;
                }
            }
            for (String word : last.getWordMap().keySet()){
                if (test.contains(word)) score ++;
            }

            if (score < 2) return;
        }

        //Bukkit.broadcast("ksdk","kdsk");
        ChatterBotSession session;
        if (sessions.containsKey(event.getSender().getName())){
            session = sessions.get(event.getSender().getName());
        }
        else
            session = bot.createSession();


        try{
            String out = session.think(URLDecoder.decode(event.getMessage()));
            //Bukkit.broadcast(out, "kdsdk");
            ConversationMessage message = new ConversationMessage(Chatter.BIFF, out, IntentionType.RANDOM_RESPONSE, System.currentTimeMillis()) {
                @Override
                public Intention getIntention() {
                    return new Intention(IntentionType.RANDOM_RESPONSE);
                }
            };
            lastMessage.put(event.getSender().getBiffId(), new BiffRequest(BiffPlugin.getPlugin(), out));
            event.respond("BiffCleverbot", out, BiffConviction.MAYBE, message);
        }catch(Exception e){
            e.printStackTrace();
        }
        sessions.put(event.getSender().getName(), session);
    }
}
