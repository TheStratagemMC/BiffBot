package com.thestratagemmc.biff.data;

import com.google.common.io.Files;
import com.thestratagemmc.biff.BiffPlugin;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Axel on 1/29/2016.
 */
public abstract class BiffDataHandler {
    protected File workingDirectory;
    protected HashMap<String,List<String>> storedMessages = new HashMap<>();


    public void init(BiffPlugin plugin){
        workingDirectory = new File(plugin.getDataFolder(), getDefaultDirectory());
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        if (!workingDirectory.exists()) workingDirectory.mkdir();

        loadStoredFiles();
    }


    public abstract String getDefaultDirectory();
    public void loadStoredFiles(){
        storedMessages.clear();
        for (File file : workingDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        })){
            try{
                String key = FilenameUtils.removeExtension(file.getName());
                List<String> lines = Files.readLines(file, Charset.defaultCharset());
                storedMessages.put(key, lines);
            }catch(Exception e){
                Bukkit.getLogger().warning("Error when loading message file: "+file.getName());
                e.printStackTrace();
            }
        }
    }

    public void registerDefault(String name, String... defaults){
        File file = new File(workingDirectory, name +".txt");
        if (!file.exists()){
            try{
                file.createNewFile();
                FileOutputStream fout = new FileOutputStream(file);
                for (String line : defaults){
                    fout.write(line.getBytes());
                }
                fout.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        storedMessages.put(name, Arrays.asList(defaults));
    }

    public List<String> get(String name){
        if (!storedMessages.containsKey(name)) return null;
        return storedMessages.get(name);
    }

    public String getFirst(String name){
        if (!storedMessages.containsKey(name)) return null;
        return storedMessages.get(name).get(0);
    }

    public String getRandom(String name){
        if (!storedMessages.containsKey(name)) return null;

        List<String> messages = storedMessages.get(name);
        int pick = ThreadLocalRandom.current().nextInt(messages.size());
        return messages.get(pick);
    }
}
