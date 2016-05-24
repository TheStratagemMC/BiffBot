package com.thestratagemmc.biff.history;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by Axel on 1/31/2016.
 */
public class Conversation {
    private UUID sender; //by biff id, not minecraft
    private List<ConversationMessage> messageHistory = Collections.synchronizedList(new ArrayList<ConversationMessage>());
    public Conversation(UUID sender) {
        this.sender = sender;
    }

    public void log(ConversationMessage message){
        messageHistory.add(message);
    }

    public void add(ConversationMessage message){
        messageHistory.add(message);
    }

    public List<ConversationMessage> getConversation(){
        return messageHistory;
    }

    public Intention getLastBiffIntention(){
        ConversationMessage lastMessage = null;
        if (messageHistory.size() > 1){

            for (int i = messageHistory.size() - 1; i >= 0; i--){
                lastMessage = messageHistory.get(i);
                //Bukkit.broadcastMessage("d"+i);
                if (lastMessage.getChatter() == Chatter.BIFF) return lastMessage.getIntention();
            }
            //lastMessage = messageHistory.get(messageHistory.size() - 1);
            if (lastMessage != null)return lastMessage.getIntention();
        }
        return null;
    }

    public IntentionType getLastBiffIntentionType(){
        ConversationMessage lastMessage = null;
        if (messageHistory.size() > 1){

            for (int i = messageHistory.size() - 1; i >= 0; i--){
                lastMessage = messageHistory.get(i);
                //Bukkit.broadcastMessage("d"+i);
                if (lastMessage.getChatter() == Chatter.BIFF) return lastMessage.getIntentionType();
            }
            //lastMessage = messageHistory.get(messageHistory.size() - 1);
            if (lastMessage != null) return lastMessage.getIntentionType();
        }
        return null;
    }
    public String getLastUserMessage(){
        ConversationMessage lastMessage = null;
        if (messageHistory.size() > 1){

            for (int i = messageHistory.size() - 1; i >= 0; i--){
                lastMessage = messageHistory.get(i);
                //Bukkit.broadcastMessage("d"+i);
                if (lastMessage.getChatter() == Chatter.SENDER) return lastMessage.getMessage();
            }
            //lastMessage = messageHistory.get(messageHistory.size() - 1);
            if (lastMessage != null) return lastMessage.getMessage();
        }
        return null;
    }
}
