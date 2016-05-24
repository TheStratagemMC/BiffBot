package com.thestratagemmc.biff.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axel on 1/31/2016.
 */
public class NamedEntityType {
    private String name;
    private String displayNameTag;
    private List<NamedEntityAttribute> needToApply;
    private List<String> keywords = new ArrayList<>();

    public NamedEntityType(String name, String displayNameTag, List<NamedEntityAttribute> needToApply, List<String> keywords) {
        this.name = name;
        this.displayNameTag = displayNameTag;
        this.needToApply = needToApply;
        this.keywords = keywords;
    }

    public String getName() {
        return name;
    }

    public String getDisplayNameTag() {
        return displayNameTag;
    }

    public List<NamedEntityAttribute> getNeedToApply() {
        return needToApply;
    }

    public List<String> getKeywords() {
        return keywords;
    }
}
