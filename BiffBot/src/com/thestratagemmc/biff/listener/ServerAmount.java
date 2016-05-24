package com.thestratagemmc.biff.listener;

import com.google.common.base.Joiner;
import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.*;
import com.thestratagemmc.biff.event.BiffListener;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Axel on 1/29/2016.
 */
public class ServerAmount extends BiffListener {

    public ServerAmount(BiffPlugin plugin) {
        super(plugin);

        plugin.getMessages().registerDefault("triggerPlayersOnline","people/players","on/online");
        plugin.getMessages().registerDefault("messagePlayersOnline", "There are %num% players online %ip%.","%num% players are on %ip%.");
        plugin.getMessages().registerDefault("messagePlayersOnlineHere", "%num% players are online here.", "There are %num% players online on this server.");
        plugin.getMessages().registerDefault("messagePlayersOnlineError","I couldn't find that server.");
    }

    @EventHandler
    public void onTrigger(final BiffStimuliEvent event){
        //Bukkit.broadcastMessage("yo");
        if (!event.getHandler().senderHasPermission("biff.serverinfo")) return;
        //Bukkit.broadcastMessage(String.valueOf(event.isAsynchronous()));
        BiffTestfor testfor = new BiffTestfor(event);

        testfor.setQuestionType(QuestionType.COUNT);
        testfor.addNecessaryWords("triggerPlayersOnline");
        BiffConviction conviction = testfor.matches();
        if (conviction.getWeight() < 6) return;
        //Bukkit.broadcastMessage("yoyo");
        if (testfor.getLikelyTense() != BiffTense.PRESENT && testfor.getLikelyTense() != BiffTense.UNKNOWN) return;
        String ip = null;
        List<String> wordsAfter = testfor.getWordsAfter("on");
        if (wordsAfter != null){
            NamedEntity ent = testfor.getNamedEntity(wordsAfter);
            String textAfter = Joiner.on(" ").join(wordsAfter);
            String _ip = plugin.getBrain().findIpAddressOrWebsite(textAfter);
            if (_ip == null && ent == null){
                //not ip, try to ask about it?
                //Bukkit.broadcastMessage(textAfter);
                LearnedNamedEntity.learn(event, textAfter, "server", new DefaultLearnCallback(event)); //broken
            }
            if (ent == null){
                ip = testfor.getWordAfter("on");
            }
            else{
                if (ent.getType().equalsIgnoreCase("server")){
                    ip = ent.getAttributes().get("ip");
                }
                else{
                    ip = null;
                }
            }
        }

        if (ip == null){
            final String response = getPlugin().getMessages().getRandom("messagePlayersOnlineHere").replace("%num%", String.valueOf(Bukkit.getOnlinePlayers().size())).replace("%ip%", "");
            event.getRequest().runSolution("serverAmount", "Returned number of players online.", BiffConviction.COMPLETELY_SURE, response, new BiffSolutionHandler() {
                @Override
                public void run() {
                    event.getHandler().reply(response);
                }
            }, event.getSender().getBiffId());
            //Bukkit.broadcastMessage("yoyoyo");
            return;
        }
        else{
            //try pinging ip
            String[] d = ip.split(":");
            int port = 25565;
            String host;
            if (d.length > 1){
                host = d[0];
                port = Integer.valueOf(d[1]);
            }
            else host = ip;

            //try pinging
            final String[] info = serverInfo(host, port);
            if (info == null){
                //check how close the ip was
                if (!ip.contains(".")){ //nope
                    return;
                }
                final String response = getPlugin().getMessages().getRandom("messagePlayersOnlineError");
                event.getRequest().runSolution("serverAmount", "Tried to get number of players online " + ip + ", but had a problem.", BiffConviction.POSSIBLY, response, new BiffSolutionHandler() {
                    @Override
                    public void run() {
                        event.getHandler().reply(response);
                    }
                }, event.getSender().getBiffId());
                return;
            }
            else{
                final String response = getPlugin().getMessages().getRandom("messagePlayersOnline").replace("%num%", String.valueOf(info[1])).replace("%ip%", ip);
                event.getRequest().runSolution("serverAmount", "Returned number of players online " + ip, BiffConviction.COMPLETELY_SURE, response, new BiffSolutionHandler() {
                    @Override
                    public void run() {
                        event.getHandler().reply(response);
                    }
                }, event.getSender().getBiffId());
                return;
            }
        }
    }

    public static String[] serverInfo(String host, int port){
        try {
            @SuppressWarnings("resource")
            Socket sock = new Socket(host, port);
            sock.setSoTimeout(2000);
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            DataInputStream in = new DataInputStream(sock.getInputStream());

            out.write(0xFE);

            int b;
            StringBuffer str = new StringBuffer();
            while ((b = in.read()) != -1) {
                if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) {
                    str.append((char) b);
                }
            }

            String[] data = str.toString().split("§");

            return data;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
