package com.thestratagemmc.biff.api;

import com.thestratagemmc.biff.BiffPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Axel on 1/31/2016.
 */
public class NamedEntity {
    private String type;
    private String title;
    private String displayNamesTag;
    private Map<String,String> attributes;
    private List<String> keywords;

    public NamedEntity(String title, String displayNamesTag, String type, Map<String, String> attributes, List<String> keywords) {
        this.type = type;
        this.title = title;
        this.displayNamesTag = displayNamesTag;
        this.attributes = attributes;
        this.keywords = keywords;
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getTitle() {
        return title;
    }

    public String getDisplayNamesTag(){
        return displayNamesTag;
    }

    public List<String> getDisplayNames(){
        return BiffPlugin.getPlugin().getMessages().get(displayNamesTag);
    }
    public JSONObject writeToString(){
        JSONObject ent = new JSONObject();
        ent.put("title", title);
        ent.put("type", type);
        JSONArray keywords = new JSONArray();
        keywords.addAll(keywords);
        ent.put("keywords", keywords);
        ent.put("displayNamesTag", displayNamesTag);
        JSONObject attr = new JSONObject();
        for (Map.Entry<String,String> entry : attributes.entrySet()){
            attr.put(entry.getKey(), entry.getValue());
        }
        ent.put("attributes", attr);
        return ent;
    }
    public static NamedEntity fromJSON(String jsonString) throws Exception{
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject)parser.parse(jsonString);
        JSONArray kw = (JSONArray)obj.get("keywords");
        List<String> keywords = new ArrayList<>();
        for (Object object : kw){
            keywords.add((String)object);
        }

        Map<String,String> map = new HashMap<>();
        JSONObject attr = (JSONObject)obj.get("attributes");
        for (Object entry : attr.entrySet()){
            Map.Entry e  = (Map.Entry)entry;
            map.put((String)e.getKey(), (String)e.getValue());
        }

        return new NamedEntity((String)obj.get("title"), (String)obj.get("displayNamesTag"), (String)obj.get("type"), map, keywords);
    }
}
