package com.thestratagemmc.biff.api;

import com.thestratagemmc.biff.BiffBrain;
import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.history.Chatter;
import com.thestratagemmc.biff.history.ConversationMessage;
import com.thestratagemmc.biff.history.Intention;
import com.thestratagemmc.biff.history.IntentionType;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by Axel on 1/29/2016.
 */
public class BiffRequest { /* Represents one chat message/request, can be multiple sentences */
    private static final Pattern punctuationRegex = Pattern.compile("[\\Q][(){},.;!?<>%\\E]");
    private String originalMessage;
    private String[] words;
    private Map<String,Word> wordMap = new HashMap<>(); //for speed
    private BiffPlugin plugin;
    private UUID requestId;

    public BiffRequest(BiffPlugin plugin, String message){
        this.plugin = plugin;
        originalMessage = message;
        words = punctuationRegex.matcher(message).replaceAll("").split("\\s+");
        for (int i = 0; i < words.length; i++){
            String word = words[i].toLowerCase();
            wordMap.put(word, new Word(i, word));  //for logic
        }
        requestId = UUID.randomUUID();
    }

    public BiffConviction matches(BiffTestfor testfor){
        return testfor.matches();
    }

    public void runSolution(String moduleTag, String description, BiffConviction conviction, String response, BiffSolutionHandler solution, UUID with){
        ConversationMessage msg = new ConversationMessage(Chatter.BIFF, response, IntentionType.RESPONSE,System.currentTimeMillis()) {
            @Override
            public Intention getIntention() {
                return new Intention(IntentionType.RESPONSE);
            }
        };
        runSolution(moduleTag, description, conviction, solution, msg, with);
    }



    public void runSolution(String moduleTag, String description, BiffConviction conviction, BiffSolutionHandler solution, ConversationMessage message, UUID with){
        BiffSolution s = new BiffSolution(moduleTag, conviction, description, solution, wordMap, message, with);
        plugin.getSolutionsManager().addSolution(requestId, s);

        //Bukkit.broadcastMessage("ran: "+message.getIntention().getIntention());
    }


    public UUID getId(){
        return requestId;
    }

    public String getOriginalMessage(){
        return originalMessage;
    }

    public Map<String, Word> getWordMap(){
        return wordMap;
    }

    public String[] getWordArray(){
        return words;
    }
}
