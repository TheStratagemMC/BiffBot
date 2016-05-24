package com.thestratagemmc.biffbot.api;

import com.thestratagemmc.biff.event.BiffStimuliEvent;

/**
 * Created by Axel on 2/13/2016.
 */
public interface StimuliExecutor {
    public void stimuli(BiffStimuliEvent event, String wordGroup);
}
