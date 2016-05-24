package com.thestratagemmc.biffbot.listener;

import com.google.common.base.Joiner;
import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.*;
import com.thestratagemmc.biff.event.BiffListener;
import com.thestratagemmc.biff.event.BiffSpeakEvent;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import com.thestratagemmc.biff.listener.LearnedNamedEntity;
import com.thestratagemmc.biffbot.BiffPack;
import com.thestratagemmc.biffbot.hook.BrewHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Axel on 2/9/2016.
 */
public class MiscListeners extends BiffListener {

    HashMap<UUID,Long> lastJoin = new HashMap<>();
    int joinGreetingTime = 10 * 1000;
    public MiscListeners(BiffPlugin plugin) {
        super(plugin);
        plugin.getMessages().registerDefault("server","the server");
        plugin.getMessages().registerDefault("welcomeNewPlayer","Hey, {player}, welcome to {server} :)","Welcome {player}! :)","Welcome to {server}, {player} :)","Welcome!");
        plugin.getMessages().registerDefault("welcomeBackPlayer","hey {player}!","hello :)","what's up {player}", "welcome back {player}");
        plugin.getMessages().registerDefault("thankYou","thanks","ty","thx","thank", "thanx");
        plugin.getMessages().registerDefault("notOnline","{player} is not online.", "{player} isn't online.");
        plugin.getMessages().registerDefault("notAfk","{player} isn't afk.","{player} is not away.","{player} isn't away.", "{player} is not afk.");
        plugin.getMessages().registerDefault("howDrunk","{player} is {drunk}.");
        plugin.getMessages().registerDefault("seeYaPlayer","See ya, {player}.","Later, {player}!","See you later, {player}", "Will see ya, {player}", "See you around, {player}","Bye {player}.");
        plugin.getMessages().registerDefault("noProblem","No problem!","Anytime.","Lol, anytime.","Lol, no prob.","No problem.","No prob!","Lol, no problem.","Anytime!","Haha, anytime.","Haha, sure.", "Haha, no problem.");
        plugin.getMessages().registerDefault("biffNames", "biff","biffbot","server bot");
    }

    //TEST: welcome players on first join
    //what day is it? and be smart
    //TEST: add global biff rate limit
    //abbreviations

    //dictionary
    //youtube data api to look up videos/songs, possibly connect to dubtrack to play: Use with Genius?

    // who owns this land / read information from landlord
    // read information from mcmmo
    // onTime information

    //are there staff on?

    //respond when talked to
    //TEST: when someone joins and says hello, greet them (welcome vs. welcome back??)
    // locations: google places api services & and elevation and geocoding apis would be so cool. and time zone api
    // announcements webhook//discourse, reddit, etc
    // reportrts make tickets
    //Remote Biff!!
    // = need to be able to validate users with minecraft. requireValidate(username, platform, handlerOnceIt'sDone);
    // skype biff
    // irc biff
    // slack biff
    // cs:source biff because kek
    // dubtrack biff

    //be able to modify internal structure by talking to biff
    // - be able to tell you when time it sent a message and why: log these to a database?
    // - lookup last messages it responded to a player
    // - add platforms to remote biff
    // - force validate players
    // - add webhook
    // - force learn usernames
    // - add message for event
    // ---- alias each event into something typable
    // ------------- new datastore!
    // - modify named entities, because they could be wrong

    // also fix the thing in plots world where people can do /gm 3
    // and fix the placement of gmshift items

