package com.thestratagemmc.biffbot.listener;

import com.google.common.base.Joiner;
import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.*;
import com.thestratagemmc.biff.event.BiffListener;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import com.thestratagemmc.biff.history.Chatter;
import com.thestratagemmc.biff.history.ConversationMessage;
import com.thestratagemmc.biff.history.Intention;
import com.thestratagemmc.biff.history.IntentionType;
import com.thestratagemmc.biff.listener.LearnedNamedEntity;
import com.thestratagemmc.biffbot.hook.EssentialsHook;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axel on 2/14/2016.
 */
public class EssentialsListener extends BiffListener {
    public EssentialsListener(BiffPlugin plugin) {
        super(plugin);
        plugin.getMessages().registerDefault("triggerLastLoggedOut","when");
        plugin.getMessages().registerDefault("isOnline","{player} is online.");
        plugin.getMessages().registerDefault("wasOnline","{player} was online {time} ago.");
        plugin.getMessages().registerDefault("userNotFound","User not found.");
        plugin.getMessages().registerDefault("wasWhere","{player} was last at: {location}.");
    }

    @EventHandler
    public void lastLoggedOut(final BiffStimuliEvent event){
        BiffTestfor test = new BiffTestfor(event);
        EssentialsHook hook = new EssentialsHook();

        if (test.isDefinitelyNotTense(BiffTense.PAST)) return;
        if (!test.isQuestionType(QuestionType.TIME, false)) return;
        //Bukkit.broadcast("ksjadf","kasdf");
        if (!test.isQuestion()) return;
        if (!test.contains("last/online/on") && !test.contains("logout/log/out")) return;

        ArrayList<String> words = test.getWordsBetween("was/did","last/log/logout/online/on");
        String name = Joiner.on(" ").join(test.removeNonNamedWords(words));

        Player p = plugin.getBrain().getRelevantPlayer(name);

        String user = name;
        if (p == null){
            if (BiffPlugin.getPlugin().getNamedEntities().get(name) != null){
                user = BiffPlugin.getPlugin().getNamedEntities().get(name).getAttributes().get("username");
            }
        }
        if (p != null && ((p != null) ? p.isOnline() : false)){
            event.respond("LastOnline",plugin.getMessages().getRandom("isOnline").replace("{player}", p.getName()), BiffConviction.PRETTY_SURE);
        }
        else{
            if (hook.timeLastOnline(user) == -1){
                LearnedNamedEntity.learn(event, name, "nickname", new LearnCallback() {
                    @Override
                    public void callback() {
                        lastLoggedOut(event);
                    }
                });
            }
            String online = DurationFormatUtils.formatDurationWords(
                    System.currentTimeMillis() - hook.timeLastOnline(user), true, true);
            event.respond("LastOnline",plugin.getMessages().getRandom("wasOnline").replace(
                    "{player}",user).replace("{time}",online), BiffConviction.PRETTY_SURE);

        }
    }

    @EventHandler
    public void lastLocation(final BiffStimuliEvent event){
        BiffTestfor test = new BiffTestfor(event);
        EssentialsHook hook = new EssentialsHook();
        if (!test.isQuestionType(QuestionType.WHERE, true)) return;
        if (!test.contains("where") || !test.contains("logout")) return;
        ArrayList<String> target = test.getWordsBetween((test.contains("did") ? "did" : "where"),"logged/left/log/leave");

        String name = Joiner.on(" ").join(test.removeNonNamedWords(target));
        Player player = plugin.getBrain().getRelevantPlayer(name);
        if (player != null){
            event.respond("LastLocation",plugin.getMessages().getRandom("isOnline").replace("{player}",player.getName()), BiffConviction.SOMEWHAT_SURE);
            return;
        }


        Location loc = hook.logoutLocation(name);

        if (loc == null){
            BiffUser user = plugin.getUserStore().getUserOnIndex(name);
            name = user.getName();
            loc = hook.logoutLocation(user.getName());
        }

        if (loc == null){
            event.respond("LastLocation",plugin.getMessages().getRandom("userNotFound"), BiffConviction.PRETTY_SURE);
            return;
        }

        final Location l = loc;
        String message = plugin.getMessages().getRandom("wasWhere").replace("{player}", name).replace("{location}",plugin.getBrain().location(loc));
        ConversationMessage c = new ConversationMessage(Chatter.BIFF, message, IntentionType.LIST_COORDINATES, System.currentTimeMillis()) {
            @Override
            public Intention getIntention() {
                Intention i = new Intention(IntentionType.LIST_COORDINATES);
                i.getInformation().put("world", l.getWorld().getName());
                i.getInformation().put("x",l.getBlockX()+"");
                i.getInformation().put("y",l.getBlockY()+"");
                i.getInformation().put("z",l.getBlockZ()+"");
                return i;
            }
        };

        event.respond("LastLocation",message, BiffConviction.PRETTY_SURE, c);

    }
}
