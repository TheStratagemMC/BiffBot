package com.thestratagemmc.biff.data;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.BiffUser;
import com.thestratagemmc.biff.api.NamedEntity;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Axel on 1/31/2016.
 */
public class BiffUserStore {
    private HashMap<UUID,BiffUser> biffUserMap = new HashMap<>(); //UUID IS NOT MINECRAFT!
    private HashMap<UUID,UUID> minecraftToBiffMap = new HashMap<>();
    private Hashtable<String,UUID> index = new Hashtable<>();
    protected File workingDirectory;


    public void init(BiffPlugin plugin){
        workingDirectory = new File(plugin.getDataFolder(), getDefaultDirectory());
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        if (!workingDirectory.exists()) workingDirectory.mkdir();

        loadStoredFiles();
    }

    public String getDefaultDirectory(){
        return "users";
    }
    public void loadStoredFiles(){
        biffUserMap.clear();
        for (File file : workingDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        })){
            try{

                String input = Joiner.on("\n").join(Files.readLines(file, Charset.defaultCharset()));
                BiffUser user = BiffUser.fromJSONString(input);
                biffUserMap.put(user.getBiffId(), user);
                minecraftToBiffMap.put(user.getMinecraftId(), user.getBiffId());
                index.put(user.getName().toLowerCase(), user.getBiffId());
                for (String nickname : user.getKnownNicknames()){
                    index.put(nickname.toLowerCase(), user.getBiffId());
                }
                for (String value : user.getOtherAccountNames().values()){
                    index.put(value.toLowerCase(), user.getBiffId());
                }


            }catch(Exception e){
                Bukkit.getLogger().warning("Error when loading user file: "+file.getName());
                e.printStackTrace();
            }
        }
    }

    public void update(BiffUser user){

        File file = new File(workingDirectory, user.getBiffId().toString()+".txt");

        try{
            if (file.exists()) file.delete();
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file);
            fout.write(user.writeToJSON().toJSONString().getBytes());
            fout.close();
        }catch(Exception e){
            e.printStackTrace();
        }


        biffUserMap.put(user.getBiffId(), user);
        minecraftToBiffMap.put(user.getMinecraftId(), user.getBiffId());
    }

    public BiffUser get(UUID name){
        if (!biffUserMap.containsKey(name)) return null;
        return biffUserMap.get(name);
    }

    public BiffUser getByMinecraft(UUID id){
        if (!minecraftToBiffMap.containsKey(id)) return null;
        UUID _id = minecraftToBiffMap.get(id);
        if (biffUserMap.containsKey(_id)){
            return biffUserMap.get(_id);
        }
        return null;
    }
    public void register(BiffUser user){
        File file = new File(workingDirectory, user.getBiffId().toString()+".txt");
        if (!file.exists()){
            try{
                file.createNewFile();
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(user.writeToJSON().toJSONString().getBytes());
                fout.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        biffUserMap.put(user.getBiffId(), user);
        minecraftToBiffMap.put(user.getMinecraftId(), user.getBiffId());
    }

    public boolean exists(UUID name){
        return biffUserMap.containsKey(name);
    }

    public BiffUser getUserOnIndex(String key){
        if (index.containsKey(key.toLowerCase())) return biffUserMap.get(index.get(key));
        return null;
    }
}
