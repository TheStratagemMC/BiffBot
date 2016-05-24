package com.thestratagemmc.biff.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.parser.ParserException;

import java.util.*;

/**
 * Created by Axel on 1/31/2016.
 */
public class BiffUser {
    private String name;
    private UUID biffId;
    private UUID minecraftId;
    private List<String> knownNicknames;
    private Map<String,String> otherAccountNames;

    public BiffUser(String name, UUID biffId, UUID minecraftId, List<String> knownNicknames, Map<String, String> otherAccountNames) {
        this.biffId = biffId;
        this.name = name;
        this.minecraftId = minecraftId;
        this.knownNicknames = knownNicknames;
        this.otherAccountNames = otherAccountNames;
    }

    public BiffUser(String name, UUID biffId, UUID minecraftId) {
        this.name = name;
        this.biffId = biffId;
        this.minecraftId = minecraftId;
        knownNicknames = new ArrayList<>();
        otherAccountNames = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public UUID getBiffId() {
        return biffId;
    }

    public UUID getMinecraftId() {
        return minecraftId;
    }

    public Player getMinecraftPlayer(){
        Player p = Bukkit.getPlayer(minecraftId);
        return p;
    }
    public List<String> getKnownNicknames() {
        return knownNicknames;
    }

    public Map<String, String> getOtherAccountNames() {
        return otherAccountNames;
    }

    public JSONObject writeToJSON(){
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("minecraftId", minecraftId.toString());
        obj.put("biffId", biffId.toString());
        JSONArray nicks = new JSONArray();
        nicks.addAll(knownNicknames);
        JSONObject otherAccounts = new JSONObject();
        otherAccounts.putAll(otherAccountNames);
        obj.put("knownNicknames", nicks);
        obj.put("otherAccountNames", otherAccounts);
        return obj;
    }

    public static BiffUser fromJSONString(String jsonString) throws Exception{
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject)parser.parse(jsonString);

        String name = (String)obj.get("name");
        UUID minecraftId = UUID.fromString((String)obj.get("minecraftId"));
        UUID biffId = UUID.fromString((String)obj.get("biffId"));
        List<String> knownNicknames = new ArrayList<>();
        for (Object o : ((JSONArray)obj.get("knownNicknames")).toArray()){
            knownNicknames.add((String)o);
        }
        Map<String,String> otherAccountNames = new HashMap<>();
        JSONObject map = (JSONObject)obj.get("otherAccountNames");
        for (Object entry : map.entrySet()){
            Map.Entry e = (Map.Entry)entry;
            otherAccountNames.put((String)e.getKey(), (String)e.getValue());
        }

        return new BiffUser(name, biffId, minecraftId, knownNicknames, otherAccountNames);
    }
}
