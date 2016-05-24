package com.thestratagemmc.biffbot;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biffbot.listener.ChatThings;
import com.thestratagemmc.biffbot.listener.EssentialsListener;
import com.thestratagemmc.biffbot.listener.LinkOpener;
import com.thestratagemmc.biffbot.listener.MiscListeners;
import javafx.util.converter.DateTimeStringConverter;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Axel on 2/9/2016.
 */
public class BiffPack extends JavaPlugin implements Listener {

    private static HashMap<UUID,Long> lastMoved = new HashMap<>();
    private static final int legallyAfk = 1000 * 60;
    private static final JSONParser parser = new JSONParser();
    public void onEnable(){
        getServer().getPluginManager().registerEvents(new LinkOpener(BiffPlugin.getPlugin()), this);
        getServer().getPluginManager().registerEvents(new MiscListeners(BiffPlugin.getPlugin()), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new EssentialsListener(BiffPlugin.getPlugin()), this);
        getServer().getPluginManager().registerEvents(new ChatThings(BiffPlugin.getPlugin()), this);
        BiffPlugin.getPlugin().getMessages().registerDefault("afkTime","{player} has been away from keyboard for {time}");

        getCommand("biffsay").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
                if (!sender.hasPermission("biff.say")){
                    sender.sendMessage("No permission.");
                    return true;
                }
                String message = Joiner.on(" ").join(args);
                BiffPlugin.getPlugin().dispatchMessage("BiffSay","User-submitted",message,null);
                return true;
            }
        });
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent event){
        if (event.getFrom().distance(event.getTo()) < 0.2) return;
        lastMoved.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    public static boolean isAfk(Player player){
        if (!lastMoved.containsKey(player.getUniqueId())) return false;
        if ((System.currentTimeMillis() - lastMoved.get(player.getUniqueId())) > legallyAfk) return true;
        return false;
    }

    public static String getTimeAfk(Player player){
        if (lastMoved.containsKey(player.getUniqueId())){

            return BiffPlugin.getPlugin().getMessages().getRandom("afkTime").replace("{player}",player.getName())
                    .replace("{time}", DurationFormatUtils.formatDurationWords((System.currentTimeMillis() - lastMoved.get(player.getUniqueId())), true, true) + ".");
        }
        return null;
    }

    public static String getLastNLogLines(File file, int nLines) {
        /*StringBuilder s = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec("tail -"+nLines+" "+file);
            java.io.BufferedReader input = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            String line = null;
            //Here we first read the next line into the variable
            //line and then check for the EOF condition, which
            //is the return value of null
            while((line = input.readLine()) != null){
                s.append(line+'\n');
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return s.toString();*/
        try{
            String s = Joiner.on("\n").join(tail(file, Charsets.UTF_8.name(), nLines, 80));
            return s;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String postHastebin(String i) {
        StringBuilder s = new StringBuilder();
        try {
            //File test = new File("test.file");
            //Bukkit.broadcast(i, "test");
            File newFile = new File("last-biffbin.txt");
            if (newFile.exists()) newFile.delete();

            newFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(newFile);
            stream.write(i.getBytes());
            stream.close();
            Process p = Runtime.getRuntime().exec("curl -XPOST http://hastebin.com/documents --data-binary @last-biffbin.txt");
            java.io.BufferedReader input = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            String line = null;

            while ((line = input.readLine()) != null) {
                s.append(line + '\n');
            }
            input.close();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Bukkit.broadcast("aksfjasd","test");
        //Bukkit.broadcast(s.toString(), "test");
        try {
            JSONObject obj = (JSONObject) parser.parse(s.toString());
            String key = (String) obj.get("key");
            return "http://hastebin.com/" + key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String[] tail(File textFile, String charSet, int lines, int lineLengthHint)
            throws IOException {
        if (lineLengthHint < 80) {
            lineLengthHint = 80;
        }
        RandomAccessFile in = new RandomAccessFile(textFile, "r");
        try {
            long fileSize = in.length();
            int bytesCount = lines * lineLengthHint;
            // Loop allocating a byte array hopefully sufficiently large.
            for (;;) {
                if (fileSize < bytesCount) {
                    bytesCount = (int)fileSize;
                }
                byte[] bytes = new byte[bytesCount];
                in.seek(fileSize - bytesCount);
                in.readFully(bytes);

                int startIndex = bytes.length; // Position of last '\n'.
                int lineEndsFromStart = 0;
                boolean bytesCountSufficient = true;
                while (lineEndsFromStart - 1 < lines) {
                    int pos = startIndex - 1;
                    while (pos >= 0 && bytes[pos] != '\n') {
                        --pos;
                    }
                    startIndex = pos; // -1 will do fine.
                    ++lineEndsFromStart;
                    if (pos < 0) {
                        bytesCountSufficient = false;
                        break;
                    }
                }
                if (bytesCountSufficient || fileSize == bytesCount) {
                    String text = new String(bytes, startIndex + 1,
                            bytes.length - (startIndex + 1), charSet);
                    return text.split("\r?\n");
                }
                // Not bytesCountSufficient:
                //lineLengthHint += 10; // Average line length was larger.
                bytesCount += lineLengthHint * 4; // Try with more.
            }
        } finally {
            in.close();
        }
    }
}
