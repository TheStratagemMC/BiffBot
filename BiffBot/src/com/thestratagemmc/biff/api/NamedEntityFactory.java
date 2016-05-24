package com.thestratagemmc.biff.api;

import com.thestratagemmc.biff.BiffPlugin;

import java.util.*;

/**
 * Created by Axel on 1/31/2016.
 */
public class NamedEntityFactory {
    private String title;
    private NamedEntityType type;
    private Map<String, NamedEntityAttribute> needToApply;
    private UUID conversationHolder;
    private Map<String,String> attributes = new HashMap<>();
    private LearnCallback learnCallback;
    private List<String> keywords = new ArrayList<>();

    public NamedEntityFactory(String title, NamedEntityType type, UUID conversationHolder, LearnCallback callback) {
        this.title = title;
        this.type = type;
        this.conversationHolder = conversationHolder;
        needToApply = new HashMap<>();
        keywords.clear();
        keywords.addAll(type.getKeywords());
        for (NamedEntityAttribute attr : type.getNeedToApply()){
            needToApply.put(attr.getName(), attr);
        }
        this.learnCallback = callback;
    }

    public LearnCallback getLearnCallback() {
        return learnCallback;
    }

    public String getTitle() {
        return title;
    }

    public void setAttributes(String key, String value){
        attributes.put(key, value);
    }

    public NamedEntityAttribute nextAttribute(){
        if (needToApply.size() == 0){
            return null; //signals ready to create NE
        }
        String firstKey = needToApply.keySet().iterator().next();
        return needToApply.get(firstKey);
    }

    public void doneWith(String key){
        needToApply.remove(key);
    }

    public NamedEntityAttribute getAttribute(String key){
        return needToApply.get(key);
    }

    public NamedEntityType getType(){
        return type;
    }

    public NamedEntity get(){
        BiffPlugin.getPlugin().getMessages().registerDefault(title, title);
        return new NamedEntity(title, title, type.getName(),attributes,keywords);
    }

}
