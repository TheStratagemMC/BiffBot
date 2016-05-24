package com.thestratagemmc.biffbot;

import com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axel on 2/21/2016.
 */
public class BiffRSS extends JavaPlugin {
    private final RSSParser parser = new RSSParser();
    private final List<String> urls = new ArrayList<>();



    public void onEnable(){
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File file = new File(getDataFolder(), "feeds.txt");
        try{
            if (!file.exists()){
                file.createNewFile();
                FileOutputStream stream = new FileOutputStream(file);
                stream.write("https://mojang.com/feed.xml".getBytes());
                stream.close();
                urls.add("https://mojang.com/feed.xml");
            }
            else{
                List<String> lines = Files.readLines(file, Charset.defaultCharset());
                urls.clear();
                urls.addAll(lines);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                try{
                    for (String string : urls){
                        RSSFeed feed = parser.getFeed(string);

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }


            }
        }, 100l, 100l);
    }
}
