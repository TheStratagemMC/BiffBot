package com.thestratagemmc.biffbot;

import java.util.List;

/**
 * Created by Axel on 2/21/2016.
 */
public class RSSFeed {
    private String title;
    private String description;
    private String link;
    private List<RSSItem> items;

    public RSSFeed(String title, String description, String link, List<RSSItem> items) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<RSSItem> getItems() {
        return items;
    }

    public void setItems(List<RSSItem> items) {
        this.items = items;
    }
}
