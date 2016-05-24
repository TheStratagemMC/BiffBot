package com.thestratagemmc.biffbot.api;

import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.BiffConviction;
import com.thestratagemmc.biff.event.BiffListener;
import com.thestratagemmc.biff.event.BiffStimuliEvent;

import java.util.HashMap;

/**
 * Created by Axel on 2/13/2016.
 */
public class Biff2Listener extends BiffListener {
    protected HashMap<String,StimuliExecutor> executorMap = new HashMap<>();
    protected String name;
    public Biff2Listener(BiffPlugin plugin, String name) {
        super(plugin);
        this.name = name;
    }

    public void register(String display, StimuliExecutor executor){
        for (String string : display.split("/")){
            executorMap.put(display.toLowerCase(), executor);
        }
    }

    public StimuliExecutor getExecutor(String display){
        if (executorMap.containsKey(display.toLowerCase())) return executorMap.get(display.toLowerCase());
        return null;
    }

    public void defaultExecutorLogic(BiffStimuliEvent event, String targetWord){
        StimuliExecutor exec = getExecutor(targetWord);
        if (exec != null){
            exec.stimuli(event, targetWord);
            return;
        }
        event.respond(name, plugin.getMessages().getRandom("unsureIfShouldRespond"), BiffConviction.SOMEWHAT_SURE);
    }
}
