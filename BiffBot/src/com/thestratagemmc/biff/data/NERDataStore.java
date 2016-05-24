package com.thestratagemmc.biff.data;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.NamedEntity;
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
 * Created by Axel on 1/31/2016.
 */
public class NERDataStore{
    protected File workingDirectory;
    protected HashMap<String,NamedEntity> storedEntities = new HashMap<>();

    public void init(BiffPlugin plugin){
        workingDirectory = new File(plugin.getDataFolder(), getDefaultDirectory());
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        if (!workingDirectory.exists()) workingDirectory.mkdir();

        loadStoredFiles();
    }

    public String getDefaultDirectory(){
        return "ner";
    }
    public void loadStoredFiles(){
        storedEntities.clear();
        for (File file : workingDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        })){
            try{

                String input = Joiner.on("\n").join(Files.readLines(file, Charset.defaultCharset()));
                NamedEntity entity = NamedEntity.fromJSON(input);
                storedEntities.put(entity.getTitle(), entity);
            }catch(Exception e){
                Bukkit.getLogger().warning("Error when loading named entity file: "+file.getName());
                e.printStackTrace();
            }
        }
    }

    public void register(NamedEntity entity){
        File file = new File(workingDirectory, entity +".txt");
        if (!file.exists()){
            try{
                file.createNewFile();
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(entity.writeToString().toJSONString().getBytes());
                fout.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        storedEntities.put(entity.getTitle(), entity);
    }

    public NamedEntity get(String name){
        if (!storedEntities.containsKey(name)) return null;
        return storedEntities.get(name);
    }


    public boolean exists(String name){
        return storedEntities.containsKey(name);
    }
}
