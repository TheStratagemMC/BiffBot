package com.thestratagemmc.biff.api;

import com.thestratagemmc.biff.BiffPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Axel on 1/31/2016.
 */
public class NETBuilder {
    private String name;
    private String displayNameTag;
    private List<NamedEntityAttribute> needToApply = new ArrayList<>();
    private List<String> keywords = new ArrayList<>();

    public NETBuilder(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void registerDisplayName(String name, String... register){
        displayNameTag = name;
        BiffPlugin.getPlugin().getMessages().registerDefault(name, register);
    }

    public void setDisplayNameTag(String displayNameTag) {
        this.displayNameTag = displayNameTag;
    }

    public void setNEA(NamedEntityAttribute... attr){
        needToApply.addAll(Arrays.asList(attr));
    }

    public void setKeywords(String... keywords){
        this.keywords.addAll(Arrays.asList(keywords));
    }

    public NamedEntityType get(){
        return new NamedEntityType(name, displayNameTag, needToApply, keywords);
    }
}
