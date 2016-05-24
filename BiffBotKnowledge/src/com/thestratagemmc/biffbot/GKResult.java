package com.thestratagemmc.biffbot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axel on 2/9/2016.
 */
public class GKResult {

    private String name;
    private List<String> types;
    private String description;
    private String detailedDescription;
    private String link;
    private double score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public static GKResult fromJson(JSONObject o){
        GKResult gk = new GKResult();
        JSONObject result = (JSONObject)o.get("result");
        gk.setName((String)result.get("name"));
        List<String> types = new ArrayList<>();
        JSONArray _types = (JSONArray)result.get("@type");
        for (Object _o : _types){
            types.add((String)_o);
        }
        gk.setTypes(types);
        gk.setDescription((String)result.get("description"));
        JSONObject dD = (JSONObject)result.get("detailedDescription");
        gk.setDetailedDescription((String)dD.get("articleBody"));
        gk.setLink((String)dD.get("url"));
        gk.setScore((double)o.get("resultScore"));
        return gk;
    }
}
