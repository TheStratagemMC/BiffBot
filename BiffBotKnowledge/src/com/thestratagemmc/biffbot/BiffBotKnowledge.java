package com.thestratagemmc.biffbot;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.thestratagemmc.biff.api.BiffConviction;
import com.thestratagemmc.biff.api.BiffTense;
import com.thestratagemmc.biff.api.BiffTestfor;
import com.thestratagemmc.biff.api.QuestionType;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Axel on 2/9/2016.
 */
public class BiffBotKnowledge extends JavaPlugin implements Listener {
    private String key;
    private String name = "GoogleKnowledgeGraphLookup";
    private JSONParser parser = new JSONParser();

    public void onEnable(){
        getServer().getPluginManager().registerEvents(this, this);

        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File keyFile =new File(getDataFolder(), "google-api-key.txt");
        try{
            if (!keyFile.exists()){
                keyFile.createNewFile();
            }
            else{
                key = Joiner.on("").join(Files.readLines(keyFile, Charset.defaultCharset()));
        }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @EventHandler
    public void stimuli(BiffStimuliEvent event){
        BiffTestfor testfor = new BiffTestfor(event);
        if (testfor.isDefinitelyNotTense(BiffTense.PRESENT)) return;
        if (!testfor.isQuestion()) return;
        if (testfor.isQuestionType(QuestionType.WHO, false) || testfor.isQuestionType(QuestionType.INFO_ABOUT, false)){
            List<String> wordsAfter = testfor.getWordsAfter("what's/whats/who's/whos/is");
            if (wordsAfter.size() == 0) return;

            String searchQuery = Joiner.on(" ").join(wordsAfter);
            String search = URLEncoder.encode(searchQuery);
           // Bukkit.broadcast("kek","kek");

            String path = "https://kgsearch.googleapis.com/v1/entities:search?query="+search+"&key="+key+"&limit=1&indent=True";
            try{
                URL url = new URL(path);
                URLConnection conn = url.openConnection();

                List<String> lines = new ArrayList<>();
                // open the stream and put it into BufferedReader
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    lines.add(inputLine);
                }

                br.close();

                String output = Joiner.on("\n").join(lines);
                JSONObject obj = (JSONObject)parser.parse(output);
                JSONArray list = (JSONArray)obj.get("itemListElement");
                List<GKResult> results = new ArrayList<>();
                for (Object o : list){
                    JSONObject _o = (JSONObject)o;
                    results.add(GKResult.fromJson(_o));
                }

                GKResult topResult = null;
                for (GKResult result : results){
                    if (topResult == null) topResult = result;
                    else{
                        if (topResult.getScore() < result.getScore()) topResult = result;
                    }
                }

                if (topResult.getScore() < 200) return;

                TextComponent component = new TextComponent(topResult.getDetailedDescription()+" ");
                component.setColor(ChatColor.GRAY);
                ComponentBuilder cb = new ComponentBuilder(topResult.getName()).bold(true).append("\n");

                if (topResult.getDescription() != null){
                    cb = cb.append(topResult.getDescription()).bold(false);
                }
                cb = cb.append("\n")
                .color(ChatColor.GRAY).append(Joiner.on(", ").join(topResult.getTypes())).color(ChatColor.LIGHT_PURPLE).italic(true).append("\n").append("Result score: "+topResult.getScore()).italic(false).color(ChatColor.GREEN);

                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cb.create()));
                TextComponent readMore = new TextComponent("<Read More>");
                readMore.setBold(true);
                readMore.setColor(ChatColor.WHITE);
                readMore.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, topResult.getLink()));
                readMore.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Opens Wikipedia Link").create()));
                component.addExtra(readMore);

                File file = new File(getDataFolder(), "kek1.txt");
                if (!file.exists()){
                    file.createNewFile();
                    String out = ComponentSerializer.toString(component);
                    FileOutputStream stream = new FileOutputStream(file);
                    stream.write(out.getBytes());
                    stream.close();
                }
                event.respond(name, component, BiffConviction.PRETTY_SURE);
            }catch(Exception e){
                e.printStackTrace();
            }

        }



    }
}
