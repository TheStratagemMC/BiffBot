package com.thestratagemmc.biffbot.listener;

import com.google.common.base.Joiner;
import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.BiffConviction;
import com.thestratagemmc.biff.api.BiffTestfor;
import com.thestratagemmc.biff.event.BiffListener;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Axel on 2/9/2016.
 */
public class LinkOpener extends BiffListener {
    private static final Pattern TITLE_TAG =
            Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE| Pattern.DOTALL);

    public LinkOpener(BiffPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void stimuli(BiffStimuliEvent event) {
        BiffTestfor testfor = new BiffTestfor(event);

        List<String> results = plugin.getBrain().findIpAddressesOrWebsites(event.getRequest().getOriginalMessage());
        //Bukkit.broadcast(""+results.size(), ""+results.size());
        if (results.size() == 0) return;
        Map<String,String> titles = new HashMap<>();
        for (String string : results) {
            String url = string;
            if (!url.contains("http://") && !url.contains("https://")) {
                url = "http://" + url;
            }

            try {
                String title = TitleExtractor.getPageTitle(url);
                if (title == null) continue;
                if (title.isEmpty()) continue;
                if (title.length() < 4) continue;
                titles.put(url, title);
            }catch(Exception e){
                e.printStackTrace();
            }
        }


        String output = (event.getSender().getName()+"'s link"+(titles.size() > 1 ? "s": "")+": ");
        List<String> otherOutput = new ArrayList<>();
        for (Map.Entry<String,String> entry : titles.entrySet()){
            if (entry.getValue() == null || entry.getKey() == null) continue;
            if (entry.getValue().isEmpty() || entry.getKey().isEmpty()) continue;
            otherOutput.add(entry.getKey() +" - "+entry.getValue());
        }

        output = output + Joiner.on(", ").join(otherOutput);
        event.respond("LinkOpener", output, BiffConviction.PRETTY_SURE);
    }

}
