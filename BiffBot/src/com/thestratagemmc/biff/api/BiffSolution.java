package com.thestratagemmc.biff.api;

import com.thestratagemmc.biff.event.BiffSpeakEvent;
import com.thestratagemmc.biff.history.ConversationMessage;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Axel on 1/29/2016.
 */
public class BiffSolution {
    public String solutionTag;
    public BiffConviction conviction;
    public String descriptionTag;
    public BiffSolutionHandler handler;
    public Map<String,Word> wordMap;
    public ConversationMessage message; //message to be added if executed
    public UUID with;

    public BiffSolution(String solutionTag, BiffConviction conviction, String descriptionTag, BiffSolutionHandler handler, Map<String,Word> wordMap, ConversationMessage message, UUID with) {
        this.solutionTag = solutionTag;
        this.conviction = conviction;
        this.descriptionTag = descriptionTag;
        this.handler = handler;
        this.wordMap = wordMap;
        this.message = message;
        this.with = with;
    }

    public BiffSpeakEvent getEvent(MessageHandler messageHandler){
        return new BiffSpeakEvent(false, solutionTag, descriptionTag, conviction, handler, wordMap, message, with, messageHandler);
    }
}