    // when will it be night?
    // ----- can everyone sleep? - detect afk players - sleep
    // -----------------------------track afk players!



    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        if (!event.getPlayer().hasPlayedBefore()){
            String message = plugin.getMessages().getRandom("welcomeNewPlayer");
            plugin.dispatchMessage("PlayerWelcomer","Welcome "+event.getPlayer().getName(),
                    message.replace("{name}",event.getPlayer().getName()).replace("{server}",plugin.getMessages().getRandom("{server}")), event.getPlayer().getUniqueId());
        }
        else{
            lastJoin.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void speakGreeting(BiffStimuliEvent event){
        if (!lastJoin.containsKey(event.getSender().getMinecraftId())) return;
        if (System.currentTimeMillis() - lastJoin.get(event.getSender().getMinecraftId()) > joinGreetingTime) return;
        if (!plugin.getBrain().isGreeting(event.getRequest().getWordMap())) return;
        event.respond("Greeter",plugin.getMessages().getRandom("welcomeBackPlayer").replace("{player}", event.getSender().getName()), BiffConviction.PRETTY_SURE);
    }

    @EventHandler
    public void later(BiffStimuliEvent event){
        BiffTestfor test = new BiffTestfor(event);
        if (test.contains("gonna/going/will/guys/i/ok/i'll") && test.contains("cya/go/leave/later/gtg") && event.getRequest().getWordMap().size() < 5){
            event.respond("SeerOff",plugin.getMessages().getRandom("seeYaPlayer").replace("{player}", event.getSender().getName()), BiffConviction.SOMEWHAT_SURE);
        }
    }

    @EventHandler
    public void isAfk(final BiffStimuliEvent event){
        BiffTestfor test = new BiffTestfor(event);
        if(test.isDefinitelyNotTense(BiffTense.PRESENT)) return;
        if (!test.contains("afk/away")) return;
        ArrayList<String> words = test.getWordsBetween("is","afk/away");
        List<String> newWords = test.removeNonNamedWords(words);
        if (newWords.size() == 0) return;
        String name = Joiner.on(" ").join(newWords);

        BiffUser user = plugin.getUserStore().getUserOnIndex(name);
        String n = null;
        if (plugin.getNamedEntities().get(name) != null){
            n = plugin.getNamedEntities().get(name).getAttributes().get("username");
        }
        Player _p = null;
        for (Player player : Bukkit.getOnlinePlayers()){
            if (player.getName().startsWith(name)) _p = player;
        }
        if (user == null && n == null && _p == null) {
            LearnedNamedEntity.learn(event, name, "nickname", new LearnCallback() {
                @Override
                public void callback() {
                    isAfk(new BiffStimuliEvent(plugin, event.getHandler(),event.getMessage(),event.getSender()));
                }
            });
            return;
        }
        Player p;
        if (user != null){
            p = user.getMinecraftPlayer();
        }
        else if (_p != null){
            p = _p;
        }
        else{
            p = Bukkit.getPlayer(n);
        }


        if (p == null){
            event.respond("IsAfk",plugin.getMessages().getRandom("notOnline").replace("{player}",(n == null) ? user.getName() : n), BiffConviction.PRETTY_SURE);
            return;
        }
        else {
            if(!BiffPack.isAfk(p)){
                event.respond("IsAfk",plugin.getMessages().getRandom("notAfk").replace("{player}",p.getName()), BiffConviction.PRETTY_SURE);
            }
            else{
                String timeMessage = BiffPack.getTimeAfk(p);
                event.respond("IsAfk",timeMessage,BiffConviction.PRETTY_SURE);
            }
        }

    }

    @EventHandler
    public void howDrunk(final BiffStimuliEvent event){
        BiffTestfor testfor = new BiffTestfor(event);
        if (testfor.isDefinitelyNotTense(BiffTense.PRESENT)) return;
        if (!testfor.contains("drunk")) return;
        String username = testfor.getWordAfter("is");

        BrewHook brewHook = new BrewHook();//will not run code further if brewery not enabled anyway
        Player player = plugin.getBrain().getRelevantPlayer(username);
        if (player == null){
            LearnedNamedEntity.learn(event, username, "nickname", new LearnCallback() {
                @Override
                public void callback() {
                    howDrunk(event);
                }
            });
        }
        else{
            event.respond("HowDrunk",plugin.getMessages().getRandom("howDrunk").replace("{player}",player.getName())
            .replace("{drunk}",brewHook.howDrunk(player)), BiffConviction.PRETTY_SURE);
        }
    }

    @EventHandler
    public void thanksBiff(final BiffStimuliEvent event){
        BiffTestfor test = new BiffTestfor(event);
        //Bukkit.broadcast("noob","noob");
        //Bukkit.broadcast(test.containsTag("thankYou")+"","test");
        //Bukkit.broadcast(test.containsTag("biffNames")+"","test");

        if (test.containsTag("biffNames") && test.containsTag("thankYou")){
            //Bukkit.broadcast("noob","noob");
            //if (test.contains(Joiner.on("/").join(getPlugin().getMessages().get("thankYou")))){
                //Bukkit.broadcast("noob","noob");
                event.respond("NoProb",plugin.getMessages().getRandom("noProblem"), BiffConviction.PRETTY_SURE);
            //}
        }
    }

    @EventHandler
    public void lastLinesOfConsole(BiffStimuliEvent event){
        if (!event.getHandler().senderHasPermission("biff.admin")) return;
        BiffTestfor test = new BiffTestfor(event);
        if (!test.contains("upload/post/send") || !test.contains("lines/log/error") || !test.contains("console")) return;
        int lines = 100;
        if (test.contains("last") && test.contains("lines")) lines = Integer.valueOf(test.getWordAfter("last"));
        else if (test.contains("lines")) lines = Integer.valueOf(test.getWordBefore("lines"));

        File f = new File("logs","latest.log");
       // Bukkit.broadcast(f.exists()+ "", "test");
        String l = BiffPack.getLastNLogLines(f, lines);
        String url = BiffPack.postHastebin(l);

        //Bukkit.broadcast(l, "test");
        event.respond("ConsolePaster",url, BiffConviction.PRETTY_SURE);
    }
}
