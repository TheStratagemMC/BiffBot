package com.thestratagemmc.biff.history;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Axel on 1/31/2016.
 */
public class Intention{
    private Map<String,String> information = new HashMap<>();
    private IntentionType intention;

    public Intention(IntentionType intention) {
        this.intention = intention;
    }

    public Map<String, String> getInformation() {
        return information;
    }

    public IntentionType getIntention() {
        return intention;
    }

}
