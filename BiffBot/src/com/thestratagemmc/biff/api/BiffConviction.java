package com.thestratagemmc.biff.api;

import com.thestratagemmc.biff.BiffPlugin;

/**
 * Created by Axel on 1/29/2016.
 */
public enum BiffConviction {
    COMPLETELY_SURE("completelySure", 10),
    PRETTY_SURE("prettySure", 8),
    SOMEWHAT_SURE("somewhatSure", 7),
    MAYBE("maybe", 5),
    POSSIBLY("possibly", 4),
    DOUBT_IT("doubtIt", 3),
    DEFINITELY_NOT("definitelyNot", 0);

    /*
    TODO:
    begin logging conversations so that bot can see where to improve and look back on conversations
    internally ask questions about what behavior should change
    "learners" folder--where successful questions that have been asked and verified will go for their word choice

    -----> store a Map<String,Word> object in BiffSolution? then whenever they are done some task will run biff statistics on them
    begin storing information about players and do name lookups, nicknames

    development mode where the messages don't save, get overriden each time

    make biff more friendly & store information about conversation with biff. i.e.
    me: how many players are on tsmc?
    biff: what is the ip address to tsmc?
    me: tsmc.pw
    biff: 6 players are online tsmc

    1. need to be able to call the final (serverAmount)
    2. need to figureo ut what question to ask
    3. need to store what "tsmc" is in memory
    -- this will probably be all fake and not real machine learning

    limit biff's rate of responses

    chat moderator

    store information in biff about non-chat related things (joins, leaves, commands, etc)

    google knowledge graph

    remote biff

    biff pack

     */
    private String tag;
    private int weight;
    private BiffConviction(String tag, int weight){
        this.tag = tag;
        this.weight = weight;
    }

    public String getTag(){
        return tag;
    }

    public int getWeight(){
        return weight;
    }

    static{
        r("completelySure","I am completely sure.", "Definitely!");
        r("prettySure","I mean, I'm pretty sure.","I think so.");
        r("somewhatSure","I'm somewhat sure...", "I think so, but maybe not?");
        r("maybe","Maybe.", "I don't really know.");
        r("possibly", "Possibly!", "It's possible.");
        r("doubtIt", "Ehhh, I doubt it.", "I sort of doubt it.");
        r("definitelyNot", "Definitely not.");
    }
    private static final void r(String name, String... defaults){
        BiffPlugin.getPlugin().getMessages().registerDefault(name, defaults);
    }

}
