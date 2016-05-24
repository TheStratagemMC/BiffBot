package com.thestratagemmc.biff.history;

/**
 * Created by Axel on 1/31/2016.
 */
public abstract class ConversationMessage {
    private Chatter chatter;
    private String message;
    private IntentionType intentionType;
    private long time;

    public ConversationMessage(Chatter chatter, String message, IntentionType type, long time) {
        this.intentionType = type;
        this.chatter = chatter;
        this.message = message;
        this.time = time;
    }


    public Chatter getChatter() {
        return chatter;
    }

    public String getMessage() {
        return message;
    }

    public IntentionType getIntentionType() {
        return intentionType;
    }

    public long getTime() {
        return time;
    }
    public abstract Intention getIntention();
}
