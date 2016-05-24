package com.thestratagemmc.biffbot;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axel on 2/21/2016.
 */
public class RSSParser {


    public RSSFeed getFeed(String url) throws Exception{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new URL(url).openStream());


        Node channel = doc.getElementsByTagName("channel").item(0);
        String _title = null;
        String _link = null;
        String _description = null;
        List<RSSItem> items = new ArrayList<>();

        for (Node n = channel.getFirstChild(); n != null; n = n.getNextSibling()){
            if (n.getNodeName().equalsIgnoreCase("title")) _title = n.getTextContent();
            else if (n.getNodeName().equalsIgnoreCase("link")) _link = n.getTextContent();
            else if (n.getNodeName().equalsIgnoreCase("description")) _description = n.getTextContent();
            else if (n.getNodeName().equalsIgnoreCase("item")){
                String title = null;
                String description = null;
                String link = null;
                String date = null;
                for (Node i = n.getFirstChild(); i != null; i = i.getNextSibling()){
                    if (i.getNodeName().equalsIgnoreCase("title")) title = i.getTextContent();
                    else if (i.getNodeName().equalsIgnoreCase("description")) description = i.getTextContent();
                    else if (i.getNodeName().equalsIgnoreCase("link")) link = i.getTextContent();
                    else if (i.getNodeName().equalsIgnoreCase("pubDate")) date = i.getTextContent();
                }

                RSSItem item = new RSSItem(title, description, link, date);
                items.add(item);
            }
        }

        RSSFeed feed = new RSSFeed(_title, _link, _description, items);
        return feed;
    }

}
