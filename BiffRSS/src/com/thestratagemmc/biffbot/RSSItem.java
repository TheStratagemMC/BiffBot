package com.thestratagemmc.biffbot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Axel on 2/21/2016.
 */
public class RSSItem {
    private String title;
    private String description;
    private Date date;
    private String link;
    private static final DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public RSSItem(String title, String description, String link, String d) {
        this.title = title;
        this.description = description;
        this.link = link;
        try{
            date = format.parse(d);
        }catch(Exception e){
            e.printStackTrace();
        }
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
